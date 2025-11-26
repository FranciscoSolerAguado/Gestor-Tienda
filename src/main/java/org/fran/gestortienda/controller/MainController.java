package org.fran.gestortienda.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
    @FXML
    private VBox rightPanel;

    // Guardamos una referencia al controlador de la vista activa
    private Object activeController;

    @FXML
    public void initialize() {
        LOGGER.info("MainController inicializado.");
        if (rightPanel != null) {
            rightPanel.setVisible(false);
            rightPanel.setManaged(false);
        }
    }

    // --- Métodos de Navegación ---

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

    /**
     * Método reutilizable para cargar una vista FXML en el centro del BorderPane.
     * VERSIÓN CORREGIDA: Ahora instancia FXMLLoader para obtener el controlador.
     */
    private void loadView(String fxmlPath) {
        try {
            URL viewUrl = MainApp.class.getResource(fxmlPath);
            if (viewUrl == null) {
                LOGGER.severe("Recurso FXML no encontrado: " + fxmlPath);
                return;
            }

            // 1. CREAR UNA INSTANCIA DEL CARGADOR
            FXMLLoader loader = new FXMLLoader(viewUrl);

            // 2. CARGAR LA VISTA USANDO LA INSTANCIA
            Parent view = loader.load();

            // 3. OBTENER Y GUARDAR EL CONTROLADOR
            activeController = loader.getController();
            LOGGER.info("Controlador activo establecido en: " + (activeController != null ? activeController.getClass().getName() : "null"));

            // 4. ESTABLECER LA VISTA EN EL CENTRO
            mainPane.setCenter(view);
            LOGGER.info("Vista '" + fxmlPath + "' cargada en el panel central.");

            if (rightPanel != null) {
                rightPanel.setVisible(true);
                rightPanel.setManaged(true);
            }

        } catch (IOException e) {
            LOGGER.severe("Error de E/S al cargar la vista FXML: " + fxmlPath);
            e.printStackTrace();
        }
    }

    // --- Métodos de Acción de la Barra Derecha ---

    @FXML
    private void handleDeleteClick() {
        LOGGER.info("Botón de borrado presionado.");
        if (activeController instanceof ClientesController) {
            ((ClientesController) activeController).borrarSeleccionados();
        } else {
            // Este log nos dirá por qué falla si el 'if' es falso
            LOGGER.warning("El controlador activo no es una instancia de ClientesController. Es: " +
                    (activeController != null ? activeController.getClass().getName() : "null"));
        }
    }

    // --- Métodos de Control de Ventana (sin cambios) ---

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