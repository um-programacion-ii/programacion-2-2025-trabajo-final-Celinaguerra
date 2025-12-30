package com.celi.backend.service;

import com.celi.backend.config.ApplicationProperties;
import com.celi.backend.domain.Evento;
import com.celi.backend.domain.TipoEvento;
import com.celi.backend.domain.Integrante;
import com.celi.backend.repository.EventoRepository;
import com.celi.backend.repository.IntegranteRepository;
import com.celi.backend.repository.TipoEventoRepository;
import com.celi.backend.service.dto.catedra.CatedraEventoDTO;
import com.celi.backend.service.dto.catedra.CatedraIntegranteDTO;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@Transactional
public class CatedraSyncService {

    private final Logger log = LoggerFactory.getLogger(CatedraSyncService.class);

    private final RestTemplate restTemplate;
    private final ApplicationProperties applicationProperties;
    private final EventoRepository eventoRepository;
    private final TipoEventoRepository tipoEventoRepository;
    private final IntegranteRepository integranteRepository;

    public CatedraSyncService(
            RestTemplate restTemplate,
            ApplicationProperties applicationProperties,
            EventoRepository eventoRepository,
            TipoEventoRepository tipoEventoRepository,
            IntegranteRepository integranteRepository) {
        this.restTemplate = restTemplate;
        this.applicationProperties = applicationProperties;
        this.eventoRepository = eventoRepository;
        this.tipoEventoRepository = tipoEventoRepository;
        this.integranteRepository = integranteRepository;
    }

    public void syncEventos() {
        log.info("Iniciando sincronización de eventos con la cátedra...");
        String url = applicationProperties.getCatedra().getUrl() + "/eventos"; // Now goes through proxy
        String token = applicationProperties.getCatedra().getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // Debug: Fetch raw string first
            var responseString = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            String json = responseString.getBody();
            log.info("RAW JSON CATEDRA: {}", json);

            if (json != null) {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                        false);
                List<CatedraEventoDTO> eventosExternos = Arrays
                        .asList(mapper.readValue(json, CatedraEventoDTO[].class));

                processEventos(eventosExternos);
                log.info("Sincronización finalizada. {} eventos procesados.", eventosExternos.size());
            }
        } catch (Exception e) {
            log.error("Error al sincronizar eventos: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Synchronizes a single event from Cátedra API by its ID.
     * If the event exists in Cátedra, updates local database and returns the event
     * data.
     * 
     * @param catedraEventoId The ID of the event in Cátedra system
     * @return Optional containing the synchronized EventoDTO, or empty if not found
     */
    public Optional<Evento> syncSingleEvent(Long catedraEventoId) {
        log.info("Sincronizando evento individual desde Cátedra, ID: {}", catedraEventoId);
        String url = applicationProperties.getCatedra().getUrl() + "/evento/" + catedraEventoId;
        String token = applicationProperties.getCatedra().getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            var responseString = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            String json = responseString.getBody();
            log.debug("Response from Cátedra for event {}: {}", catedraEventoId, json);

            if (json != null) {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                        false);

                CatedraEventoDTO eventoDTO = mapper.readValue(json, CatedraEventoDTO.class);

                // Sync the event using existing logic
                TipoEvento tipoEvento = syncTipoEvento(eventoDTO);
                Evento evento = syncEvento(eventoDTO, tipoEvento);
                syncIntegrantes(eventoDTO, evento);

                log.info("Evento {} sincronizado correctamente", catedraEventoId);
                return Optional.of(evento);
            }

            return Optional.empty();
        } catch (Exception e) {
            log.warn("No se pudo sincronizar evento {} desde Cátedra: {}", catedraEventoId, e.getMessage());
            return Optional.empty();
        }
    }

    private void processEventos(List<CatedraEventoDTO> eventosExternos) {
        for (CatedraEventoDTO dto : eventosExternos) {
            // 1. Sync TipoEvento
            TipoEvento tipoEvento = syncTipoEvento(dto);

            // 2. Sync Evento
            Evento evento = syncEvento(dto, tipoEvento);

            // 3. Sync Integrantes
            syncIntegrantes(dto, evento);
        }
    }

    private TipoEvento syncTipoEvento(CatedraEventoDTO dto) {
        if (dto.getEventoTipo() == null)
            return null;

        String nombreTipo = dto.getEventoTipo().getNombre();
        if (nombreTipo == null) {
            nombreTipo = "SIN_NOMBRE";
        }

        final String nombreFinal = nombreTipo; // for lambda

        return tipoEventoRepository.findAll().stream()
                .filter(t -> t.getNombre() != null && t.getNombre().equalsIgnoreCase(nombreFinal))
                .findFirst()
                .orElseGet(() -> {
                    TipoEvento nuevo = new TipoEvento();
                    nuevo.setNombre(nombreFinal);
                    nuevo.setDescripcion(dto.getEventoTipo().getDescripcion());
                    return tipoEventoRepository.save(nuevo);
                });
    }

    private Evento syncEvento(CatedraEventoDTO dto, TipoEvento tipoEvento) {
        log.info("Procesando evento: {}, cols: {}, tipo: {}", dto.getTitulo(), dto.getColumnAsientos(),
                dto.getEventoTipo());

        Optional<Evento> existing = eventoRepository.findAll().stream()
                .filter(e -> e.getTitulo().equalsIgnoreCase(dto.getTitulo())) // Weak matching
                .findFirst();

        Evento evento = existing.orElse(new Evento());

        evento.setTitulo(dto.getTitulo());
        evento.setResumen(dto.getResumen());
        evento.setDescripcion(dto.getDescripcion());
        evento.setFecha(dto.getFecha());
        evento.setDireccion(dto.getDireccion());
        evento.setImagen(dto.getImagen());
        evento.setFilaAsientos(dto.getFilaAsientos());
        evento.setColumnAsientos(dto.getColumnAsientos());
        evento.setPrecioEntrada(dto.getPrecioEntrada());
        evento.setEventoTipo(tipoEvento);

        return eventoRepository.save(evento);
    }

    private void syncIntegrantes(CatedraEventoDTO dto, Evento evento) {
        // Clear existing integrations to prevent duplication (for ManyToMany, we clear
        // the association)
        // With ManyToMany, we can't just deleteAll or we lose the unique person.
        // We just clear the relationships for this event.
        if (evento.getIntegrantes() != null) {
            evento.getIntegrantes().clear();
        }

        if (dto.getIntegrantes() == null)
            return;

        for (CatedraIntegranteDTO intDto : dto.getIntegrantes()) {
            Integrante integrante = integranteRepository
                    .findFirstByNombreAndApellidoAndIdentificacion(intDto.getNombre(), intDto.getApellido(),
                            intDto.getIdentificacion())
                    .orElseGet(() -> {
                        Integrante newInt = new Integrante();
                        newInt.setNombre(intDto.getNombre());
                        newInt.setApellido(intDto.getApellido());
                        newInt.setIdentificacion(intDto.getIdentificacion());
                        return integranteRepository.save(newInt);
                    });

            evento.addIntegrantes(integrante);
        }
        eventoRepository.save(evento); // Save changes to the association
    }

    /**
     * Checks if a seat is available via Proxy (which checks Redis).
     * 
     * @param eventoId
     * @param fila
     * @param columna
     * @return true if available, false otherwise
     */
    public String getSeatStatus(Long eventoId, int fila, int columna) {
        String url = applicationProperties.getCatedra().getUrl().replace("/api/catedra", "")
                + "/api/asientos/" + eventoId + "/" + fila + "/" + columna;

        String token = applicationProperties.getCatedra().getToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            var response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            return response.getBody() != null ? response.getBody().toUpperCase() : "DESCONOCIDO";
        } catch (Exception e) {
            log.error("Error checking seat availability for event {} seat {}-{}: {}", eventoId, fila, columna,
                    e.getMessage());
            return "ERROR";
        }
    }

    /**
     * Executes the sale against the Cátedra API via Proxy.
     * 
     * @param eventoId
     * @param asientos  list of seats to buy
     * @param usuarioId ID of the user buying
     * @return true if successful
     */
    public boolean performSale(Long eventoId, java.util.Set<com.celi.backend.service.dto.AsientoDTO> asientos,
            Long usuarioId) {
        String url = applicationProperties.getCatedra().getUrl() + "/realizar-venta";

        String token = applicationProperties.getCatedra().getToken();

        // Construct payload
        // Expected format: {"eventoId":1, "asientos":[{"fila":1,"columna":3}],
        // "usuarioId":123}
        java.util.Map<String, Object> payload = new java.util.HashMap<>();
        payload.put("eventoId", eventoId);

        List<java.util.Map<String, Integer>> listaAsientos = new ArrayList<>();
        if (asientos != null) {
            for (com.celi.backend.service.dto.AsientoDTO a : asientos) {
                java.util.Map<String, Integer> asientoMap = new java.util.HashMap<>();
                asientoMap.put("fila", a.getFila());
                asientoMap.put("columna", a.getColumna());
                listaAsientos.add(asientoMap);
            }
        }
        payload.put("asientos", listaAsientos);
        payload.put("usuarioId", usuarioId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<java.util.Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        try {
            log.info("Sending sale request to Proxy: {}", payload);
            restTemplate.postForEntity(url, entity, String.class);
            return true;
        } catch (Exception e) {
            log.error("Error performing sale in Cátedra: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Sends a request to Cátedra (via Proxy) to block seats.
     * 
     * @param eventoId Event ID
     * @param asientos List of seats to block
     * @return true if successful
     */
    public boolean blockSeats(Long eventoId, List<com.celi.backend.service.dto.AsientoDTO> asientos) {
        String url = applicationProperties.getCatedra().getUrl() + "/bloquear-asientos";
        String token = applicationProperties.getCatedra().getToken();

        // Construct payload: {"eventoId": 1, "asientos": [{"fila":1, "columna":2}]}
        java.util.Map<String, Object> payload = new java.util.HashMap<>();
        payload.put("eventoId", eventoId);

        List<java.util.Map<String, Integer>> listaAsientos = new ArrayList<>();
        for (com.celi.backend.service.dto.AsientoDTO a : asientos) {
            java.util.Map<String, Integer> m = new java.util.HashMap<>();
            m.put("fila", a.getFila());
            m.put("columna", a.getColumna());
            listaAsientos.add(m);
        }
        payload.put("asientos", listaAsientos);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<java.util.Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        try {
            log.info("Sending block request to Proxy: {}", payload);
            restTemplate.postForEntity(url, entity, String.class);
            return true;
        } catch (Exception e) {
            log.error("Error blocking seats in Cátedra: {}", e.getMessage());
            return false;
        }
    }
}
