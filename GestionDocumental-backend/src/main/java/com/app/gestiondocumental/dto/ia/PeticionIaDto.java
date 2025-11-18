package com.app.gestiondocumental.dto.ia;

// El JSON que enviaremos (debe coincidir con el main.py de Python)
public class PeticionIaDto {
    private String ruta_archivo;

    // Constructor, Getter y Setter
    public PeticionIaDto(String ruta_archivo) {
        this.ruta_archivo = ruta_archivo;
    }
    public String getRuta_archivo() { return ruta_archivo; }
    public void setRuta_archivo(String ruta_archivo) { this.ruta_archivo = ruta_archivo; }
}