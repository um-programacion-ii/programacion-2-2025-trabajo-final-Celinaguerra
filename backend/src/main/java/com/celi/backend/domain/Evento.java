package com.celi.backend.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Evento.
 */
@Entity
@Table(name = "evento")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Evento implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "resumen")
    private String resumen;

    @Lob
    @Column(name = "descripcion")
    private String descripcion;

    @NotNull
    @Column(name = "fecha", nullable = false)
    private Instant fecha;

    @Column(name = "direccion")
    private String direccion;

    @Column(name = "imagen", length = 2048)
    private String imagen;

    @Column(name = "fila_asientos")
    private Integer filaAsientos;

    @Column(name = "column_asientos")
    private Integer columnAsientos;

    @NotNull
    @Column(name = "precio_entrada", nullable = false)
    private Double precioEntrada;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "rel_evento__integrante", joinColumns = @JoinColumn(name = "evento_id"), inverseJoinColumns = @JoinColumn(name = "integrante_id"))
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "eventos" }, allowSetters = true)
    private Set<Integrante> integrantes = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private TipoEvento eventoTipo;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Evento id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return this.titulo;
    }

    public Evento titulo(String titulo) {
        this.setTitulo(titulo);
        return this;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getResumen() {
        return this.resumen;
    }

    public Evento resumen(String resumen) {
        this.setResumen(resumen);
        return this;
    }

    public void setResumen(String resumen) {
        this.resumen = resumen;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public Evento descripcion(String descripcion) {
        this.setDescripcion(descripcion);
        return this;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Instant getFecha() {
        return this.fecha;
    }

    public Evento fecha(Instant fecha) {
        this.setFecha(fecha);
        return this;
    }

    public void setFecha(Instant fecha) {
        this.fecha = fecha;
    }

    public String getDireccion() {
        return this.direccion;
    }

    public Evento direccion(String direccion) {
        this.setDireccion(direccion);
        return this;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getImagen() {
        return this.imagen;
    }

    public Evento imagen(String imagen) {
        this.setImagen(imagen);
        return this;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public Integer getFilaAsientos() {
        return this.filaAsientos;
    }

    public Evento filaAsientos(Integer filaAsientos) {
        this.setFilaAsientos(filaAsientos);
        return this;
    }

    public void setFilaAsientos(Integer filaAsientos) {
        this.filaAsientos = filaAsientos;
    }

    public Integer getColumnAsientos() {
        return this.columnAsientos;
    }

    public Evento columnAsientos(Integer columnAsientos) {
        this.setColumnAsientos(columnAsientos);
        return this;
    }

    public void setColumnAsientos(Integer columnAsientos) {
        this.columnAsientos = columnAsientos;
    }

    public Double getPrecioEntrada() {
        return this.precioEntrada;
    }

    public Evento precioEntrada(Double precioEntrada) {
        this.setPrecioEntrada(precioEntrada);
        return this;
    }

    public void setPrecioEntrada(Double precioEntrada) {
        this.precioEntrada = precioEntrada;
    }

    public Set<Integrante> getIntegrantes() {
        return this.integrantes;
    }

    public void setIntegrantes(Set<Integrante> integrantes) {
        this.integrantes = integrantes;
    }

    public Evento integrantes(Set<Integrante> integrantes) {
        this.setIntegrantes(integrantes);
        return this;
    }

    public Evento addIntegrantes(Integrante integrante) {
        this.integrantes.add(integrante);
        return this;
    }

    public Evento removeIntegrantes(Integrante integrante) {
        this.integrantes.remove(integrante);
        return this;
    }

    public TipoEvento getEventoTipo() {
        return this.eventoTipo;
    }

    public void setEventoTipo(TipoEvento tipoEvento) {
        this.eventoTipo = tipoEvento;
    }

    public Evento eventoTipo(TipoEvento tipoEvento) {
        this.setEventoTipo(tipoEvento);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and
    // setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Evento)) {
            return false;
        }
        return getId() != null && getId().equals(((Evento) o).getId());
    }

    @Override
    public int hashCode() {
        // see
        // https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Evento{" +
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
                "}";
    }
}
