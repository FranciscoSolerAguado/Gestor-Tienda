package org.fran.gestortienda.Connection;

import org.fran.gestortienda.DatabaseManager.ConfigManager;
import org.fran.gestortienda.utils.LoggerUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public class ConnectionFactory {

    private static final Logger LOGGER = LoggerUtil.getLogger();

    // 2. Variable estática para guardar la ÚNICA instancia de la conexión
    private static Connection connection = null;

    private ConnectionFactory() {
        // Constructor privado para evitar que se creen instancias
    }

    /**
     * Devuelve la instancia única de la conexión (Singleton).
     * Si la conexión no existe, la crea.
     * @return La conexión a la base de datos.
     * @throws SQLException si ocurre un error al conectar.
     */
    public static Connection getConnection() throws SQLException {
        // 3. Si la conexión no ha sido creada todavía...
        if (connection == null || connection.isClosed()) {
            try {
                // Leemos la configuración desde ConfigManager
                String dbType = ConfigManager.getActiveDatabaseType();
                String url = ConfigManager.getProperty("db." + dbType + ".url");
                String user = ConfigManager.getProperty("db." + dbType + ".user");
                String pass = ConfigManager.getProperty("db." + dbType + ".password");

                if (url == null) {
                    throw new SQLException("No se encontró la URL para la base de datos: " + dbType);
                }

                LOGGER.info("Creando nueva conexión a la base de datos: " + dbType);
                // Creamos la conexión y la guardamos en nuestra variable estática
                connection = DriverManager.getConnection(url, user, pass);
                LOGGER.info("Conexión a " + dbType.toUpperCase() + " establecida correctamente.");

            } catch (SQLException e) {
                LOGGER.severe("FALLO CRÍTICO: No se pudo crear la conexión a la base de datos.");
                throw e; // Relanzamos la excepción para que la aplicación principal la capture
            }
        }
        // 4. Devolvemos la conexión existente
        return connection;
    }

    /**
     * Cierra la conexión si está abierta.
     * Se puede llamar al final de la aplicación.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                LOGGER.info("Conexión a la base de datos cerrada.");
            } catch (SQLException e) {
                LOGGER.severe("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
}