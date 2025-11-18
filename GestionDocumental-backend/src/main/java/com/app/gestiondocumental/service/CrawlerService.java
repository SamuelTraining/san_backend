package com.app.gestiondocumental.service; // <-- PAQUETE CORREGIDO

// --- IMPORTS CORREGIDOS ---
import com.app.gestiondocumental.config.AppProperties;
import com.app.gestiondocumental.model.FileRecord;
import com.app.gestiondocumental.model.FileStatus;
import com.app.gestiondocumental.repository.FileRecordRepository;
import com.app.gestiondocumental.util.FileUtils;
import com.app.gestiondocumental.util.ManifestGenerator;
import com.app.gestiondocumental.dto.ia.RespuestaIaDto; // <-- IMPORT PARA LA IA
// import com.app.gestiondocumental.service.FileRecordService; // (Aún comentado)

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.*;

@Service
public class CrawlerService {

    public enum Mode { READ_ONLY, PROCESS }

    private static final Logger log = LoggerFactory.getLogger(CrawlerService.class);

    // --- Inyección de dependencias ---
    private final AppProperties props;
    private final FileRecordRepository repo;
    // private final FileRecordService fileRecordService; // (Aún comentado)
    private final IaClasificacionService iaService; // <-- NUEVO SERVICIO DE IA AÑADIDO

    // --- CONSTRUCTOR MODIFICADO (con IaClasificacionService) ---
    public CrawlerService(AppProperties props,
                          FileRecordRepository repo,
            /* FileRecordService fileRecordService, */
                          IaClasificacionService iaService) { // <-- AÑADIDO AQUÍ
        this.props = props;
        this.repo = repo;
        // this.fileRecordService = fileRecordService;
        this.iaService = iaService; // <-- AÑADIDO AQUÍ
    }

    public Path scan(Path repoPath, Mode mode) throws Exception {
        if (!Files.exists(repoPath) || !Files.isDirectory(repoPath)) {
            throw new IllegalArgumentException("El path especificado no existe o no es un directorio: " + repoPath);
        }

        String repoName = repoPath.getFileName() != null ? repoPath.getFileName().toString() : "repo";
        ManifestGenerator mg = new ManifestGenerator(Paths.get(props.getManifestDir()));

        Files.walkFileTree(repoPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                try {
                    if (!Files.isReadable(file) || !attrs.isRegularFile()) {
                        log.debug("Archivo no legible o no regular: {}", file);
                        return FileVisitResult.CONTINUE;
                    }

                    String md5 = FileUtils.computeMD5(file);
                    long size = attrs.size();
                    Instant mtime = FileUtils.getMtime(file);
                    Instant ctime = FileUtils.getCtime(file);
                    String mime = FileUtils.probeMime(file);

                    log.info("Procesando archivo: {} size={} md5={} mime={}", file.toAbsolutePath(), size, md5, mime);

                    Map<String, Object> entry = new LinkedHashMap<>();
                    entry.put("originPath", file.toString());
                    entry.put("size", size);
                    entry.put("md5", md5);
                    entry.put("mtime", mtime != null ? mtime.toString() : null);
                    entry.put("ctime", ctime != null ? ctime.toString() : null);
                    entry.put("mime", mime);

                    // --- ¡AQUÍ LLAMAMOS A LA IA! ---
                    log.info("Llamando a la IA para clasificar...");
                    // Usamos .block() para hacer la llamada síncrona (espera la respuesta)
                    RespuestaIaDto respuestaIA = iaService.clasificarArchivo(file.toString()).block();

                    if (respuestaIA == null || respuestaIA.getError() != null) {
                        log.error("La IA falló al clasificar: {}", (respuestaIA != null ? respuestaIA.getError() : "Respuesta nula"));
                        entry.put("status", "NO_PROCESABLE");
                        entry.put("cause", "IA_ERROR");
                    } else {
                        log.info("Respuesta de la IA: Categoria={}, Confianza={}",
                                respuestaIA.getCategoria_predicha(),
                                respuestaIA.getConfianza());
                        // Aquí puedes añadir los resultados de la IA al manifiesto
                        entry.put("ia_categoria", respuestaIA.getCategoria_predicha());
                        entry.put("ia_confianza", respuestaIA.getConfianza());
                        entry.put("ia_estado", respuestaIA.getEstado_validacion());
                        entry.put("ia_metadatos", respuestaIA.getMetadatos());
                    }


                    // --- Lógica de Duplicados (ahora usa 'repo' en lugar de 'fileRecordService') ---
                    Optional<FileRecord> existing = repo.findByMd5(md5);
                    log.info("Busqueda en BD por md5={} -> encontrado={}", md5, existing.isPresent());

                    if (existing.isPresent()) {
                        entry.put("status", "DUPLICATE");
                        entry.put("originalId", existing.get().getId());
                        if (mode == Mode.PROCESS) {
                            try {
                                Path target = Paths.get(props.getDuplicatesDir()).resolve(md5 + "-" + file.getFileName().toString());
                                FileUtils.movePreserveAttributes(file, target);

                                FileRecord rec = new FileRecord();
                                rec.setOriginPath(target.toString());
                                rec.setMd5(md5);
                                rec.setSize(size);
                                rec.setMtime(mtime);
                                rec.setCtime(ctime);
                                rec.setMime(mime);
                                rec.setStatus(FileStatus.DUPLICATE);
                                rec.setOriginalId(existing.get().getId());
                                rec.setProcessedAt(Instant.now());

                                // fileRecordService.saveOrGetExisting(rec); // Comentado
                                repo.save(rec); // Guardamos directamente con el repositorio
                                log.info("Archivo duplicado movido a: {}", target);
                            } catch (Exception e) {
                                log.error("Error moviendo duplicado {} : {}", file, e.getMessage());
                                entry.put("status", "NO_PROCESABLE");
                                entry.put("cause", "EXCEPTION_MOVE:" + e.getMessage());
                            }
                        }
                    } else {
                        boolean supported = props.getSupportedMimeTypes() != null && props.getSupportedMimeTypes().contains(mime);
                        if (!supported) {
                            entry.put("status", "NO_PROCESABLE");
                            entry.put("cause", "MIME_NOT_SUPPORTED:" + mime);
                            if (mode == Mode.PROCESS) {
                                // ... (lógica para mover y guardar 'NO_PROCESABLE' por MIME) ...
                            }
                        } else {
                            entry.put("status", "NEW");
                            if (mode == Mode.PROCESS) {
                                // ... (lógica para guardar 'NEW') ...
                            }
                        }
                    }

                    mg.addEntry(entry);
                } catch (Exception ex) {
                    log.error("Error procesando archivo {} : {}", file, ex.getMessage(), ex);
                    // ... (tu bloque catch) ...
                }
                return FileVisitResult.CONTINUE;
            }
        });

        Path manifest = mg.write(repoName);
        log.info("Manifest generado en {}", manifest.toAbsolutePath());
        return manifest;
    }
}