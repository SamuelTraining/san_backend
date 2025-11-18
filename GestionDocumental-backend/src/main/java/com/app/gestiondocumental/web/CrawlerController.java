package com.app.gestiondocumental.web;

import com.app.gestiondocumental.service.CrawlerService; // <-- 1. IMPORT CORREGIDO
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;

@RestController
@RequestMapping("/api/crawler")
public class CrawlerController {

    private final CrawlerService crawler;

    public CrawlerController(CrawlerService crawler) {
        this.crawler = crawler;
    }

    // acepta POST (producción) y GET (solo para pruebas rápidas)
    @RequestMapping(value = "/scan", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity<?> scan(@RequestParam("path") String path,
                                  @RequestParam(value = "mode", defaultValue = "READ_ONLY") String modeParam) throws Exception {

        // Sanitizar entrada: quitar comillas y espacios accidentales
        String modeClean = modeParam == null ? "READ_ONLY" :
                modeParam.replace("'", "").replace("\"", "").trim().toUpperCase();

        CrawlerService.Mode mode;
        try {
            mode = CrawlerService.Mode.valueOf(modeClean);
        } catch (IllegalArgumentException ex) {
            String msg = "Valor de 'mode' inválido: '" + modeParam + "'. Valores permitidos: READ_ONLY, PROCESS";
            // Lanzamos IllegalArgumentException para que el RestExceptionHandler lo traduzca a 400 con mensaje
            throw new IllegalArgumentException(msg);
        }

        Path repoPath = Path.of(path);
        var manifest = crawler.scan(repoPath, mode);
        return ResponseEntity.ok().body("Manifest generado: " + manifest.toAbsolutePath().toString());
    }
}