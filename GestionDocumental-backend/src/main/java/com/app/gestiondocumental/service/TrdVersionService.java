package com.app.gestiondocumental.service;

import com.app.gestiondocumental.model.TrdVersion;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface TrdVersionService {

    TrdVersion procesarArchivo(MultipartFile file, String nombre, String vigenciaDesde, String vigenciaHasta, String estado) throws IOException;

    List<TrdVersion> findAll();

    TrdVersion findByFecha(String fecha);
}
