package com.app.gestiondocumental.repository;

import com.app.gestiondocumental.model.TrdVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List; // <-- IMPORT AÑADIDO

public interface TrdVersionRepository extends JpaRepository<TrdVersion, Long> {

    // Método que ya tenías
    @Query("SELECT v FROM TrdVersion v WHERE :fecha BETWEEN v.vigenciaDesde AND COALESCE(v.vigenciaHasta, :fecha)")
    TrdVersion findByFecha(@Param("fecha") LocalDate fecha);

    // --- MÉTODO AÑADIDO QUE FALTABA ---
    // Busca TRDs cuyo nombre contenga el texto 'q' (ignorando mayúsculas)
    @Query("SELECT t FROM TrdVersion t WHERE lower(t.nombre) LIKE lower(concat('%', :q, '%'))")
    List<TrdVersion> searchByName(String q);
}