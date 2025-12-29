package com.celi.backend.service.dto;

/**
 * DTO for event synchronization notifications from proxy.
 */
public class EventoSyncNotificationDTO {
    private Long eventoId;
    private String source;

    public Long getEventoId() {
        return eventoId;
    }

    public void setEventoId(Long eventoId) {
        this.eventoId = eventoId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
