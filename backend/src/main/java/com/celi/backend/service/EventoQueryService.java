package com.celi.backend.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.celi.backend.domain.Evento;
import com.celi.backend.repository.EventoRepository;
import com.celi.backend.service.dto.EventoDetalleDTO;
import com.celi.backend.service.dto.EventoResumenDTO;
import com.celi.backend.service.dto.MapaAsientosDTO;
import com.celi.backend.service.mapper.EventoMapper;
import com.celi.backend.service.proxy.ProxyAsientosService;

/**
 * Servicio de consulta de eventos (listado y detalle).
 */
@Service
@Transactional(readOnly = true)
public class EventoQueryService {

    private static final Logger LOG = LoggerFactory.getLogger(EventoQueryService.class);

    private final EventoRepository eventoRepository;
    private final EventoMapper eventoMapper;
    private final ProxyAsientosService proxyAsientosService;

    public EventoQueryService(
            EventoRepository eventoRepository,
            EventoMapper eventoMapper,
            ProxyAsientosService proxyAsientosService) {
        this.eventoRepository = eventoRepository;
        this.eventoMapper = eventoMapper;
        this.proxyAsientosService = proxyAsientosService;
    }

    /**
     * Obtiene el listado de eventos activos (no cancelados y no expirados).
     */
    public List<EventoResumenDTO> obtenerEventosActivos() {
        Instant ahora = Instant.now();
        List<Evento> eventos = eventoRepository.findEventosActivos(ahora);
        return eventos.stream().map(eventoMapper::toResumenDTO).collect(Collectors.toList());
    }

    /**
     * Obtiene el detalle de un evento por ID, si está activo (no cancelado y no
     * expirado).
     * Incluye estadísticas de asientos bloqueados.
     */
    public Optional<EventoDetalleDTO> obtenerDetalleEvento(Long id) {
        Instant ahora = Instant.now();
        Optional<EventoDetalleDTO> detalleOpt = eventoRepository
                .findById(id)
                .filter(e -> Boolean.FALSE.equals(e.getCancelado()))
                .filter(e -> e.getFecha() == null || !e.getFecha().isBefore(ahora))
                .map(eventoMapper::toDetalleDTO);

        // No incluir asientos en el detalle del evento (no establecer la lista)

        return detalleOpt;
    }

    /**
     * Obtiene las dimensiones (filas y columnas) de un evento por su ID de cátedra.
     */
    public Optional<java.util.Map<String, Integer>> obtenerDimensionesEvento(Long eventoIdCatedra) {
        LOG.debug("Obteniendo dimensiones del evento con eventoIdCatedra: {}", eventoIdCatedra);

        return eventoRepository.findByEventoIdCatedra(eventoIdCatedra)
                .map(evento -> {
                    java.util.Map<String, Integer> dimensiones = new java.util.HashMap<>();
                    if (evento.getFilaAsiento() != null && evento.getColumnAsiento() != null) {
                        dimensiones.put("filas", evento.getFilaAsiento());
                        dimensiones.put("columnas", evento.getColumnAsiento());
                        LOG.debug("Dimensiones encontradas: {} filas x {} columnas",
                                evento.getFilaAsiento(), evento.getColumnAsiento());
                        return dimensiones;
                    } else {
                        LOG.warn("Evento con eventoIdCatedra={} no tiene dimensiones definidas", eventoIdCatedra);
                        return null;
                    }
                });
    }
}