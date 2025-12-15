package com.celi.backend.service.dto.catedra;

import java.io.Serializable;

public class CatedraTipoEventoDTO implements Serializable {

    private String nombre;
    private String descripcion;

    // Getters and Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
