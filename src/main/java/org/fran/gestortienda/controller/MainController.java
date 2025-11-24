package org.fran.gestortienda.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.fran.gestortienda.utils.LoggerUtil;

import java.util.logging.Logger;

public class MainController {
    private static final Logger LOGGER = LoggerUtil.getLogger();

    @FXML private HBox topBar;
    @FXML private Button btnVentas;
    @FXML private Button btnClientes;
    @FXML private Button btnProveedores;
    @FXML private Button btnProductos;

    @FXML
    public void initialize() {
        // Acción de ejemplo: abrir consola. Sustituye por la navegación real.
        btnVentas.setOnAction(e -> System.out.println("Abrir Ventas"));
        btnClientes.setOnAction(e -> System.out.println("Abrir Clientes"));
        btnProveedores.setOnAction(e -> System.out.println("Abrir Proveedores"));
        btnProductos.setOnAction(e -> System.out.println("Abrir Productos"));
    }

    /**
     * Metodo que maneja el minimizado de la pantalla
     */
    @FXML
    private void handleMinimize() {
        LOGGER.info("Minimizando la pantalla...");
        Stage stage = (Stage) topBar.getScene().getWindow();
        stage.setIconified(true);
    }

    private boolean isMaximized = false;

    /**
     * Metodo que maneja el maximizado de la pantalla
     */
    @FXML
    private void handleToggleMaximize() {
        LOGGER.info("Maximización de la pantalla...");
        Stage stage = (Stage) topBar.getScene().getWindow();
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        if (isMaximized) {
            stage.setWidth(1200);
            stage.setHeight(800);
            stage.setX(screenBounds.getMinX() + (screenBounds.getWidth() - 1000) / 2);
            stage.setY(screenBounds.getMinY() + (screenBounds.getHeight() - 700) / 2);
            isMaximized = false;
        } else {
            stage.setX(screenBounds.getMinX());
            stage.setY(screenBounds.getMinY());
            stage.setWidth(screenBounds.getWidth());
            stage.setHeight(screenBounds.getHeight());
            isMaximized = true;
        }
    }

    /**
     * Metodo que maneja el cierre de la pantalla
     */
    @FXML
    private void handleClose() {
        LOGGER.info("Cerrando la pantalla...");
        Platform.exit();
    }
}
