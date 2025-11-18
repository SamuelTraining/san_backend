package com.app.gestiondocumental.service.impl;

import com.app.gestiondocumental.model.TrdVersion;
import com.app.gestiondocumental.repository.TrdVersionRepository;
import com.app.gestiondocumental.service.TrdVersionService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
public class TrdVersionServiceImpl implements TrdVersionService {

    private final TrdVersionRepository repository;

    private static final Set<String> ALLOWED_EXT = Set.of(".xlsx", ".xls", ".csv", ".pdf", ".docx", ".doc");

    public TrdVersionServiceImpl(TrdVersionRepository repository) {
        this.repository = repository;
    }

    @Override
    public TrdVersion procesarArchivo(MultipartFile file, String nombre, String vigenciaDesde, String vigenciaHasta, String estado) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("Archivo vacío o no proporcionado.");
        }

        String originalName = file.getOriginalFilename();
        String lower = (originalName != null) ? originalName.toLowerCase() : "";
        boolean ok = ALLOWED_EXT.stream().anyMatch(lower::endsWith);
        if (!ok) {
            throw new IOException("Formato no soportado. Solo se aceptan XLSX, XLS, CSV, PDF, DOCX, DOC.");
        }

        TrdVersion version = new TrdVersion();
        version.setNombre(nombre);
        version.setVigenciaDesde(LocalDate.parse(vigenciaDesde));
        version.setVigenciaHasta((vigenciaHasta != null && !vigenciaHasta.isEmpty()) ? LocalDate.parse(vigenciaHasta) : null);
        version.setEstado(estado);

        version.setNombreArchivo(originalName);
        version.setTipoArchivo(file.getContentType());
        version.setArchivo(file.getBytes());

        return repository.save(version);
    }

    @Override
    public List<TrdVersion> findAll() {
        return repository.findAll();
    }

    @Override
    public TrdVersion findByFecha(String fecha) {
        LocalDate f = LocalDate.parse(fecha);
        return repository.findByFecha(f);
    }
}
