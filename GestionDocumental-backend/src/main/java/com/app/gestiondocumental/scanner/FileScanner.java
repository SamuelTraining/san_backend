package com.app.gestiondocumental.scanner; // <-- ¡ESTA LÍNEA FALTABA!

import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.sql.Timestamp;
import java.util.*;

public class FileScanner {

    public static List<Map<String, Object>> scanFolder(String path) {
        List<Map<String, Object>> files = new ArrayList<>();
        File folder = new File(path);

        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("Carpeta no válida: " + path);
            return files;
        }

        for (File f : Objects.requireNonNull(folder.listFiles())) {
            if (f.isFile()) {
                Map<String, Object> info = new HashMap<>();
                info.put("nombre", f.getName());
                info.put("ruta", f.getAbsolutePath());
                info.put("tamano", f.length());
                info.put("fecha", new Timestamp(f.lastModified()));

                try {
                    String hash = calcularMD5(f);
                    info.put("hash_md5", hash);
                    info.put("estado", "procesado");
                    info.put("motivo", null);
                } catch (Exception e) {
                    info.put("hash_md5", null);
                    info.put("estado", "no_procesable");
                    info.put("motivo", e.getMessage());
                }

                files.add(info);
            }
        }
        return files;
    }

    private static String calcularMD5(File file) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        try (InputStream is = Files.newInputStream(file.toPath())) {
            DigestInputStream dis = new DigestInputStream(is, md);
            byte[] buffer = new byte[4096];
            while (dis.read(buffer) != -1) {
                // El 'while' vacío está correcto, solo lee el archivo
            }
        }
        byte[] digest = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}