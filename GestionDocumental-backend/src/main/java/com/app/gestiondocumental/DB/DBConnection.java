package com.app.gestiondocumental.DB; // <-- ESTA LÍNEA FALTABA

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:sqlite:trd.db";
    // Si usan PostgreSQL, cambia por:
    // "jdbc:postgresql://localhost:5432/trd"
    // y agrega usuario y contraseña

    public static Connection connect() {
        try {
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.out.println("Error de conexión: " + e.getMessage());
            return null;
        }
    }
}
