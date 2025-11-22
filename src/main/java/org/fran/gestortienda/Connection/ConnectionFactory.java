package org.fran.gestortienda.Connection;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionFactory {
    private static Connection conn;
    private static Properties props = new Properties();

    // Bloque estático para cargar las propiedades una sola vez
    static {
        try (InputStream input = ConnectionFactory.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.err.println("Error: No se pudo encontrar el archivo config.properties");
            } else {
                props.load(input);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ConnectionFactory() {
        // Constructor privado para el patrón Singleton
    }

    public static Connection getConnection() {
        if (conn == null) {
            try {
                // Lee el interruptor para decidir qué base de datos usar
                String dbType = props.getProperty("db.active", "h2"); // Usa h2 por defecto si no se especifica
                System.out.println("Intentando conectar a la base de datos: " + dbType);

                String url = props.getProperty("db." + dbType + ".url");
                String user = props.getProperty("db." + dbType + ".user");
                String pass = props.getProperty("db." + dbType + ".password");

                if (url == null) {
                    throw new SQLException("No se encontró la URL para la base de datos: " + dbType);
                }

                conn = DriverManager.getConnection(url, user, pass);
                System.out.println("Conexión a " + dbType.toUpperCase() + " establecida.");

            } catch (SQLException e) {
                System.err.println("Error al conectar a la base de datos.");
                e.printStackTrace();
            }
        }
        return conn;
    }
}

