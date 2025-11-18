package com.app.gestiondocumental.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tipo_documental", schema = "trd")
public class TipoDocumental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", nullable = false)
    private String codigo;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "obligatoria", nullable = false)
    private Boolean obligatoria;

    // --- Relación con Subserie ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subserie_id", nullable = false)
    private Subserie subserie;

    // --- GETTERS Y SETTERS ---
    // (Estos son los métodos que le faltan a tu TrdService)

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Boolean getObligatoria() {
        return obligatoria;
    }

    public void setObligatoria(Boolean obligatoria) {
        this.obligatoria = obligatoria;
    }

    public Subserie getSubserie() {
        return subserie;
    }

    public void setSubserie(Subserie subserie) {
        this.subserie = subserie;
    }
}