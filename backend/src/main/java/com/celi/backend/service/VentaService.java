package com.celi.backend.service;

import com.celi.backend.domain.Venta;
import com.celi.backend.repository.VentaRepository;
import com.celi.backend.service.dto.VentaDTO;
import com.celi.backend.service.dto.AsientoDTO;
import com.celi.backend.service.mapper.VentaMapper;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.celi.backend.web.rest.errors.BadRequestAlertException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.celi.backend.domain.Venta}.
 */
@Service
@Transactional
public class VentaService {

    private static final Logger LOG = LoggerFactory.getLogger(VentaService.class);

    private final VentaRepository ventaRepository;

    private final VentaMapper ventaMapper;

    private final CatedraSyncService catedraSyncService;

    public VentaService(VentaRepository ventaRepository, VentaMapper ventaMapper,
            CatedraSyncService catedraSyncService) {
        this.ventaRepository = ventaRepository;
        this.ventaMapper = ventaMapper;
        this.catedraSyncService = catedraSyncService;
    }

    /**
     * Save a venta.
     *
     * @param ventaDTO the entity to save.
     * @return the persisted entity.
     */
    public VentaDTO save(VentaDTO ventaDTO) {
        LOG.debug("Request to save Venta : {}", ventaDTO);
        Venta venta = ventaMapper.toEntity(ventaDTO);
        venta = ventaRepository.save(venta);
        return ventaMapper.toDto(venta);
    }

    /**
     * Update a venta.
     *
     * @param ventaDTO the entity to save.
     * @return the persisted entity.
     */
    public VentaDTO update(VentaDTO ventaDTO) {
        LOG.debug("Request to update Venta : {}", ventaDTO);
        Venta venta = ventaMapper.toEntity(ventaDTO);
        venta = ventaRepository.save(venta);
        return ventaMapper.toDto(venta);
    }

    /**
     * Partially update a venta.
     *
     * @param ventaDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<VentaDTO> partialUpdate(VentaDTO ventaDTO) {
        LOG.debug("Request to partially update Venta : {}", ventaDTO);

        return ventaRepository
                .findById(ventaDTO.getId())
                .map(existingVenta -> {
                    ventaMapper.partialUpdate(existingVenta, ventaDTO);

                    return existingVenta;
                })
                .map(ventaRepository::save)
                .map(ventaMapper::toDto);
    }

    /**
     * Get all the ventas.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<VentaDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Ventas");
        return ventaRepository.findAll(pageable).map(ventaMapper::toDto);
    }

    /**
     * Get all the ventas with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<VentaDTO> findAllWithEagerRelationships(Pageable pageable) {
        return ventaRepository.findAllWithEagerRelationships(pageable).map(ventaMapper::toDto);
    }

    /**
     * Get one venta by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<VentaDTO> findOne(Long id) {
        LOG.debug("Request to get Venta : {}", id);
        return ventaRepository.findOneWithEagerRelationships(id).map(ventaMapper::toDto);
    }

    /**
     * Delete the venta by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Venta : {}", id);
        ventaRepository.deleteById(id);
    }

    /**
     * Realiza la compra de entrada.
     * 1. Valida disponibilidad (vía Proxy/Redis).
     * 2. Llama a Cátedra para realizar venta (vía Proxy).
     * 3. Guarda localmente.
     * 4. Notifica Kafka (TODO).
     */
    public VentaDTO comprarEntrada(VentaDTO ventaDTO) {
        LOG.debug("Procesando compra de entrada: {}", ventaDTO);

        if (ventaDTO.getEvento() == null || ventaDTO.getEvento().getId() == null) {
            throw new IllegalArgumentException("El evento es requerido");
        }
        Long eventoId = ventaDTO.getEvento().getId();

        // 1. Validar disponibilidad de asientos (llamada a Proxy -> Redis)
        if (ventaDTO.getAsientos() == null || ventaDTO.getAsientos().isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos un asiento");
        }

        for (AsientoDTO asiento : ventaDTO.getAsientos()) {
            String estado = catedraSyncService.getSeatStatus(eventoId, asiento.getFila(), asiento.getColumna());

            // Permitimos la compra si está LIBRE o BLOQUEADO (asumiendo que el bloqueo es
            // del usuario)
            boolean canBuy = "LIBRE".equals(estado) || "BLOQUEADO".equals(estado);

            if (!canBuy) {
                LOG.warn("Asiento no disponible (Estado: {}): Fila {}, Columna {}", estado, asiento.getFila(),
                        asiento.getColumna());
                throw new BadRequestAlertException("El asiento Fila " + asiento.getFila() + " Columna "
                        + asiento.getColumna() + " no está disponible. Estado: " + estado, "venta", "asientoOcupado");
            }
        }

        // 2. Realizar venta remota via Proxy
        Long usuarioId = ventaDTO.getUser() != null ? ventaDTO.getUser().getId() : 1L; // Fallback to 1 if no user?
        boolean ventaExitosa = catedraSyncService.performSale(eventoId, ventaDTO.getAsientos(), usuarioId);

        if (!ventaExitosa) {
            LOG.error("Fallo la venta en Cátedra");
            throw new BadRequestAlertException(
                    "Fallo la venta en el sistema de Cátedra",
                    "venta", "ventaCatedraFallida");
        }

        // 3. Guardar localmente
        ventaDTO.setFechaVenta(java.time.Instant.now());
        ventaDTO.setResultado(true);
        ventaDTO.setDescripcion("Venta realizada via Proxy");
        ventaDTO.setCantidadAsientos(ventaDTO.getAsientos().size());

        // Ensure asientos have state OCUPADO
        ventaDTO.getAsientos().forEach(a -> a.setEstado(com.celi.backend.domain.enumeration.EstadoAsiento.OCUPADO));

        return save(ventaDTO);
    }

    /**
     * Bloquea asientos en Cátedra y guarda estado local.
     */
    public VentaDTO bloquearAsientos(Long eventoId, Set<AsientoDTO> asientos, Long userId) {
        LOG.info("Solicitud de bloqueo de asientos para evento {}", eventoId);

        // 1. Llamar a Proxy para bloquear
        java.util.List<AsientoDTO> listaAsientos = new java.util.ArrayList<>(asientos);
        boolean bloqueoExitoso = catedraSyncService.blockSeats(eventoId, listaAsientos);

        if (!bloqueoExitoso) {
            throw new RuntimeException("No se pudo bloquear los asientos en Cátedra");
        }

        // 2. Guardar localmente como una Venta (tipo bloqueo?)
        // Creamos un VentaDTO dummy para representar el bloqueo
        VentaDTO bloqueo = new VentaDTO();
        bloqueo.setFechaVenta(java.time.Instant.now());
        bloqueo.setDescripcion("Bloqueo de asientos - Usuario " + userId);
        bloqueo.setResultado(true);
        bloqueo.setCantidadAsientos(asientos.size());
        bloqueo.setPrecioVenta(0.0); // Bloqueo no cobra

        // Set user
        com.celi.backend.service.dto.UserDTO u = new com.celi.backend.service.dto.UserDTO();
        u.setId(userId);
        bloqueo.setUser(u);

        // Set evento
        com.celi.backend.service.dto.EventoDTO evt = new com.celi.backend.service.dto.EventoDTO();
        evt.setId(eventoId);
        bloqueo.setEvento(evt);

        // Set asientos con estado BLOQUEADO (needs to be MUTABLE copy if DTO set is
        // immutable, but typically it is not)
        // We iterate and set state
        for (AsientoDTO a : asientos) {
            a.setEstado(com.celi.backend.domain.enumeration.EstadoAsiento.BLOQUEADO);
        }
        bloqueo.setAsientos(asientos);

        return save(bloqueo);
    }
}
