package com.celi.proxy.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    public RedisService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String getSeatStatus(Long eventoId, Integer fila, Integer columna) {
        String key = buildSeatKey(eventoId, fila, columna);
        log.debug("Querying Redis for seat status with key: {}", key);

        try {
            String status = redisTemplate.opsForValue().get(key);
            log.debug("Redis returned status '{}' for key '{}'", status, key);
            return status;
        } catch (Exception e) {
            log.error("Error querying Redis for key '{}': {}", key, e.getMessage());
            return null;
        }
    }

    public boolean isSeatAvailable(Long eventoId, Integer fila, Integer columna) {
        String status = getSeatStatus(eventoId, fila, columna);
        return "LIBRE".equalsIgnoreCase(status);
    }

    private String buildSeatKey(Long eventoId, Integer fila, Integer columna) {
        return String.format("asiento_%d_%d_%d", eventoId, fila, columna);
    }
}
