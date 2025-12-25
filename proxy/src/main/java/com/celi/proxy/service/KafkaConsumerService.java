package com.celi.proxy.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConsumerService {

    @KafkaListener(topics = "${application.kafka.topic-name:evento-updates}", groupId = "proxy-consumer-group")
    public void consumeEventUpdates(String message) {
        log.info("Received Kafka message from Cátedra: {}", message);

        try {
            processEventUpdate(message);
        } catch (Exception e) {
            log.error("Error processing Kafka message: {}", e.getMessage(), e);
        }
    }

    private void processEventUpdate(String message) {
        log.debug("Processing event update: {}", message);
    }
}
