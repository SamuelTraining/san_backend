package com.app.gestiondocumental.repository; // <-- Tu paquete (está bien)

import com.app.gestiondocumental.model.TipoDocumental; // <-- 1. IMPORT CORREGIDO
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// 2. CAMBIADO 'Integer' A 'Long'
public interface TipoDocumentalRepository extends JpaRepository<TipoDocumental, Long> {

    // 3. CAMBIADO 'Integer' A 'Long'
    List<TipoDocumental> findBySubserieId(Long subserieId);
}