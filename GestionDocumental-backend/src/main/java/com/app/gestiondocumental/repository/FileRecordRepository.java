package com.app.gestiondocumental.repository; // <-- PAQUETE CORREGIDO

// --- IMPORT CORREGIDO ---
import com.app.gestiondocumental.model.FileRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Esta es una interfaz de Spring Data JPA, se encarga de las consultas a la BD
public interface FileRecordRepository extends JpaRepository<FileRecord, Long> {

    // Spring Data JPA crea automáticamente la consulta SQL para este método
    // basado en el nombre ("findByMd5")
    Optional<FileRecord> findByMd5(String md5);
}