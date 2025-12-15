package com.celi.backend.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * DTO for summarized event view (Payload 3 - eventos-resumidos).
 * Contains only essential fields for listing events.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EventoResumidoDTO implements Serializable {

    private Long id;

    @NotNull
    private String titulo;

    private String resumen;

    private String descripcion;

    @NotNull
    private Instant fecha;

    private Double precioEntrada;

    @JsonIgnoreProperties(value = { "id" }, allowSetters = true)
    private TipoEventoDTO eventoTipo;

    // Getters and Setters

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EventoResumidoDTO)) {
            return false;
        }
        EventoResumidoDTO that = (EventoResumidoDTO) o;
        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return "EventoResumidoDTO{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", resumen='" + resumen + '\'' +
                ", fecha=" + fecha +
                ", precioEntrada=" + precioEntrada +
                ", eventoTipo=" + eventoTipo +
                '}';
    }
}
