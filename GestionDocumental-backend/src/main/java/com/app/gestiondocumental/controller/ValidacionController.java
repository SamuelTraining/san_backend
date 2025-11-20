package com.app.gestiondocumental.controller;

import com.app.gestiondocumental.model.FileRecord;
import com.app.gestiondocumental.service.ValidacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/validacion")
@CrossOrigin(origins = "http://localhost:4200")
public class ValidacionController {

    private final ValidacionService service;

    public ValidacionController(ValidacionService service) {
        this.service = service;
    }

    @GetMapping("/pendientes")
    public ResponseEntity<List<FileRecord>> listarPendientes() {
        return ResponseEntity.ok(service.obtenerPendientes());
    }

    @PostMapping("/{id}/confirmar")
    public ResponseEntity<?> confirmarClasificacion(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        try {
            String categoria = payload.get("categoria");
            if (categoria == null || categoria.isEmpty()) {
                return ResponseEntity.badRequest().body("Falta la categoría");
            }

            FileRecord actualizado = service.validarManualmente(id, categoria);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al validar: " + e.getMessage());
        }
    }
}