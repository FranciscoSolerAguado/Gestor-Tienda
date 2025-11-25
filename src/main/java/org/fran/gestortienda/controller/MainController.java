package org.fran.gestortienda.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.control.Button;
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
    @FXML
    private BorderPane mainPane;
    @FXML
    private HBox topBar;
    @FXML
    private Button btnVentas;
    @FXML
    private Button btnClientes;
    @FXML
    private Button btnProveedores;
    @FXML
    private Button btnProductos;

    @FXML
    public void initialize() {
        // Acción de ejemplo: abrir consola. Sustituye por la navegación real.
        btnVentas.setOnAction(e -> System.out.println("Abrir Ventas"));
        btnClientes.setOnAction(e -> System.out.println("Abrir Clientes"));
        btnProveedores.setOnAction(e -> System.out.println("Abrir Proveedores"));
        btnProductos.setOnAction(e -> System.out.println("Abrir Productos"));
    }

    /**
     * Maneja el evento de clic en el botón "Clientes".
     * Carga la vista de clientes (clientes.fxml) en el panel central.
     *
     * @param event El evento de acción.
     */
    @FXML
    void handleClientesClick(ActionEvent event) {
        System.out.println("Botón Clientes presionado. Cargando vista de clientes...");
        loadView("/org/fran/gestortienda/ui/clientes.fxml");
    }

    /**
     * Método reutilizable para cargar una vista FXML en el centro del BorderPane.
     * VERSIÓN MEJORADA: Usa MainApp.class para una resolución de ruta más fiable.
     *
     * @param fxmlPath La ruta al archivo FXML, absoluta desde la carpeta 'resources'.
     */
    private void loadView(String fxmlPath) {
        try {
            // --- CAMBIO CLAVE AQUÍ ---
            // Usamos MainApp.class.getResource() en lugar de getClass().getResource().
            // Es más robusto porque MainApp es el punto de entrada de la aplicación.
            URL viewUrl = MainApp.class.getResource(fxmlPath);

            if (viewUrl == null) {
                System.err.println("ERROR CRÍTICO: El recurso FXML no se encuentra en el classpath: " + fxmlPath);
                // Opcional: Mostrar una alerta al usuario
                // Alert alert = new Alert(Alert.AlertType.ERROR);
                // alert.setTitle("Error de Carga");
                // alert.setHeaderText("No se pudo cargar la vista");
                // alert.setContentText("El archivo " + fxmlPath + " no fue encontrado.");
                // alert.showAndWait();
                return;
            }

            // Cargar el FXML
            Parent view = FXMLLoader.load(viewUrl);

            // Establecer la nueva vista en el centro del BorderPane
            mainPane.setCenter(view);
            System.out.println("Vista '" + fxmlPath + "' cargada correctamente en el panel central.");

        } catch (IOException e) {
            System.err.println("Error de E/S al cargar la vista FXML: " + fxmlPath);
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
