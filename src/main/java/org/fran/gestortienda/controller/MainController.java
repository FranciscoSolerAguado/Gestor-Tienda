package org.fran.gestortienda.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.fran.gestortienda.MainApp;
import org.fran.gestortienda.utils.LoggerUtil;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

public class MainController {
    private static final Logger LOGGER = LoggerUtil.getLogger();

    // 1. Este fx:id ahora se inyectará correctamente desde el BorderPane raíz
    @FXML
    private BorderPane mainPane;

    @FXML
    private HBox topBar;


    @FXML
    public void initialize() {
        LOGGER.info("MainController inicializado.");
    }

    /**
     * Maneja el evento de clic en el botón "Clientes".
     * Este método es llamado por el onAction del FXML.
     */
    @FXML
    void handleClientesClick(ActionEvent event) {
        LOGGER.info("Botón Clientes presionado. Cargando vista de clientes...");
        loadView("/org/fran/gestortienda/ui/clientes.fxml");
    }

    // Puedes añadir más métodos para los otros botones aquí
    // @FXML void handleProductosClick(ActionEvent event) { ... }

    /**
     * Método reutilizable para cargar una vista FXML en el centro del BorderPane.
     */
    private void loadView(String fxmlPath) {
        try {
            URL viewUrl = MainApp.class.getResource(fxmlPath);
            if (viewUrl == null) {
                LOGGER.severe("Recurso FXML no encontrado: " + fxmlPath);
                return;
            }

            Parent view = FXMLLoader.load(viewUrl);

            // 3. Ahora mainPane no será null y esto funcionará.
            mainPane.setCenter(view);
            LOGGER.info("Vista '" + fxmlPath + "' cargada en el panel central.");

        } catch (IOException e) {
            LOGGER.severe("Error de E/S al cargar la vista FXML: " + fxmlPath);
            e.printStackTrace();
        }
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