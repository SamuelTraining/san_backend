package com.app.gestiondocumental.repository;

import com.app.gestiondocumental.model.FileRecord;
import com.app.gestiondocumental.model.FileStatus; // Asegúrate de importar el Enum
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FileRecordRepository extends JpaRepository<FileRecord, Long> {
    Optional<FileRecord> findByMd5(String md5);

    // --- NUEVO MÉTODO ---
    List<FileRecord> findByStatus(FileStatus status);
}