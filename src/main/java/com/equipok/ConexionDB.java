package com.equipok;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {
    private static final String URL = "jdbc:mysql://localhost:3306/restaurante_db";
    private static final String USER = "restaurante_user"; 
    private static final String PASS = "12345"; 

    public static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}