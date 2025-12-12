package com.celi.backend.domain;

import com.celi.backend.domain.enumeration.EstadoAsiento;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "asiento")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Asiento implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "fila", nullable = false)
    private Integer fila;

    @NotNull
    @Column(name = "columna", nullable = false)
    private Integer columna;

    @Column(name = "persona")
    private String persona;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoAsiento estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "asientos", "user", "evento" }, allowSetters = true)
    private Venta venta;

    public Long getId() {
        return this.id;
    }

    public Asiento id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getFila() {
        return this.fila;
    }

    public Asiento fila(Integer fila) {
        this.setFila(fila);
        return this;
    }

    public void setFila(Integer fila) {
        this.fila = fila;
    }

    public Integer getColumna() {
        return this.columna;
    }

    public Asiento columna(Integer columna) {
        this.setColumna(columna);
        return this;
    }

    public void setColumna(Integer columna) {
        this.columna = columna;
    }

    public String getPersona() {
        return this.persona;
    }

    public Asiento persona(String persona) {
        this.setPersona(persona);
        return this;
    }

    public void setPersona(String persona) {
        this.persona = persona;
    }

    public EstadoAsiento getEstado() {
        return this.estado;
    }

    public Asiento estado(EstadoAsiento estado) {
        this.setEstado(estado);
        return this;
    }

    public void setEstado(EstadoAsiento estado) {
        this.estado = estado;
    }

    public Venta getVenta() {
        return this.venta;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }

    public Asiento venta(Venta venta) {
        this.setVenta(venta);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Asiento)) {
            return false;
        }
        return getId() != null && getId().equals(((Asiento) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Asiento{" +
                "id=" + getId() +
                ", fila=" + getFila() +
                ", columna=" + getColumna() +
                ", persona='" + getPersona() + "'" +
                ", estado='" + getEstado() + "'" +
                "}";
    }
}
