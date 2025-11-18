package com.app.gestiondocumental.model;

import jakarta.persistence.*;
import java.util.ArrayList; // <-- IMPORT AÑADIDO
import java.util.List;

@Entity
@Table(name = "subserie") // Faltaba el schema = "trd"
public class Subserie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String codigo;
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "serie_id")
    private Serie serie;

    @OneToMany(mappedBy = "subserie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TipoDocumental> tipos = new ArrayList<>(); // <-- ¡CORREGIDO!

    public Long getId() {
        return id;
    }
    // ... (resto de getters y setters sin cambios) ...
    public void setId(Long id) { this.id = id; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Serie getSerie() { return serie; }
    public void setSerie(Serie serie) { this.serie = serie; }
    public List<TipoDocumental> getTipos() { return tipos; }
    public void setTipos(List<TipoDocumental> tipos) { this.tipos = tipos; }

    // helpers
    public void addTipo(TipoDocumental tipo) {
        tipos.add(tipo);
        tipo.setSubserie(this);
    }

    public void removeTipo(TipoDocumental tipo) {
        tipos.remove(tipo);
        tipo.setSubserie(null);
    }
}