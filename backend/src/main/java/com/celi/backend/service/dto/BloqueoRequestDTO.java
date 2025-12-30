package com.celi.backend.service.dto;

import java.io.Serializable;
import java.util.List;

/**
 * DTO specific for blocking seats request.
 */
public class BloqueoRequestDTO implements Serializable {

    private Long eventoId;
    private Long userId; // Optional, can be inferred from context
    private List<AsientoDTO> asientos;

    public Long getEventoId() {
        return eventoId;
    }

    public void setEventoId(Long eventoId) {
        this.eventoId = eventoId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<AsientoDTO> getAsientos() {
        return asientos;
    }

    public void setAsientos(List<AsientoDTO> asientos) {
        this.asientos = asientos;
    }
}
