package com.app.gestiondocumental.controller;

import com.app.gestiondocumental.dto.TrdDto;
import com.app.gestiondocumental.service.TrdService;
import com.app.gestiondocumental.service.TrdService.SearchResultItem; // Importa la clase interna
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional; // Import añadido (necesario para Optional)

@RestController
// Cambié "/api/trds" a "/api/trd" para ser consistente con el archivo original, puedes dejar "/trds" si prefieres
@RequestMapping("/api/trd")
@CrossOrigin(origins = "http://localhost:4200") // Añadido para Angular
public class TrdController {
    private final TrdService trdService;

    public TrdController(TrdService trdService) {
        this.trdService = trdService;
    }

    @GetMapping
    public ResponseEntity<List<TrdDto>> list(@RequestParam(value = "q", required = false) String q) {
        return ResponseEntity.ok(trdService.listTrds(q));
    }

    @GetMapping("/{id}")
    // --- CORREGIDO AQUÍ ---
    public ResponseEntity<TrdDto> get(@PathVariable Long id) {
        Optional<TrdDto> optDto = trdService.getTrdDetail(id); // Guarda el Optional
        // Usa el map y orElse para devolver 200 o 404
        return optDto.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/search")
    // --- CORREGIDO AQUÍ ---
    public ResponseEntity<List<SearchResultItem>> search(
            @PathVariable Long id,
            @RequestParam("q") String q) {
        return ResponseEntity.ok(trdService.searchWithinTrd(id, q));
    }
}