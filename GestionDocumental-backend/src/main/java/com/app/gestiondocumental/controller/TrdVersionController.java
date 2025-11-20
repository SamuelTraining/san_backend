package com.app.gestiondocumental.controller;

import com.app.gestiondocumental.dto.ia.RespuestaIaDto;
import com.app.gestiondocumental.model.FileRecord;
import com.app.gestiondocumental.model.FileStatus;
import com.app.gestiondocumental.model.TrdVersion;
import com.app.gestiondocumental.repository.FileRecordRepository; // <-- NUEVO IMPORT
import com.app.gestiondocumental.service.FileRecordService;
import com.app.gestiondocumental.service.IaClasificacionService;
import com.app.gestiondocumental.service.TrdVersionService;
import com.app.gestiondocumental.util.FileUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.Optional; // <-- NUEVO IMPORT

@RestController
@RequestMapping("/api/trd/version")
@CrossOrigin(origins = "http://localhost:4200")
public class TrdVersionController {

    private static final Logger log = LoggerFactory.getLogger(TrdVersionController.class);

    // --- CONFIGURACIÓN DE RUTAS ---
    private static final String RUTA_TEMPORAL = "C:/uploads/Pruebas";
    private static final String RUTA_CLASIFICADOS = "C:/Clasificados";
    private static final String RUTA_DUPLICADOS = "C:/Clasificados/Duplicados"; // <-- CARPETA DE DUPLICADOS

    private final TrdVersionService service;
    private final IaClasificacionService iaService;
    private final FileRecordService fileRecordService;
    private final FileRecordRepository fileRecordRepository; // <-- NECESITAMOS EL REPO PARA CONSULTAR

    public TrdVersionController(TrdVersionService service,
                                IaClasificacionService iaService,
                                FileRecordService fileRecordService,
                                FileRecordRepository fileRecordRepository) {
        this.service = service;
        this.iaService = iaService;
        this.fileRecordService = fileRecordService;
        this.fileRecordRepository = fileRecordRepository;
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> uploadTrd(
            @RequestPart("file") MultipartFile file,
            @RequestPart("nombre") String nombre,
            @RequestPart("vigenciaDesde") String vigenciaDesde,
            @RequestPart(value = "vigenciaHasta", required = false) String vigenciaHasta,
            @RequestPart("estado") String estado
    ) {
        try {
            log.info("📥 Recibiendo archivo: {}", file.getOriginalFilename());

            // --- 1. GUARDAR TEMPORALMENTE ---
            Path tempDir = Paths.get(RUTA_TEMPORAL);
            if (!Files.exists(tempDir)) Files.createDirectories(tempDir);

            String fileName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "doc_" + System.currentTimeMillis() + ".pdf";
            Path tempFilePath = tempDir.resolve(fileName);

            try (var inputStream = file.getInputStream()) {
                Files.copy(inputStream, tempFilePath, StandardCopyOption.REPLACE_EXISTING);
            }
            log.info("✅ Archivo guardado temporalmente en: {}", tempFilePath);

            // --- 2. CALCULAR MD5 Y VERIFICAR DUPLICADOS ---
            String md5 = "";
            try {
                md5 = FileUtils.computeMD5(tempFilePath);
            } catch (Exception e) {
                md5 = "error-" + System.currentTimeMillis();
            }

            Optional<FileRecord> duplicadoExistente = fileRecordRepository.findByMd5(md5);

            Path finalPath = tempFilePath; // Ruta final del archivo
            FileRecord rec = new FileRecord();

            // Datos comunes
            rec.setSize(file.getSize());
            rec.setMime(file.getContentType());
            rec.setMtime(Instant.now());
            rec.setCtime(Instant.now());
            rec.setProcessedAt(Instant.now());
            rec.setMd5(md5);

            if (duplicadoExistente.isPresent()) {
                // === ES UN DUPLICADO ===
                log.warn("⚠️ DUPLICADO DETECTADO. MD5 ya existe en la BD: {}", md5);

                // Crear carpeta de duplicados si no existe
                Path dupDir = Paths.get(RUTA_DUPLICADOS);
                if (!Files.exists(dupDir)) Files.createDirectories(dupDir);

                // Mover a carpeta duplicados (con nombre único para no sobrescribir otros duplicados)
                String dupName = "dup_" + System.currentTimeMillis() + "_" + fileName;
                finalPath = dupDir.resolve(dupName);

                moverArchivoSeguro(tempFilePath, finalPath);

                // Configurar registro como DUPLICADO
                rec.setStatus(FileStatus.DUPLICATE);
                rec.setOriginalId(duplicadoExistente.get().getId()); // Referencia al original
                rec.setCause("Es copia del archivo ID: " + duplicadoExistente.get().getId());

            } else {
                // === ES UN ARCHIVO NUEVO (FLUJO IA) ===
                log.info("✨ Archivo nuevo. Consultando a la IA...");

                RespuestaIaDto aiResponse = null;
                try {
                    aiResponse = iaService.clasificarArchivo(tempFilePath.toString()).block();
                } catch (Exception e) {
                    log.error("⚠️ Error IA: {}", e.getMessage());
                }

                String infoIA = "IA no disponible";

                if (aiResponse != null && aiResponse.getCategoria_predicha() != null) {
                    String categoria = aiResponse.getCategoria_predicha();
                    infoIA = "IA: " + categoria + " (" + (aiResponse.getConfianza() * 100) + "%)";

                    // Crear carpeta de categoría
                    Path categoryDir = Paths.get(RUTA_CLASIFICADOS, categoria);
                    if (!Files.exists(categoryDir)) Files.createDirectories(categoryDir);

                    // Mover a carpeta de categoría
                    finalPath = categoryDir.resolve(fileName);
                    moverArchivoSeguro(tempFilePath, finalPath);

                    log.info("📂 Organizado en: {}", finalPath);

                    // Estado según confianza
                    if ("CLASIFICACIÓN AUTOMÁTICA (Confiable)".equals(aiResponse.getEstado_validacion())) {
                        rec.setStatus(FileStatus.NEW);
                    } else {
                        rec.setStatus(FileStatus.NO_PROCESABLE);
                    }
                } else {
                    // Si la IA falla, se queda en temporal o se mueve a 'NoProcesables' (opcional)
                    rec.setStatus(FileStatus.NO_PROCESABLE);
                    infoIA = "Error de análisis";
                }
                rec.setCause(infoIA);
            }

            // --- 3. GUARDAR REGISTRO EN BD ---
            rec.setOriginPath(finalPath.toString());
            fileRecordService.saveOrGetExisting(rec);
            log.info("💾 Registro guardado en BD (Status: {})", rec.getStatus());

            // --- 4. PROCESO TRD NORMAL ---
            TrdVersion saved = service.procesarArchivo(file, nombre, vigenciaDesde, vigenciaHasta, estado);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);

        } catch (IOException ex) {
            return ResponseEntity.badRequest().body("Error de E/S: " + ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno: " + ex.getMessage());
        }
    }

    // Método auxiliar para mover archivos de forma segura en Windows
    private void moverArchivoSeguro(Path origen, Path destino) {
        try {
            Files.move(origen, destino, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.warn("⚠️ Archivo bloqueado, intentando copiar y borrar...");
            try {
                Files.copy(origen, destino, StandardCopyOption.REPLACE_EXISTING);
                Files.delete(origen);
            } catch (IOException ex) {
                log.error("❌ No se pudo mover el archivo: {}", ex.getMessage());
            }
        }
    }

    @GetMapping(params = "fecha")
    public ResponseEntity<?> getVersionByFecha(@RequestParam String fecha) {
        try {
            TrdVersion v = service.findByFecha(fecha);
            if (v == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(v);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Error en fecha.");
        }
    }
}