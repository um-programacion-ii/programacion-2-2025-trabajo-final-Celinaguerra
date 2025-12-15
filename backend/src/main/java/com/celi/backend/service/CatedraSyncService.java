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
        String url = applicationProperties.getCatedra().getUrl() + "/endpoints/v1/eventos"; // Payload 4
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
}
