package com.celi.backend.service.dto.catedra;

import com.fasterxml.jackson.annotation.JsonAlias;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;

public class CatedraEventoDTO implements Serializable {
    private Long id;
    private String titulo;
    private String resumen;
    private String descripcion;
    private Instant fecha;
    private String direccion; // Payload 4
    private String imagen; // Payload 4

    @JsonAlias({ "fila_asientos", "filaAsientos" })
    private Integer filaAsientos; // Payload 4

    @JsonAlias({ "column_asientos", "columnaAsientos" })
    private Integer columnAsientos; // JSON key is "columnaAsientos"

    @JsonAlias({ "precio_entrada", "precioEntrada" })
    private Double precioEntrada;

    @JsonAlias({ "evento_tipo", "tipo" })
    private CatedraTipoEventoDTO eventoTipo; // JSON key is "tipo"

    private List<CatedraIntegranteDTO> integrantes; // Payload 4

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

    public CatedraTipoEventoDTO getEventoTipo() {
        return eventoTipo;
    }

    public void setEventoTipo(CatedraTipoEventoDTO eventoTipo) {
        this.eventoTipo = eventoTipo;
    }

    public List<CatedraIntegranteDTO> getIntegrantes() {
        return integrantes;
    }

    public void setIntegrantes(List<CatedraIntegranteDTO> integrantes) {
        this.integrantes = integrantes;
    }
}
