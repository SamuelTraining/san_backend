package com.app.gestiondocumental.util; // Paquete correcto

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ManifestGenerator {

    private final Path manifestDir;
    private final List<Map<String, Object>> entries;
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    // Constructor: Recibe la carpeta donde guardar los manifiestos
    public ManifestGenerator(Path manifestDir) throws IOException {
        if (!Files.exists(manifestDir)) {
            Files.createDirectories(manifestDir); // Crea la carpeta si no existe
        }
        this.manifestDir = manifestDir;
        this.entries = new ArrayList<>();
    }

    // Método para añadir información de un archivo
    public void addEntry(Map<String, Object> entry) {
        if (entry != null) {
            entries.add(entry);
        }
    }

    // Método para escribir el archivo JSON
    public Path write(String repoName) throws IOException {
        String timestamp = LocalDateTime.now().format(dtf);
        String filename = "manifest-" + repoName + "-" + timestamp + ".json";
        Path manifestPath = manifestDir.resolve(filename);

        // Usaremos una forma muy simple de escribir JSON (sin librerías externas)
        try (BufferedWriter writer = Files.newBufferedWriter(manifestPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            writer.write("[\n"); // Inicio de la lista JSON
            for (int i = 0; i < entries.size(); i++) {
                Map<String, Object> entry = entries.get(i);
                writer.write("  {\n"); // Inicio del objeto JSON
                int keyCount = 0;
                for (Map.Entry<String, Object> pair : entry.entrySet()) {
                    writer.write("    \"");
                    writer.write(pair.getKey());
                    writer.write("\": ");
                    Object value = pair.getValue();
                    if (value instanceof String || value instanceof Path) {
                        writer.write("\"");
                        // Escapar comillas dobles y barras invertidas en el valor
                        writer.write(value.toString().replace("\\", "\\\\").replace("\"", "\\\""));
                        writer.write("\"");
                    } else if (value == null) {
                        writer.write("null");
                    } else { // Asumir número o booleano
                        writer.write(value.toString());
                    }
                    keyCount++;
                    if (keyCount < entry.size()) {
                        writer.write(","); // Coma entre claves, excepto la última
                    }
                    writer.write("\n");
                }
                writer.write("  }"); // Fin del objeto JSON
                if (i < entries.size() - 1) {
                    writer.write(","); // Coma entre objetos, excepto el último
                }
                writer.write("\n");
            }
            writer.write("]\n"); // Fin de la lista JSON
        }

        return manifestPath;
    }
}