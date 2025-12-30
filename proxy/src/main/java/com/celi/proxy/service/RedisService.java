package com.celi.proxy.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RedisService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Obtiene el JSON completo de asientos para un evento desde Redis.
     * Key: evento_{id}
     */
    public String getEventSeats(Long eventoId) {
        String key = "evento_" + eventoId;
        log.debug("Querying Redis for key: {}", key);
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("Error querying Redis for key '{}': {}", key, e.getMessage());
            return null;
        }
    }

    /**
     * Verifica si un asiento específico está libre parseando el JSON del evento.
     */
    /**
     * Verifica el estado específico de un asiento parseando el JSON del evento.
     * Retorna: "LIBRE", "VENDIDO", "BLOQUEADO", "OCUPADO" (si no se sabe), o
     * "ERROR".
     */
    public String getSeatStatus(Long eventoId, Integer fila, Integer columna) {
        String json = getEventSeats(eventoId);
        if (json == null) {
            // Si no existe la key, asumimos que todo está libre
            return "LIBRE";
        }

        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode asientos = root.get("asientos");

            if (asientos != null && asientos.isArray()) {
                for (JsonNode asiento : asientos) {
                    int f = asiento.get("fila").asInt();
                    int c = asiento.get("columna").asInt();

                    if (f == fila && c == columna) {
                        String estado = asiento.get("estado").asText();
                        // Retornamos el estado tal cual (ej: "Bloqueado", "Vendido"), convertido a
                        // Mayusculas para consistencia
                        return estado != null ? estado.toUpperCase() : "OCUPADO";
                    }
                }
            }
            // Si no está en la lista de ocupados, está libre
            return "LIBRE";

        } catch (Exception e) {
            log.error("Error parsing Redis JSON for seat check: {}", e.getMessage());
            return "ERROR";
        }
    }

    /**
     * DEBUG: Listar keys que coinciden con un patrón
     */
    public java.util.Set<String> getKeys(String pattern) {
        return redisTemplate.keys(pattern);
    }
}
