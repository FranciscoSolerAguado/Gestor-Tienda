package org.fran.gestortienda.Connection;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class MySQLConnection {
    private static Connection conn;
    private static Properties props = new Properties();

    // Bloque estático para cargar las propiedades una sola vez
    static {
        try (InputStream input = MySQLConnection.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Lo siento, no se pudo encontrar el archivo config.properties");
            } else {
                props.load(input);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Leemos las propiedades del fichero cargado
    private static final String URL = props.getProperty("db.mysql.url");
    private static final String USER = props.getProperty("db.mysql.user");
    private static final String PASS = props.getProperty("db.mysql.password");

    private MySQLConnection() {
    }

    public static Connection getConnection() {
        if (conn == null) {
            try {
                conn = DriverManager.getConnection(URL, USER, PASS);
                System.out.println("Conexión a MySQL establecida.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return conn;
    }
}