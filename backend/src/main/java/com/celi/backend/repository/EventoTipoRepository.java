package com.celi.backend.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.celi.backend.domain.EventoTipo;

/**
 * Spring Data JPA repository for the {@link EventoTipo} entity.
 */
@Repository
public interface EventoTipoRepository extends JpaRepository<EventoTipo, Long> {
    Optional<EventoTipo> findByNombre(String nombre);
}
