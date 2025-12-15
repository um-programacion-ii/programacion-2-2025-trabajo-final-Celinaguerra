package com.celi.backend.repository;

import com.celi.backend.domain.Integrante;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Integrante entity.
 */
@SuppressWarnings("unused")
@Repository
public interface IntegranteRepository extends JpaRepository<Integrante, Long> {
    Optional<Integrante> findFirstByNombreAndApellidoAndIdentificacion(String nombre, String apellido,
            String identificacion);
}
