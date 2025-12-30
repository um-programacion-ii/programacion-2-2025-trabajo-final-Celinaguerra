package com.celi.proxy.web.rest;

import com.celi.proxy.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing seat status via Redis.
 */
@RestController
@RequestMapping("/api/asientos")
@Slf4j
public class AsientoResource {

    private final RedisService redisService;

    public AsientoResource(RedisService redisService) {
        this.redisService = redisService;
    }

    /**
     * GET /api/asientos/{eventoId}
     * Returns the full JSON status of seats for an event from Redis.
     * key: evento_{id}
     */
    @GetMapping("/{eventoId}")
    public ResponseEntity<String> getAsientoEstado(@PathVariable Long eventoId) {
        log.debug("REST request to get seat status for eventoId: {}", eventoId);
        String status = redisService.getEventSeats(eventoId);

        if (status != null) {
            return ResponseEntity.ok(status);
        } else {
            // Si no hay datos en Redis, podría significar evento sin asientos ocupados o
            // error.
            // Retornamos 404 o un JSON vacío?
            // "Datos de la key: evento_1". Si no existe, no hay datos.
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /api/asientos/{eventoId}/{fila}/{columna}
     * Check if a specific seat is available (helper endpoint, primarily for
     * backend/testing)
     */
    @GetMapping("/{eventoId}/{fila}/{columna}")
    public ResponseEntity<String> getAsientoEspecifico(@PathVariable Long eventoId, @PathVariable Integer fila,
            @PathVariable Integer columna) {
        log.debug("REST request to check seat: {} {} {}", eventoId, fila, columna);
        String status = redisService.getSeatStatus(eventoId, fila, columna);
        return ResponseEntity.ok(status);
    }

    /**
     * DEBUG: Listar todas las keys de eventos en Redis
     */
    @GetMapping("/debug/keys")
    public ResponseEntity<java.util.Set<String>> getRedisKeys() {
        return ResponseEntity.ok(redisService.getKeys("evento_*"));
    }
}
