package com.celi.backend.service;

import com.celi.backend.domain.TipoEvento;
import com.celi.backend.repository.TipoEventoRepository;
import com.celi.backend.service.dto.TipoEventoDTO;
import com.celi.backend.service.mapper.TipoEventoMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.celi.backend.domain.TipoEvento}.
 */
@Service
@Transactional
public class TipoEventoService {

    private static final Logger LOG = LoggerFactory.getLogger(TipoEventoService.class);

    private final TipoEventoRepository tipoEventoRepository;

    private final TipoEventoMapper tipoEventoMapper;

    public TipoEventoService(TipoEventoRepository tipoEventoRepository, TipoEventoMapper tipoEventoMapper) {
        this.tipoEventoRepository = tipoEventoRepository;
        this.tipoEventoMapper = tipoEventoMapper;
    }

    /**
     * Save a tipoEvento.
     *
     * @param tipoEventoDTO the entity to save.
     * @return the persisted entity.
     */
    public TipoEventoDTO save(TipoEventoDTO tipoEventoDTO) {
        LOG.debug("Request to save TipoEvento : {}", tipoEventoDTO);
        TipoEvento tipoEvento = tipoEventoMapper.toEntity(tipoEventoDTO);
        tipoEvento = tipoEventoRepository.save(tipoEvento);
        return tipoEventoMapper.toDto(tipoEvento);
    }

    /**
     * Update a tipoEvento.
     *
     * @param tipoEventoDTO the entity to save.
     * @return the persisted entity.
     */
    public TipoEventoDTO update(TipoEventoDTO tipoEventoDTO) {
        LOG.debug("Request to update TipoEvento : {}", tipoEventoDTO);
        TipoEvento tipoEvento = tipoEventoMapper.toEntity(tipoEventoDTO);
        tipoEvento = tipoEventoRepository.save(tipoEvento);
        return tipoEventoMapper.toDto(tipoEvento);
    }

    /**
     * Partially update a tipoEvento.
     *
     * @param tipoEventoDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TipoEventoDTO> partialUpdate(TipoEventoDTO tipoEventoDTO) {
        LOG.debug("Request to partially update TipoEvento : {}", tipoEventoDTO);

        return tipoEventoRepository
            .findById(tipoEventoDTO.getId())
            .map(existingTipoEvento -> {
                tipoEventoMapper.partialUpdate(existingTipoEvento, tipoEventoDTO);

                return existingTipoEvento;
            })
            .map(tipoEventoRepository::save)
            .map(tipoEventoMapper::toDto);
    }

    /**
     * Get all the tipoEventos.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<TipoEventoDTO> findAll() {
        LOG.debug("Request to get all TipoEventos");
        return tipoEventoRepository.findAll().stream().map(tipoEventoMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one tipoEvento by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TipoEventoDTO> findOne(Long id) {
        LOG.debug("Request to get TipoEvento : {}", id);
        return tipoEventoRepository.findById(id).map(tipoEventoMapper::toDto);
    }

    /**
     * Delete the tipoEvento by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete TipoEvento : {}", id);
        tipoEventoRepository.deleteById(id);
    }
}
