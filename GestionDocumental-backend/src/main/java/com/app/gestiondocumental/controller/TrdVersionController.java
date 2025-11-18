package com.app.gestiondocumental.controller;

import com.app.gestiondocumental.model.TrdVersion;
import com.app.gestiondocumental.service.TrdVersionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/trd/version")
@CrossOrigin(origins = "http://localhost:4200")
public class TrdVersionController {

    private final TrdVersionService service;

    public TrdVersionController(TrdVersionService service) {
        this.service = service;
    }

    // 📥 Cargar archivo TRD
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> uploadTrd(
            @RequestPart("file") MultipartFile file,
            @RequestPart("nombre") String nombre,
            @RequestPart("vigenciaDesde") String vigenciaDesde,
            @RequestPart(value = "vigenciaHasta", required = false) String vigenciaHasta,
            @RequestPart("estado") String estado
    ) {
        try {
            TrdVersion saved = service.procesarArchivo(file, nombre, vigenciaDesde, vigenciaHasta, estado);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IOException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno: " + ex.getMessage());
        }
    }

    // 📋 Listar todas las TRD
    @GetMapping
    public List<TrdVersion> getAll() {
        return service.findAll();
    }

    // 🔍 Buscar versión vigente por fecha
    @GetMapping(params = "fecha")
    public ResponseEntity<?> getVersionByFecha(@RequestParam String fecha) {
        try {
            TrdVersion v = service.findByFecha(fecha);
            if (v == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(v);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Formato de fecha incorrecto. Use YYYY-MM-DD.");
        }
    }
}
