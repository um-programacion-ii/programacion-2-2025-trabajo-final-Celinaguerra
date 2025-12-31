package com.celi.backend.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import com.celi.backend.domain.AsientoVenta;

/**
 * Spring Data JPA repository for the AsientoVenta entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AsientoVentaRepository extends JpaRepository<AsientoVenta, Long> {
}
