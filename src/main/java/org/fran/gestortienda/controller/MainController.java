package org.fran.gestortienda.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.fran.gestortienda.DAO.ClienteDAO;
import org.fran.gestortienda.DAO.ProductoDAO;
import org.fran.gestortienda.DAO.ProveedorDAO;
import org.fran.gestortienda.MainApp;
import org.fran.gestortienda.model.entity.Cliente;
import org.fran.gestortienda.model.entity.Producto;
import org.fran.gestortienda.model.entity.Proveedor;
import org.fran.gestortienda.utils.LoggerUtil;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.logging.Logger;

public class MainController {
    private static final Logger LOGGER = LoggerUtil.getLogger();

    @FXML
    private BorderPane mainPane;
    @FXML
    private HBox topBar;
    @FXML
    private VBox rightPanel;
    @FXML
    private String modoBusqueda = "Nombre";
    @FXML
    private TextField searchField;
    @FXML
    private MenuButton filterMenuButton;
    @FXML
    private Button sideBtnProveedores;
    @FXML
    private Button sideBtnProductos;
    @FXML
    private Button sideBtnClientes;
    @FXML
    private Button sideBtnVentas;


    // Guardamos una referencia al controlador de la vista activa
    private Object activeController;

    @FXML
    public void initialize() {
        LOGGER.info("MainController inicializado.");
        // LOG DE DEPURACIÓN: Comprobamos si el panel derecho se ha inyectado correctamente.
        LOGGER.info("Estado de rightPanel en initialize(): " + (rightPanel != null ? "Inyectado correctamente" : "¡¡¡ES NULL!!!"));

        if (rightPanel != null) {
            rightPanel.setVisible(false);
        }
    }

    // --- Métodos de Navegación ---

    @FXML
    void handleClientesClick(ActionEvent event) {
        LOGGER.info("Botón Clientes presionado. Cargando vista de clientes...");
        loadView("/org/fran/gestortienda/ui/clientes.fxml", "clientes");
    }


    @FXML
    void handleProveedoresClick(ActionEvent event) {
        LOGGER.info("Botón Proveedores presionado. Cargando vista de proveedores...");
        loadView("/org/fran/gestortienda/ui/proveedores.fxml", "proveedores");
    }

    @FXML
    void handleProductosClick(ActionEvent event) {
        LOGGER.info("Botón Productos presionado. Cargando vista de productos...");
        loadView("/org/fran/gestortienda/ui/productos.fxml", "productos");
    }

    @FXML
    void handleVentasClick(ActionEvent event) {
        loadView("/org/fran/gestortienda/ui/ventas.fxml", "ventas");
    }


    private void loadView(String fxmlPath, String viewName) {
        try {
            // LOG DE DEPURACIÓN: Comprobamos el estado del panel antes de hacer nada.
            LOGGER.info("--- Iniciando loadView para: " + fxmlPath + " ---");
            LOGGER.info("Estado de rightPanel ANTES de cargar FXML: " + (rightPanel != null ? "Existe" : "¡¡¡ES NULL!!!"));

            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource(fxmlPath));
            Parent view = loader.load();
            activeController = loader.getController();
            LOGGER.info("Controlador activo establecido en: " + (activeController != null ? activeController.getClass().getName() : "null"));

            mainPane.setCenter(view);
            LOGGER.info("Vista '" + fxmlPath + "' cargada en el panel central.");
            actualizarEstadoMenu(viewName);
            // LOG DE DEPURACIÓN: Comprobamos el panel justo antes de mostrarlo.
            if (rightPanel != null) {
                LOGGER.info("Haciendo visible el rightPanel. Su visibilidad actual es: " + rightPanel.isVisible());
                rightPanel.setVisible(true);
                LOGGER.info("Visibilidad de rightPanel establecida en true. Ahora es: " + rightPanel.isVisible());
                actualizarMenuFiltro(activeController);
            } else {
                LOGGER.severe("¡¡¡ERROR!!! rightPanel se ha vuelto NULL después de cargar la vista.");
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
        } else if (activeController instanceof ProveedoresController) {
            ((ProveedoresController) activeController).borrarSeleccionados();

        } else if (activeController instanceof VentasController) {
            ((VentasController) activeController).borrarSeleccionados();
        }else if (activeController instanceof ProductosController) {
            ((ProductosController) activeController).borrarSeleccionados();
        } else {
            LOGGER.warning("El controlador activo no es una instancia de ClientesController. Es: " +
                    (activeController != null ? activeController.getClass().getName() : "null"));
        }
    }

    /**
     * Método ayudante para cambiar el modo de búsqueda y el texto del prompt.
     */
    private void setModoBusqueda(String modo, String promptText) {
        this.modoBusqueda = modo;
        searchField.setPromptText(promptText);
        LOGGER.info("Modo de búsqueda cambiado a: " + modo);
    }

    private void actualizarMenuFiltro(Object controller) {
        if (filterMenuButton == null) return; // Guarda de seguridad

        filterMenuButton.getItems().clear();
        searchField.setDisable(false);
        filterMenuButton.setDisable(false);

        if (controller instanceof ClientesController) {
            MenuItem porNombre = new MenuItem("Por Nombre");
            porNombre.setOnAction(e -> setModoBusqueda("Nombre", "Buscar por Nombre..."));
            MenuItem porId = new MenuItem("Por ID");
            porId.setOnAction(e -> setModoBusqueda("ID", "Buscar por ID..."));
            MenuItem porDireccion = new MenuItem("Por Dirección");
            porDireccion.setOnAction(e -> setModoBusqueda("Direccion", "Buscar por Dirección..."));
            filterMenuButton.getItems().addAll(porNombre, porId, porDireccion);
            setModoBusqueda("Nombre", "Buscar por Nombre...");


        } else if (controller instanceof ProveedoresController) {
            // ESTA ES LA LÓGICA QUE DEBE FUNCIONAR
            MenuItem porNombre = new MenuItem("Por Nombre");
            porNombre.setOnAction(e -> setModoBusqueda("Nombre", "Buscar por Nombre..."));
            MenuItem porTelefono = new MenuItem("Por Teléfono");
            porTelefono.setOnAction(e -> setModoBusqueda("Telefono", "Buscar por Teléfono..."));
            MenuItem porCorreo = new MenuItem("Por Correo");
            porCorreo.setOnAction(e -> setModoBusqueda("Correo", "Buscar por Correo..."));
            filterMenuButton.getItems().addAll(porNombre, porTelefono, porCorreo);
            setModoBusqueda("Nombre", "Buscar por Nombre...");

        } else if (controller instanceof VentasController) {
            // --- LÓGICA AÑADIDA PARA VENTAS ---
            MenuItem porFecha = new MenuItem("Por Fecha");
            porFecha.setOnAction(e -> setModoBusqueda("Fecha", "Buscar por Fecha"));

            MenuItem porCliente = new MenuItem("Por ID Cliente");
            porCliente.setOnAction(e -> setModoBusqueda("Cliente", "Buscar por ID de Cliente..."));

            filterMenuButton.getItems().addAll(porFecha, porCliente);
            setModoBusqueda("Fecha", "Buscar por Fecha"); // Modo por defecto
        } else if (controller instanceof ProductosController) {

            MenuItem porNombre = new MenuItem("Por Nombre");
            porNombre.setOnAction(e -> setModoBusqueda("Nombre", "Buscar por Nombre..."));

            MenuItem porCategoria = new MenuItem("Por Categoría");
            porCategoria.setOnAction(e -> setModoBusqueda("Categoria", "Buscar por Categoría..."));

            MenuItem porProveedor = new MenuItem("Por Proveedor");
            porProveedor.setOnAction(e -> setModoBusqueda("Proveedor", "Buscar por Proveedor..."));

            filterMenuButton.getItems().addAll(porNombre, porCategoria, porProveedor);

            setModoBusqueda("Nombre", "Buscar por Nombre...");
        }
 else {
            // Estado por defecto
            MenuItem defaultItem = new MenuItem("Sin filtro");
            defaultItem.setDisable(true);
            filterMenuButton.getItems().add(defaultItem);
            searchField.setDisable(true);
            filterMenuButton.setDisable(true);
        }
    }


    /**
     * Maneja el clic en el botón de añadir (+).
     * Abre un diálogo para crear un nuevo registro según la vista activa.
     */
    @FXML
    private void handleAddClick() {
        LOGGER.info("Se ha hecho clic en el botón Añadir (+).");

        if (activeController instanceof ClientesController) {
            LOGGER.info("Controlador activo es ClientesController. Abriendo diálogo de nuevo cliente...");
            abrirDialogoNuevoCliente();
        } else if (activeController instanceof ProveedoresController) {
            LOGGER.info("Controlador activo es ProveedoresController. Abriendo diálogo de nuevo proveedor...");
            abrirDialogoNuevoProveedor();
        } else if (activeController instanceof VentasController) {
            LOGGER.info("Controlador activo es VentasController. Abriendo dialogo de nueva venta");
            abrirDialogoNuevaVenta();
        }else if (activeController instanceof ProductosController) {
                abrirDialogoNuevoProducto();
        } else {
            LOGGER.warning("El botón de añadir no tiene una acción definida para el controlador actual.");
        }
    }

    // --- REEMPLAZA ESTE MÉTODO EN TU CLASE MainController ---

    // --- REEMPLAZA ESTE MÉTODO EN TU CLASE MainController ---

    private void abrirDialogoNuevoProducto() {
        try {
            // La ruta a tu FXML es correcta
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/org/fran/gestortienda/ui/add_producto.fxml"));
            Parent view = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Añadir Nuevo Producto");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(mainPane.getScene().getWindow());
            dialogStage.setResizable(false);

            Scene scene = new Scene(view);
            dialogStage.setScene(scene);

            // Usamos el nombre de controlador correcto que encontramos en el FXML
            AddProductoController dialogController = loader.getController();
            dialogController.setDialogStage(dialogStage);

            // Mostramos el diálogo y esperamos a que se cierre
            dialogStage.showAndWait();

            // --- SOLUCIÓN AQUÍ ---
            // Simplemente comprobamos si se guardó. La lógica de creación y guardado
            // ya está dentro de AddProductoController.
            if (dialogController.isGuardado()) {
                LOGGER.info("Diálogo de producto cerrado y guardado. Refrescando vista...");

                // Refrescar la vista de productos
                if (activeController instanceof ProductosController) {
                    ((ProductosController) activeController).cargarProductos();
                }
            } else {
                LOGGER.info("Diálogo de producto cerrado sin guardar.");
            }
            // --- FIN DE LA SOLUCIÓN ---

        } catch (IOException e) {
            LOGGER.severe("Error al abrir el diálogo de nuevo producto.");
            e.printStackTrace();
        }
    }

    /**
     * Actualiza el estilo y la imagen de los botones del menú lateral.
     * VERSIÓN MEJORADA: Ahora también cambia el icono.
     */
    private void actualizarEstadoMenu(String vistaActiva) {

        resetButtonState(sideBtnClientes, "/org/fran/gestortienda/img/icono-clientes.png");
        resetButtonState(sideBtnProveedores, "/org/fran/gestortienda/img/icono-proveedores.png");
        resetButtonState(sideBtnProductos, "/org/fran/gestortienda/img/icono-productos.png");
        resetButtonState(sideBtnVentas, "/org/fran/gestortienda/img/icono-ventas.png");

        if (vistaActiva == null) return;


        switch (vistaActiva) {
            case "clientes":
                setButtonActive(sideBtnClientes, "/org/fran/gestortienda/img/icono-clientes-pulsado.png");
                break;
            case "proveedores":
                setButtonActive(sideBtnProveedores, "/org/fran/gestortienda/img/icono-proveedores-pulsado.png");
                break;
            case "productos":
                setButtonActive(sideBtnProductos, "/org/fran/gestortienda/img/icono-productos-pulsado.png");
                break;
            case "ventas":
                setButtonActive(sideBtnVentas, "/org/fran/gestortienda/img/icono-ventas-pulsado.png");
                break;
        }
    }

    /**
     * Método ayudante para poner un botón en estado "activo".
     */
    private void setButtonActive(Button button, String imagePath) {
        button.getStyleClass().remove("side-menu-button");
        button.getStyleClass().add("side-menu-button-active");
        ((ImageView) button.getGraphic()).setImage(new Image(getClass().getResourceAsStream(imagePath)));
    }

    /**
     * Método ayudante para resetear un botón a su estado normal.
     */
    private void resetButtonState(Button button, String imagePath) {
        button.getStyleClass().remove("side-menu-button-active");
        if (!button.getStyleClass().contains("side-menu-button")) {
            button.getStyleClass().add("side-menu-button");
        }
        ((ImageView) button.getGraphic()).setImage(new Image(getClass().getResourceAsStream(imagePath)));
    }

    private void abrirDialogoNuevaVenta() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/fran/gestortienda/ui/add_venta.fxml"));
            Parent root = loader.load();

            AddVentaController controller = loader.getController();

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Registrar nueva venta");
            dialog.setScene(new Scene(root));
            dialog.setResizable(false);

            controller.setDialogStage(dialog);
            dialog.showAndWait();

            if (controller.isGuardado()) {
                if (activeController instanceof VentasController ventasController) {
                    ventasController.cargarVentas();
                }
            }

        } catch (Exception e) {
            LOGGER.severe("Error al abrir diálogo de nueva venta: " + e.getMessage());
            e.printStackTrace();
        }
    }




    /**
     * Abre, gestiona y procesa el diálogo para añadir un nuevo PROVEEDOR.
     * VERSIÓN CORREGIDA: Apunta al FXML correcto.
     */
    private void abrirDialogoNuevoProveedor() {
        try {
            // --- SOLUCIÓN AQUÍ: Cambiamos la ruta al FXML del proveedor ---
            URL fxmlUrl = MainApp.class.getResource("/org/fran/gestortienda/ui/add_proveedor.fxml");
            if (fxmlUrl == null) {
                LOGGER.severe("No se pudo encontrar el recurso FXML: /org/fran/gestortienda/ui/add_proveedor.fxml");
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent view = loader.load();

            // 2. Crear un nuevo Stage (ventana) para el diálogo
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Añadir Nuevo Proveedor");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(mainPane.getScene().getWindow());
            dialogStage.setResizable(false);

            Scene scene = new Scene(view);
            dialogStage.setScene(scene);

            // Aplicamos la hoja de estilos
            URL cssUrl = getClass().getResource("/org/fran/gestortienda/css/add_formulario.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            // 3. Pasar el Stage al controlador del diálogo
            AddProveedorController dialogController = loader.getController();
            dialogController.setDialogStage(dialogStage);

            // 4. Mostrar el diálogo y esperar
            dialogStage.showAndWait();

            // 5. Procesar el resultado
            if (dialogController.isGuardado()) {
                Proveedor nuevoProveedor = dialogController.getNuevoProveedor();
                if (nuevoProveedor != null) {
                    new ProveedorDAO().add(nuevoProveedor);
                    LOGGER.info("Nuevo proveedor guardado: " + nuevoProveedor.getNombre());
                    // Refrescar la vista de proveedores
                    ((ProveedoresController) activeController).cargarProveedores();
                }
            }

        } catch (IOException | SQLException e) {
            LOGGER.severe("Error al abrir o procesar el diálogo de nuevo proveedor.");
            e.printStackTrace();
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
    private void handleFiltroPorNombre() {
        modoBusqueda = "Nombre";
        searchField.setPromptText("Buscar por Nombre...");
        LOGGER.info("Modo de búsqueda cambiado a: Nombre");
    }

    @FXML
    private void handleFiltroPorID() {
        modoBusqueda = "ID";
        searchField.setPromptText("Buscar por ID...");
        LOGGER.info("Modo de búsqueda cambiado a: ID");
    }

    @FXML
    private void handleFiltroPorDireccion() {
        modoBusqueda = "Direccion";
        searchField.setPromptText("Buscar por Dirección...");
        LOGGER.info("Modo de búsqueda cambiado a: Dirección");
    }

    /**
     * Se ejecuta al pulsar Enter en el campo de búsqueda.
     */
    @FXML
    private void handleSearch() {
        String textoBusqueda = searchField.getText();
        LOGGER.info("Búsqueda iniciada con el texto: '" + textoBusqueda + "' en modo: " + modoBusqueda);

        if (activeController instanceof ClientesController) {
            ((ClientesController) activeController).filtrarClientes(modoBusqueda, textoBusqueda);
        } else if (activeController instanceof ProveedoresController) {
            ((ProveedoresController) activeController).filtrarProveedores(modoBusqueda, textoBusqueda);

        } else if (activeController instanceof VentasController) {
            ((VentasController) activeController).filtrarVentas(modoBusqueda, textoBusqueda);
        } else if (activeController instanceof ProductosController) {
            ((ProductosController) activeController).filtrarProductos(modoBusqueda, textoBusqueda);
        } else {
            LOGGER.warning("La búsqueda no está implementada para el controlador actual: " +
                    (activeController != null ? activeController.getClass().getName() : "null"));
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