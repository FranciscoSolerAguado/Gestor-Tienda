package org.fran.gestortienda.Connection;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class H2Connection {
    private static Connection conn;
    private static Properties props = new Properties();

    // Bloque estático para cargar las propiedades una sola vez
    static {
        try (InputStream input = H2Connection.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Lo siento, no se pudo encontrar el archivo config.properties");
            } else {
                props.load(input);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Leemos las propiedades del fichero cargado, específicas para H2
    private static final String URL = props.getProperty("db.h2.url");
    private static final String USER = props.getProperty("db.h2.user");
    private static final String PASS = props.getProperty("db.h2.password");

    private H2Connection() {
    }

    public static Connection getConnection() {
        if (conn == null) {
            try {
                conn = DriverManager.getConnection(URL, USER, PASS);
                System.out.println("Conexión a H2 establecida.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return conn;
    }
}
