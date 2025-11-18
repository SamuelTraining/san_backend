package com.app.gestiondocumental.repository; // <-- 1. PAQUETE CORREGIDO

import com.app.gestiondocumental.model.Subserie; // <-- 2. IMPORT CORREGIDO
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// 3. CAMBIADO 'Integer' A 'Long'
public interface SubserieRepository extends JpaRepository<Subserie, Long> {

    // 4. CAMBIADO 'Integer' A 'Long'
    List<Subserie> findBySerieId(Long serieId);
}