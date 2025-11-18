package com.app.gestiondocumental.dto.ia;

import java.util.Map;

// El JSON que recibiremos (debe coincidir con el main.py de Python)
// Usaremos @JsonIgnoreProperties para ignorar campos que no necesitemos
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true) // Importante si Python envía más campos
public class RespuestaIaDto {
    private String categoria_predicha;
    private double confianza;
    private String estado_validacion;
    private Map<String, String> metadatos;
    private String error;

    // Getters y Setters para todos los campos
    public String getCategoria_predicha() { return categoria_predicha; }
    public void setCategoria_predicha(String c) { this.categoria_predicha = c; }

    public double getConfianza() { return confianza; }
    public void setConfianza(double c) { this.confianza = c; }

    public String getEstado_validacion() { return estado_validacion; }
    public void setEstado_validacion(String e) { this.estado_validacion = e; }

    public Map<String, String> getMetadatos() { return metadatos; }
    public void setMetadatos(Map<String, String> m) { this.metadatos = m; }

    public String getError() { return error; }
    public void setError(String e) { this.error = e; }
}