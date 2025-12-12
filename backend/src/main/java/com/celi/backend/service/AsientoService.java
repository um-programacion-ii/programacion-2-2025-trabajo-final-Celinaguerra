package com.celi.backend.service;

import com.celi.backend.domain.Asiento;
import com.celi.backend.repository.AsientoRepository;
import com.celi.backend.service.dto.AsientoDTO;
import com.celi.backend.service.mapper.AsientoMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.celi.backend.domain.Asiento}.
 */
@Service
@Transactional
public class AsientoService {

    private static final Logger LOG = LoggerFactory.getLogger(AsientoService.class);

    private final AsientoRepository asientoRepository;

    private final AsientoMapper asientoMapper;

    public AsientoService(AsientoRepository asientoRepository, AsientoMapper asientoMapper) {
        this.asientoRepository = asientoRepository;
        this.asientoMapper = asientoMapper;
    }

    /**
     * Save a asiento.
     *
     * @param asientoDTO the entity to save.
     * @return the persisted entity.
     */
    public AsientoDTO save(AsientoDTO asientoDTO) {
        LOG.debug("Request to save Asiento : {}", asientoDTO);
        Asiento asiento = asientoMapper.toEntity(asientoDTO);
        asiento = asientoRepository.save(asiento);
        return asientoMapper.toDto(asiento);
    }

    /**
     * Update a asiento.
     *
     * @param asientoDTO the entity to save.
     * @return the persisted entity.
     */
    public AsientoDTO update(AsientoDTO asientoDTO) {
        LOG.debug("Request to update Asiento : {}", asientoDTO);
        Asiento asiento = asientoMapper.toEntity(asientoDTO);
        asiento = asientoRepository.save(asiento);
        return asientoMapper.toDto(asiento);
    }

    /**
     * Partially update a asiento.
     *
     * @param asientoDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<AsientoDTO> partialUpdate(AsientoDTO asientoDTO) {
        LOG.debug("Request to partially update Asiento : {}", asientoDTO);

        return asientoRepository
            .findById(asientoDTO.getId())
            .map(existingAsiento -> {
                asientoMapper.partialUpdate(existingAsiento, asientoDTO);

                return existingAsiento;
            })
            .map(asientoRepository::save)
            .map(asientoMapper::toDto);
    }

    /**
     * Get all the asientos.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<AsientoDTO> findAll() {
        LOG.debug("Request to get all Asientos");
        return asientoRepository.findAll().stream().map(asientoMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one asiento by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<AsientoDTO> findOne(Long id) {
        LOG.debug("Request to get Asiento : {}", id);
        return asientoRepository.findById(id).map(asientoMapper::toDto);
    }

    /**
     * Delete the asiento by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Asiento : {}", id);
        asientoRepository.deleteById(id);
    }
}
