package org.fran.gestortienda.Connection;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionFactory {
    // Ya no se necesita una variable estática para la conexión.
    // private static Connection conn;

    private static final Properties props = new Properties();

    // El bloque estático para cargar las propiedades es correcto y se mantiene.
    static {
        try (InputStream input = ConnectionFactory.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.err.println("Error: No se pudo encontrar el archivo config.properties");
            } else {
                props.load(input);
            }
        } catch (Exception e) {
            System.err.println("Error al cargar el archivo de configuración.");
            e.printStackTrace();
        }
    }

    // El constructor privado ya no es necesario, pero no molesta.
    private ConnectionFactory() {}

    /**
     * Crea y devuelve una NUEVA conexión a la base de datos cada vez que se llama.
     * La conexión debe ser cerrada por quien la solicita (usando try-with-resources).
     * @return una nueva conexión a la base de datos.
     * @throws SQLException si ocurre un error al conectar.
     */
    public static Connection getConnection() throws SQLException {
        // La lógica para decidir la base de datos se mantiene.
        String dbType = props.getProperty("db.active", "h2");
        System.out.println("Intentando conectar a la base de datos: " + dbType);

        String url = props.getProperty("db." + dbType + ".url");
        String user = props.getProperty("db." + dbType + ".user");
        String pass = props.getProperty("db." + dbType + ".password");

        if (url == null) {
            throw new SQLException("No se encontró la URL para la base de datos: " + dbType);
        }

        // La línea clave: siempre crea y devuelve una nueva conexión.
        Connection newConnection = DriverManager.getConnection(url, user, pass);
        System.out.println("Conexión a " + dbType.toUpperCase() + " establecida.");
        return newConnection;
    }
}

