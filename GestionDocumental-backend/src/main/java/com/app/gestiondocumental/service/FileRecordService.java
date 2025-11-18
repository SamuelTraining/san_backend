package com.app.gestiondocumental.service;

import com.app.gestiondocumental.model.FileRecord;
import com.app.gestiondocumental.repository.FileRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FileRecordService {

    private final FileRecordRepository repo;

    public FileRecordService(FileRecordRepository repo) {
        this.repo = repo;
    }

    // Este método guarda el archivo en la base de datos
    // @Transactional asegura que se haga en una transacción segura
    @Transactional
    public FileRecord saveOrGetExisting(FileRecord rec) {
        if (rec == null) return null;

        // Re-usamos la lógica de buscar por MD5
        if (rec.getMd5() != null) {
            return repo.findByMd5(rec.getMd5()).orElseGet(() -> {
                // Si no existe, lo guarda
                return repo.save(rec);
            });
        }
        // Si no tiene MD5 (ej. error), simplemente lo guarda
        return repo.save(rec);
    }
}