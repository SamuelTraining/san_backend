package com.app.gestiondocumental.model;

import jakarta.persistence.*;
import java.util.ArrayList; // <-- IMPORT AÑADIDO
import java.util.List;

@Entity
@Table(name = "serie") // Faltaba el schema = "trd"
public class Serie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String codigo;
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "trd_version_id")
    private TrdVersion trdVersion;

    @OneToMany(mappedBy = "serie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Subserie> subseries = new ArrayList<>(); // <-- ¡CORREGIDO!

    // getters & setters
    public Long getId() {
        return id;
    }
    // ... (resto de getters y setters sin cambios) ...
    public void setId(Long id) { this.id = id; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public TrdVersion getTrdVersion() { return trdVersion; }
    public void setTrdVersion(TrdVersion trdVersion) { this.trdVersion = trdVersion; }
        public List<Subserie> getSubseries() { return subseries; }
    public void setSubseries(List<Subserie> subseries) { this.subseries = subseries; }

    // Helpers
    public void addSubserie(Subserie subserie) {
        subseries.add(subserie);
        subserie.setSerie(this);
    }

    public void removeSubserie(Subserie subserie) {
        subseries.remove(subserie);
        subserie.setSerie(null);
    }
}