package org.fran.gestortienda.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.fran.gestortienda.DAO.ClienteDAO;
import org.fran.gestortienda.MainApp;
import org.fran.gestortienda.model.entity.Cliente;
import org.fran.gestortienda.utils.LoggerUtil;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
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

            // CREAR UNA INSTANCIA DEL CARGADOR
            FXMLLoader loader = new FXMLLoader(viewUrl);

            // CARGAR LA VISTA USANDO LA INSTANCIA
            Parent view = loader.load();

            // OBTENER Y GUARDAR EL CONTROLADOR
            activeController = loader.getController();
            LOGGER.info("Controlador activo establecido en: " + (activeController != null ? activeController.getClass().getName() : "null"));

            // ESTABLECER LA VISTA EN EL CENTRO
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


    /**
     * Maneja el clic en el botón de añadir (+).
     * Abre un diálogo para crear un nuevo registro según la vista activa.
     */
    @FXML
    private void handleAddClick() {
        LOGGER.info("Se ha hecho clic en el botón Añadir (+).");
        LOGGER.info("Comprobando el controlador activo...");

        if (activeController == null) {
            LOGGER.warning("El controlador activo es NULL. No se puede abrir el diálogo. ¿Has cargado una vista (Clientes, Productos, etc.) primero?");
            return;
        }

        LOGGER.info("El controlador activo es de tipo: " + activeController.getClass().getName());

        if (activeController instanceof ClientesController) {
            LOGGER.info("El controlador es de tipo ClientesController. Abriendo diálogo de nuevo cliente...");
            abrirDialogoNuevoCliente();
        } else {
            LOGGER.warning("El botón de añadir no tiene una acción definida para el controlador actual: " + activeController.getClass().getName());
            // Aquí podrías añadir lógica para otros tipos de controladores
            // if (activeController instanceof ProductosController) { ... }
        }
    }


    /**
     * Abre, gestiona y procesa el diálogo para añadir un nuevo cliente.
     * VERSIÓN MEJORADA: Usa un Stage modal personalizado.
     */
    private void abrirDialogoNuevoCliente() {
        try {
            // 1. Cargar el FXML del diálogo
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/org/fran/gestortienda/ui/add_cliente.fxml"));
            Parent view = loader.load();

            // 2. Crear un nuevo Stage (ventana) para el diálogo
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Añadir Nuevo Cliente");
            dialogStage.initModality(Modality.WINDOW_MODAL); // Bloquea la ventana principal
            dialogStage.initOwner(mainPane.getScene().getWindow()); // Asocia el diálogo a la ventana principal
            dialogStage.setResizable(false);

            Scene scene = new Scene(view);
            dialogStage.setScene(scene);

            // 3. Pasar el Stage al controlador del diálogo para que pueda cerrarse
            AddClienteController dialogController = loader.getController();
            dialogController.setDialogStage(dialogStage);

            // 4. Mostrar el diálogo y esperar a que se cierre
            dialogStage.showAndWait();

            // 5. Procesar el resultado (solo si se guardó)
            if (dialogController.isGuardado()) {
                Cliente nuevoCliente = dialogController.getNuevoCliente();
                if (nuevoCliente != null) {
                    new ClienteDAO().add(nuevoCliente);
                    LOGGER.info("Nuevo cliente guardado: " + nuevoCliente.getNombre());
                    ((ClientesController) activeController).cargarClientes(); // Refrescar la vista
                }
            }

        } catch (IOException | SQLException e) {
            LOGGER.severe("Error al abrir o procesar el diálogo de nuevo cliente.");
            e.printStackTrace();
        }
    }

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