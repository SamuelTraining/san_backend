package com.app.gestiondocumental.service;

// Asumiendo que FileScanner y FileProcessor están en estos paquetes
import com.app.gestiondocumental.scanner.FileScanner;
import com.app.gestiondocumental.processor.FileProcessor;
// OJO: FileReport usa JDBC manual, lo ideal sería refactorizarlo
// import com.app.gestiondocumental.DB.FileReport;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service // Marca esta clase como un Servicio de Spring
public class ScanService {

    public String iniciarEscaneoYProcesamiento(String ruta) {
        System.out.println("=== Iniciando escaneo para la ruta: " + ruta + " ===");

        // 1. Escanear la carpeta (usando tu clase FileScanner)
        List<Map<String, Object>> archivos = FileScanner.scanFolder(ruta);
        if (archivos.isEmpty()) {
            return "No se encontraron archivos o la carpeta no es válida.";
        }

        // 2. Procesar los archivos (usando tu clase FileProcessor)
        // ESTO INTENTARÁ USAR LA CONEXIÓN JDBC MANUAL (¡NO RECOMENDADO!)
        FileProcessor.processFiles(archivos);

        // 3. Generar un mensaje de resultado (el reporte lo llamaremos desde otro lado)
        String mensaje = "Escaneo y procesamiento completado para " + archivos.size() + " archivos.";
        System.out.println(mensaje);

        // Podrías devolver más detalles si quisieras
        return mensaje;
    }

    // Podrías añadir aquí un método para llamar a FileReport si lo refactorizas
    // public String generarReporte(String tipo) { ... }
}