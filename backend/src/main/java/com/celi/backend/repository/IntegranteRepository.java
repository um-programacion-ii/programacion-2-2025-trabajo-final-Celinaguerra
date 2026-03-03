package com.celi.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.celi.backend.domain.Integrante;

/**
 * Spring Data JPA repository for the {@link Integrante} entity.
 */
@Repository
public interface IntegranteRepository extends JpaRepository<Integrante, Long> {
}
