package com.app.gestiondocumental.DB; // <-- ¡ESTA LÍNEA FALTABA!

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class FileReport {

    public static void showReport(String tipo) {
        // El error 'Cannot resolve symbol DBConnection'
        // desaparecerá en cuanto añadas la línea 'package' de arriba.
        try (Connection conn = DBConnection.connect()) {
            if (conn == null) return;

            String sql; // Eliminamos la inicialización redundante ""

            if (tipo.equalsIgnoreCase("duplicados")) {
                sql = "SELECT nombre, ruta, estado FROM trd_archivo WHERE estado = 'duplicado'";
            } else if (tipo.equalsIgnoreCase("no_procesables")) {
                sql = "SELECT nombre, ruta, motivo FROM trd_archivo WHERE estado = 'no_procesable'";
            } else {
                System.out.println("Tipo de reporte no válido.");
                return;
            }

            Statement stat = conn.createStatement();
            ResultSet rs = stat.executeQuery(sql);

            // (Aquí faltaría el código para imprimir los resultados del ResultSet)
            // ej. while(rs.next()) { ... }

        } catch (Exception e) {
            System.out.println("Error al generar reporte: " + e.getMessage());
        }
    }
}