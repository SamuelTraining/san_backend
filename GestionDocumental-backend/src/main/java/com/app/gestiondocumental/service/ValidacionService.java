package com.app.gestiondocumental.service;

import com.app.gestiondocumental.model.FileRecord;
import com.app.gestiondocumental.model.FileStatus;
import com.app.gestiondocumental.repository.FileRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class ValidacionService {

    private final FileRecordRepository repo;
    // Ruta base donde se mueven los archivos validados
    private static final String RUTA_CLASIFICADOS = "C:/Clasificados";

    public ValidacionService(FileRecordRepository repo) {
        this.repo = repo;
    }

    // 1. Obtener lista de pendientes
    public List<FileRecord> obtenerPendientes() {
        return repo.findByStatus(FileStatus.NO_PROCESABLE);
    }

    // 2. Validar y Corregir (Versión Robusta)
    @Transactional
    public FileRecord validarManualmente(Long id, String categoriaCorrecta) throws IOException {
        // A. Buscar el registro en BD
        FileRecord record = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("El archivo con ID " + id + " no existe en la base de datos."));

        Path origen = Paths.get(record.getOriginPath());

        // B. Verificación de Seguridad: ¿Existe el archivo físico?
        if (!Files.exists(origen)) {
            // Si no existe, puede ser que ya se movió o la ruta está mal.
            // Lanzamos un error claro.
            throw new IOException("El archivo físico no se encuentra en: " + origen.toString());
        }

        // C. Preparar destino
        Path carpetaDestino = Paths.get(RUTA_CLASIFICADOS, categoriaCorrecta);
        if (!Files.exists(carpetaDestino)) {
            Files.createDirectories(carpetaDestino);
        }

        Path destino = carpetaDestino.resolve(origen.getFileName());

        // D. Intentar Mover con estrategia de respaldo (Copy+Delete)
        try {
            Files.move(origen, destino, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.out.println("⚠️ Archivo bloqueado para mover, intentando copiar y borrar...");
            // Plan B: Copiar y luego borrar el original
            Files.copy(origen, destino, StandardCopyOption.REPLACE_EXISTING);
            try {
                Files.delete(origen);
            } catch (Exception ignored) {
                // Si no se puede borrar el original por bloqueo, lo dejamos (no es crítico)
            }
        }

        // E. Actualizar la Base de Datos
        record.setOriginPath(destino.toString());
        record.setStatus(FileStatus.NEW); // Cambiamos estado a Válido
        record.setCause("Validado Manualmente como: " + categoriaCorrecta);

        return repo.save(record);
    }
}