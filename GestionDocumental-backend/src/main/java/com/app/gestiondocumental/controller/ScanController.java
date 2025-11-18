package com.app.gestiondocumental.controller;

import com.app.gestiondocumental.service.ScanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/scan") // La URL base para este controlador
@CrossOrigin(origins = "http://localhost:4200") // Permite llamadas desde Angular
public class ScanController {

    @Autowired // Spring inyectará automáticamente el ScanService
    private ScanService scanService;

    // Endpoint para iniciar el escaneo
    // Se llamará con un POST a /api/scan?ruta=C:/MiCarpeta
    @PostMapping
    public ResponseEntity<String> escanearCarpeta(@RequestParam String ruta) {
        try {
            String resultado = scanService.iniciarEscaneoYProcesamiento(ruta);
            return ResponseEntity.ok(resultado); // Devuelve 200 OK con el mensaje
        } catch (Exception e) {
            // Captura cualquier error inesperado
            return ResponseEntity
                    .internalServerError() // Devuelve 500 Internal Server Error
                    .body("Error durante el escaneo: " + e.getMessage());
        }
    }

    // Podrías añadir un endpoint para los reportes aquí
    // @GetMapping("/reporte")
    // public ResponseEntity<?> verReporte(@RequestParam String tipo) { ... }
}
