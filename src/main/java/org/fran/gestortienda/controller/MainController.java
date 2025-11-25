package org.fran.gestortienda.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox; // Importamos VBox
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.fran.gestortienda.MainApp;
import org.fran.gestortienda.utils.LoggerUtil;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

public class MainController {
    private static final Logger LOGGER = LoggerUtil.getLogger();

    @FXML
    private BorderPane mainPane;

    @FXML
    private HBox topBar;

    // 2. INYECTAMOS EL PANEL DERECHO
    @FXML
    private VBox rightPanel;

    @FXML
    public void initialize() {
        LOGGER.info("MainController inicializado.");
        // 3. OCULTAMOS EL PANEL DERECHO AL INICIAR
        if (rightPanel != null) {
            rightPanel.setVisible(false);
            rightPanel.setManaged(false); // También evita que ocupe espacio
        }
    }

    @FXML
    void handleClientesClick(ActionEvent event) {
        LOGGER.info("Botón Clientes presionado. Cargando vista de clientes...");
        loadView("/org/fran/gestortienda/ui/clientes.fxml");
    }

    @FXML
    void handleProveedoresClick(ActionEvent event) {
        LOGGER.info("Botón Proveedores presionado. Cargando vista de proveedores...");
        // loadView("/org/fran/gestortienda/ui/proveedores.fxml");
    }

    @FXML
    void handleProductosClick(ActionEvent event) {
        LOGGER.info("Botón Productos presionado. Cargando vista de productos...");
        // loadView("/org/fran/gestortienda/ui/productos.fxml");
    }

    @FXML
    void handleVentasClick(ActionEvent event) {
        LOGGER.info("Botón Ventas presionado. Cargando vista de ventas...");
        // loadView("/org/fran/gestortienda/ui/ventas.fxml");
    }

    private void loadView(String fxmlPath) {
        try {
            URL viewUrl = MainApp.class.getResource(fxmlPath);
            if (viewUrl == null) {
                LOGGER.severe("Recurso FXML no encontrado: " + fxmlPath);
                return;
            }

            Parent view = FXMLLoader.load(viewUrl);
            mainPane.setCenter(view);
            LOGGER.info("Vista '" + fxmlPath + "' cargada en el panel central.");

            // 4. MOSTRAMOS EL PANEL DERECHO CUANDO SE CARGA UNA VISTA
            if (rightPanel != null) {
                rightPanel.setVisible(true);
                rightPanel.setManaged(true);
            }

        } catch (IOException e) {
            LOGGER.severe("Error de E/S al cargar la vista FXML: " + fxmlPath);
            e.printStackTrace();
        }
    }

    // --- Tus métodos para controlar la ventana (sin cambios) ---

    @FXML
    private void handleMinimize() {
        Stage stage = (Stage) topBar.getScene().getWindow();
        stage.setIconified(true);
    }

    private boolean isMaximized = false;
    @FXML
    private void handleToggleMaximize() {
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

    @FXML
    private void handleClose() {
        Platform.exit();
    }
}