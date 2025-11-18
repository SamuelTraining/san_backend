package com.app.gestiondocumental.processor; // <-- 1. LÍNEA DE PAQUETE FALTANTE

import java.sql.*;
import java.util.*;
import com.app.gestiondocumental.DB.DBConnection; // <-- 2. IMPORT FALTANTE

public class FileProcessor {

    public static void processFiles(List<Map<String, Object>> files) {
        // Esta línea ahora funciona gracias al import
        try (Connection conn = DBConnection.connect()) {
            if (conn == null) return;

            String checkSQL = "SELECT id FROM trd_archivo WHERE hash_md5 = ?";
            String insertSQL = "INSERT INTO trd_archivo (nombre, ruta, tamano, hash_md5, estado, motivo) VALUES (?, ?, ?, ?, ?, ?)";

            PreparedStatement checkStmt = conn.prepareStatement(checkSQL);
            PreparedStatement insertStmt = conn.prepareStatement(insertSQL);

            int duplicados = 0;
            int noProc = 0;

            for (Map<String, Object> f : files) {
                String estado = (String) f.get("estado");
                String hash = (String) f.get("hash_md5");

                if (hash == null) {
                    estado = "no_procesable";
                    noProc++;
                } else {
                    checkStmt.setString(1, hash);
                    ResultSet rs = checkStmt.executeQuery();
                    if (rs.next()) {
                        estado = "duplicado";
                        duplicados++;
                    } else {
                        // El estado original ("procesado") se mantiene
                    }
                }

                insertStmt.setString(1, (String) f.get("nombre"));
                insertStmt.setString(2, (String) f.get("ruta"));
                insertStmt.setLong(3, (Long) f.get("tamano"));
                insertStmt.setString(4, hash);
                insertStmt.setString(5, estado);
                insertStmt.setString(6, (String) f.get("motivo"));
                insertStmt.executeUpdate();
            }

            System.out.println("Archivos duplicados: " + duplicados);
            System.out.println("No procesables: " + noProc);

        } catch (SQLException e) {
            System.out.println("Error en procesamiento: " + e.getMessage());
        }
    }
}