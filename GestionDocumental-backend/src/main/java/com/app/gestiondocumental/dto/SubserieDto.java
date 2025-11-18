package com.app.gestiondocumental.dto;

import java.util.List;

public class SubserieDto {
    public Long id; // <-- Corregido a Long
    public String codigo;
    public String nombre;
    public List<TipoDto> tipos;
}