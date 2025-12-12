package com.celi.backend.service.dto;

import com.celi.backend.domain.enumeration.EstadoAsiento;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.celi.backend.domain.Asiento} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AsientoDTO implements Serializable {

    private Long id;

    @NotNull
    private Integer fila;

    @NotNull
    private Integer columna;

    private String persona;

    private EstadoAsiento estado;

    private VentaDTO venta;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getFila() {
        return fila;
    }

    public void setFila(Integer fila) {
        this.fila = fila;
    }

    public Integer getColumna() {
        return columna;
    }

    public void setColumna(Integer columna) {
        this.columna = columna;
    }

    public String getPersona() {
        return persona;
    }

    public void setPersona(String persona) {
        this.persona = persona;
    }

    public EstadoAsiento getEstado() {
        return estado;
    }

    public void setEstado(EstadoAsiento estado) {
        this.estado = estado;
    }

    public VentaDTO getVenta() {
        return venta;
    }

    public void setVenta(VentaDTO venta) {
        this.venta = venta;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AsientoDTO)) {
            return false;
        }

        AsientoDTO asientoDTO = (AsientoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, asientoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AsientoDTO{" +
            "id=" + getId() +
            ", fila=" + getFila() +
            ", columna=" + getColumna() +
            ", persona='" + getPersona() + "'" +
            ", estado='" + getEstado() + "'" +
            ", venta=" + getVenta() +
            "}";
    }
}
