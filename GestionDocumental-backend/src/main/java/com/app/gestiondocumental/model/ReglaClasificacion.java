package com.app.gestiondocumental.model;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "regla_clasificacion")
public class ReglaClasificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "trd_version_id")
    private Integer trdVersionId;

    private String nivel;

    @Column(name = "destino_id")
    private Integer destinoId;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode expresion;

    private Integer prioridad = 100;
    private Boolean activo = true;

    // Getters & setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getTrdVersionId() { return trdVersionId; }
    public void setTrdVersionId(Integer trdVersionId) { this.trdVersionId = trdVersionId; }

    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }

    public Integer getDestinoId() { return destinoId; }
    public void setDestinoId(Integer destinoId) { this.destinoId = destinoId; }

    public JsonNode getExpresion() { return expresion; }
    public void setExpresion(JsonNode expresion) { this.expresion = expresion; }

    public Integer getPrioridad() { return prioridad; }
    public void setPrioridad(Integer prioridad) { this.prioridad = prioridad; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}