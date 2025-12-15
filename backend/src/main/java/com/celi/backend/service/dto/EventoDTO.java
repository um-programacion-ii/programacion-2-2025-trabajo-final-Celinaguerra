package com.celi.backend.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A DTO for the {@link com.celi.backend.domain.Evento} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EventoDTO implements Serializable {

    private Long id;

    @NotNull
    private String titulo;

    private String resumen;

    @Lob
    private String descripcion;

    @NotNull
    private Instant fecha;

    private String direccion;

    private String imagen;

    private Integer filaAsientos;

    private Integer columnAsientos;

    @NotNull
    private Double precioEntrada;

    private TipoEventoDTO eventoTipo;

    @JsonIgnoreProperties(value = { "evento" }, allowSetters = true)
    private Set<IntegranteDTO> integrantes = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getResumen() {
        return resumen;
    }

    public void setResumen(String resumen) {
        this.resumen = resumen;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Instant getFecha() {
        return fecha;
    }

    public void setFecha(Instant fecha) {
        this.fecha = fecha;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public Integer getFilaAsientos() {
        return filaAsientos;
    }

    public void setFilaAsientos(Integer filaAsientos) {
        this.filaAsientos = filaAsientos;
    }

    public Integer getColumnAsientos() {
        return columnAsientos;
    }

    public void setColumnAsientos(Integer columnAsientos) {
        this.columnAsientos = columnAsientos;
    }

    public Double getPrecioEntrada() {
        return precioEntrada;
    }

    public void setPrecioEntrada(Double precioEntrada) {
        this.precioEntrada = precioEntrada;
    }

    public TipoEventoDTO getEventoTipo() {
        return eventoTipo;
    }

    public void setEventoTipo(TipoEventoDTO eventoTipo) {
        this.eventoTipo = eventoTipo;
    }

    public Set<IntegranteDTO> getIntegrantes() {
        return integrantes;
    }

    public void setIntegrantes(Set<IntegranteDTO> integrantes) {
        this.integrantes = integrantes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EventoDTO)) {
            return false;
        }

        EventoDTO eventoDTO = (EventoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, eventoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return "EventoDTO{" +
                "id=" + getId() +
                ", titulo='" + getTitulo() + "'" +
                ", resumen='" + getResumen() + "'" +
                ", descripcion='" + getDescripcion() + "'" +
                ", fecha='" + getFecha() + "'" +
                ", direccion='" + getDireccion() + "'" +
                ", imagen='" + getImagen() + "'" +
                ", filaAsientos=" + getFilaAsientos() +
                ", columnAsientos=" + getColumnAsientos() +
                ", precioEntrada=" + getPrecioEntrada() +
                ", eventoTipo=" + getEventoTipo() +
                ", integrantes=" + getIntegrantes() +
                "}";
    }
}
