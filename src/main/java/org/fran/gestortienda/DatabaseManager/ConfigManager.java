package org.fran.gestortienda.DatabaseManager;

import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {

    private static final Properties properties = new Properties();
    private static final String CONFIG_FILE = "/config.properties";

    // Bloque estático para cargar el fichero una sola vez al iniciar la app
    static {
        try (InputStream input = ConfigManager.class.getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                System.err.println("No se pudo encontrar el fichero de configuración: " + CONFIG_FILE);
            } else {
                properties.load(input);
            }
        } catch (Exception e) {
            System.err.println("Error al cargar el fichero de configuración.");
            e.printStackTrace();
        }
    }

    /**
     * Obtiene una propiedad del fichero de configuración.
     * @param key La clave de la propiedad (ej: "db.active").
     * @return El valor de la propiedad.
     */
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * Devuelve el tipo de base de datos activa ("h2" o "mysql").
     */
    public static String getActiveDatabaseType() {
        return properties.getProperty("db.active", "h2"); // "h2" como valor por defecto
    }
}
