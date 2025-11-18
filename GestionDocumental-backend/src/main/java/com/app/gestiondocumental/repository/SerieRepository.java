package com.app.gestiondocumental.repository; // <-- 1. PAQUETE CORREGIDO

import com.app.gestiondocumental.model.Serie; // <-- 2. IMPORT CORREGIDO
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// 3. CAMBIADO 'Integer' A 'Long' para que coincida con tus otras clases
public interface SerieRepository extends JpaRepository<Serie, Long> {

    // 4. CAMBIADO 'Integer' A 'Long' para que coincida con el ID de TrdVersion
    List<Serie> findByTrdVersionId(Long trdVersionId);
}