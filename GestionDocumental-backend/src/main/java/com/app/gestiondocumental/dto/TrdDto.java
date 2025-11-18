package com.app.gestiondocumental.dto;

import java.time.LocalDate;
import java.util.List;

public class TrdDto {
    public Long id; // <-- ¡CORREGIDO DE INTEGER A LONG!
    public String nombre;
    public LocalDate vigenciaDesde;
    public LocalDate vigenciaHasta;
    public String estado;
    public List<SerieDto> series;
}