package com.celi.proxy.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConsumerService {

    private final BackendNotificationService backendNotificationService;
    private final ObjectMapper objectMapper;

    public KafkaConsumerService(BackendNotificationService backendNotificationService) {
        this.backendNotificationService = backendNotificationService;
        this.objectMapper = new ObjectMapper();
    }

    @KafkaListener(topics = "${application.kafka.topic-name:eventos-actualizacion}", groupId = "eventos-celi-group")
    public void consumeEventUpdates(String message) {
        log.info("📨 Received Kafka message from Cátedra: {}", message);

        try {
            JsonNode jsonNode = objectMapper.readTree(message);

            Long eventoId = null;

            if (jsonNode.has("eventoId")) {
                eventoId = jsonNode.get("eventoId").asLong();
            } else if (jsonNode.has("id")) {
                eventoId = jsonNode.get("id").asLong();
            }

            if (eventoId != null) {
                log.info("Evento actualizado en evento ID: {}", eventoId);
                backendNotificationService.notifyEventUpdate(eventoId);
            } else {
                log.warn("No se encontro eventoId o id en el mensaje: {}", message);
            }

        } catch (Exception e) {
            log.error("Error al procesar el mensaje: {}", e.getMessage(), e);
        }
    }
}
