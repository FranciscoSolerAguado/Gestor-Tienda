package org.fran.gestortienda.utils;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggerUtil {
    private static final Logger logger = Logger.getLogger("Gestor-Tienda Logger");

    static {
        try {
            // Configurar el FileHandler para escribir en gestor-tienda.log
            FileHandler fileHandler = new FileHandler("gestor-tienda.log", true);
            fileHandler.setLevel(Level.INFO); // Nivel de registro
            fileHandler.setFormatter(new SimpleFormatter()); // Formato simple
            logger.addHandler(fileHandler);

            // Configurar el nivel global del logger
            logger.setLevel(Level.INFO);
        } catch (IOException e) {
            System.err.println("Error al configurar el logger: " + e.getMessage());
        }
    }

    public static Logger getLogger() {
        return logger;
    }
}
