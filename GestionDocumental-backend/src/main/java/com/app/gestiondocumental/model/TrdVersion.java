package com.app.gestiondocumental.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List; // <-- IMPORT AÑADIDO
import java.util.ArrayList; // <-- IMPORT AÑADIDO
import jakarta.persistence.CascadeType; // <-- IMPORT AÑADIDO
import jakarta.persistence.FetchType; // <-- IMPORT AÑADIDO
import jakarta.persistence.OneToMany; // <-- IMPORT AÑADIDO

@Entity
@Table(name = "trd_version", schema = "trd")
public class TrdVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "vigencia_desde", nullable = false)
    private LocalDate vigenciaDesde;

    @Column(name = "vigencia_hasta")
    private LocalDate vigenciaHasta;

    @Column(name = "estado", length = 12, nullable = false)
    private String estado;

    // nombre del archivo original
    @Column(name = "nombre_archivo")
    private String nombreArchivo;

    // mime type del archivo
    @Column(name = "tipo_archivo")
    private String tipoArchivo;

    // contenido binario del archivo
    @Lob
    @Column(name = "archivo")
    private byte[] archivo;

    @Column(name = "fecha_carga", insertable = false, updatable = false)
    private java.time.OffsetDateTime fechaCarga;

    // --- CAMPO FALTANTE AÑADIDO ---
    @OneToMany(
            mappedBy = "trdVersion", // Debe coincidir con el nombre del campo en la clase 'Serie'
            cascade = CascadeType.ALL, // Guarda/Borra series junto con la versión
            fetch = FetchType.LAZY,    // No carga las series a menos que se pidan
            orphanRemoval = true       // Borra series que se quiten de esta lista
    )
    private List<Serie> series = new ArrayList<>();
    // --- FIN DEL CAMPO AÑADIDO ---


    // Getters y Setters (incluye nuevos campos)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDate getVigenciaDesde() {
        return vigenciaDesde;
    }

    public void setVigenciaDesde(LocalDate vigenciaDesde) {
        this.vigenciaDesde = vigenciaDesde;
    }

    public LocalDate getVigenciaHasta() {
        return vigenciaHasta;
    }

    public void setVigenciaHasta(LocalDate vigenciaHasta) {
        this.vigenciaHasta = vigenciaHasta;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String getTipoArchivo() {
        return tipoArchivo;
    }

    public void setTipoArchivo(String tipoArchivo) {
        this.tipoArchivo = tipoArchivo;
    }

    public byte[] getArchivo() {
        return archivo;
    }

    public void setArchivo(byte[] archivo) {
        this.archivo = archivo;
    }

    public java.time.OffsetDateTime getFechaCarga() {
        return fechaCarga;
    }

    public void setFechaCarga(java.time.OffsetDateTime fechaCarga) {
        this.fechaCarga = fechaCarga;
    }

    // --- Getter y Setter para 'series' (Añadido) ---
    public List<Serie> getSeries() {
        return series;
    }

    public void setSeries(List<Serie> series) {
        this.series = series;
    }

    // helpers (Ahora funcionarán)
    public void addSerie(Serie serie) {
        series.add(serie);
        serie.setTrdVersion(this);
    }

    public void removeSerie(Serie serie) {
        series.remove(serie);
        serie.setTrdVersion(null);
    }
}