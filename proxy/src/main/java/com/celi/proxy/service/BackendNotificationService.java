package com.celi.proxy.service;

import com.celi.proxy.config.ApplicationProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Notifica al backend cuando los eventos se actualizan via Kafka.
 */
@Service
@Slf4j
public class BackendNotificationService {

    private final RestTemplate restTemplate;
    private final ApplicationProperties applicationProperties;

    public BackendNotificationService(RestTemplate restTemplate, ApplicationProperties applicationProperties) {
        this.restTemplate = restTemplate;
        this.applicationProperties = applicationProperties;
    }

    public void notifyEventUpdate(Long eventoId) {
        String backendUrl = applicationProperties.getBackend().getUrl();
        String notificationUrl = backendUrl + "/api/eventos/sync-notification";

        log.info("Notificando backend de actualizacion de evento: eventoId={}", eventoId);

        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("eventoId", eventoId);
            payload.put("source", "kafka");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

            restTemplate.postForEntity(notificationUrl, request, Void.class);

            log.info("Backend notificado para evento ID: {}", eventoId);
        } catch (Exception e) {
            log.error("Error al notificar backend para evento ID {}: {}", eventoId, e.getMessage());
        }
    }
}
