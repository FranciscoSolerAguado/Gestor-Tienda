package org.fran.gestortienda.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.fran.gestortienda.DAO.ClienteDAO;
import org.fran.gestortienda.DAO.ProveedorDAO;
import org.fran.gestortienda.MainApp;
import org.fran.gestortienda.model.entity.Cliente;
import org.fran.gestortienda.model.entity.Proveedor;
import org.fran.gestortienda.utils.LoggerUtil;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.logging.Logger;

// Este controlador maneja la ventana principal, el menú lateral y decide qué pantalla
// se muestra en el centro en cada momento.
public class MainController {

    private static final Logger LOGGER = LoggerUtil.getLogger();

    @FXML private BorderPane mainPane; // El panel principal
    @FXML private HBox topBar;         // La barra superior personalizada
    @FXML private VBox rightPanel;     // El panel derecho con los filtros y búsquedas.


    @FXML private TextField searchField;
    @FXML private MenuButton filterMenuButton;


    @FXML private Button sideBtnProveedores;
    @FXML private Button sideBtnProductos;
    @FXML private Button sideBtnClientes;
    @FXML private Button sideBtnVentas;


    private Object activeController; // Aquí guardamos "quién" está en pantalla (ClientesController, VentasController, etc.)
    private String modoBusqueda = "Nombre"; // Por defecto buscamos por nombre.

    // Variables para mover la ventana (arrastrar)
    private double xOffset = 0;
    private double yOffset = 0;

    // Variables para redimensionar la ventana manualmente
    private double startX, startY, startWidth, startHeight, startScreenX, startScreenY;
    private boolean isResizing = false;
    private static final int RESIZE_MARGIN = 5; // Margen de píxeles para detectar el borde.

    /**
     * Método que inicializa el controlador.
     */
    @FXML
    public void initialize() {
        LOGGER.info("MainController inicializado y componentes FXML inyectados.");

        // Ocultamos el panel derecho al inicio hasta que carguemos una vista.
        if (rightPanel != null) {
            rightPanel.setVisible(false);
        }

        // --- Lógica para mover la ventana ---
        topBar.setOnMousePressed(event -> {
            Stage stage = (Stage) topBar.getScene().getWindow();
            xOffset = stage.getX() - event.getScreenX();
            yOffset = stage.getY() - event.getScreenY();
        });

        topBar.setOnMouseDragged(event -> {
            Stage stage = (Stage) topBar.getScene().getWindow();
            stage.setX(event.getScreenX() + xOffset);
            stage.setY(event.getScreenY() + yOffset);
        });

        addResizeListeners();
    }

    // Vincula los eventos del ratón para detectar si queremos redimensionar.
    private void addResizeListeners() {
        mainPane.setOnMouseMoved(this::handleMouseMoved);
        mainPane.setOnMousePressed(this::handleMousePressed);
        mainPane.setOnMouseDragged(this::handleMouseDragged);
        mainPane.setOnMouseReleased(this::handleMouseReleased);
    }

    // Cambia el cursor cuando pasas el ratón por los bordes (flechita doble).
    private void handleMouseMoved(MouseEvent event) {
        if (isMaximized) return; // Si está maximizada, no se puede estirar.

        Stage stage = (Stage) mainPane.getScene().getWindow();
        Scene scene = stage.getScene();
        double x = event.getX();
        double y = event.getY();
        double width = stage.getWidth();
        double height = stage.getHeight();

        Cursor cursor = Cursor.DEFAULT;
        // Detectamos esquinas y bordes laterales/inferiores.
        if (x < RESIZE_MARGIN && y < RESIZE_MARGIN) cursor = Cursor.NW_RESIZE;
        else if (x < RESIZE_MARGIN && y > height - RESIZE_MARGIN) cursor = Cursor.SW_RESIZE;
        else if (x > width - RESIZE_MARGIN && y < RESIZE_MARGIN) cursor = Cursor.NE_RESIZE;
        else if (x > width - RESIZE_MARGIN && y > height - RESIZE_MARGIN) cursor = Cursor.SE_RESIZE;
        else if (x < RESIZE_MARGIN) cursor = Cursor.W_RESIZE;
        else if (x > width - RESIZE_MARGIN) cursor = Cursor.E_RESIZE;
        else if (y < RESIZE_MARGIN) cursor = Cursor.N_RESIZE;
        else if (y > height - RESIZE_MARGIN) cursor = Cursor.S_RESIZE;

        scene.setCursor(cursor);
    }

    // Cuando haces clic en un borde para empezar a estirar.
    private void handleMousePressed(MouseEvent event) {
        if (isMaximized) return;

        Stage stage = (Stage) mainPane.getScene().getWindow();
        if (stage.getScene().getCursor() != Cursor.DEFAULT) {
            isResizing = true;
            // Guardamos posición y tamaño inicial para calcular luego.
            startX = stage.getX();
            startY = stage.getY();
            startWidth = stage.getWidth();
            startHeight = stage.getHeight();
            startScreenX = event.getScreenX();
            startScreenY = event.getScreenY();
        }
    }

    // calcula el nuevo tamaño mientras arrastras el ratón.
    private void handleMouseDragged(MouseEvent event) {
        if (!isResizing) return;

        Stage stage = (Stage) mainPane.getScene().getWindow();
        double dx = event.getScreenX() - startScreenX;
        double dy = event.getScreenY() - startScreenY;
        Cursor cursor = stage.getScene().getCursor();

        // Ajustamos ancho y posición X (si tiras de la izquierda).
        if (cursor == Cursor.W_RESIZE || cursor == Cursor.NW_RESIZE || cursor == Cursor.SW_RESIZE) {
            double newWidth = startWidth - dx;
            if (newWidth > stage.getMinWidth()) {
                stage.setX(startX + dx);
                stage.setWidth(newWidth);
            }
        }
        // Ajustamos ancho (si tiras de la derecha).
        if (cursor == Cursor.E_RESIZE || cursor == Cursor.NE_RESIZE || cursor == Cursor.SE_RESIZE) {
            double newWidth = startWidth + dx;
            if (newWidth > stage.getMinWidth()) {
                stage.setWidth(newWidth);
            }
        }
        // Ajustamos alto y posición Y (si tiras de arriba).
        if (cursor == Cursor.N_RESIZE || cursor == Cursor.NW_RESIZE || cursor == Cursor.NE_RESIZE) {
            double newHeight = startHeight - dy;
            if (newHeight > stage.getMinHeight()) {
                stage.setY(startY + dy);
                stage.setHeight(newHeight);
            }
        }
        // Ajustamos alto (si tiras de abajo).
        if (cursor == Cursor.S_RESIZE || cursor == Cursor.SW_RESIZE || cursor == Cursor.SE_RESIZE) {
            double newHeight = startHeight + dy;
            if (newHeight > stage.getMinHeight()) {
                stage.setHeight(newHeight);
            }
        }
    }


    private void handleMouseReleased(MouseEvent event) {
        isResizing = false;
    }

    // --- MENÚ LATERAL ---
    // Cada botón carga su vista correspondiente en el centro.

    /**
     * Método que maneja el evento de clicar en el boton de clientes en el lateral
     * @param event el evento de clic
     */
    @FXML
    void handleClientesClick(ActionEvent event) {
        LOGGER.info("Botón Clientes presionado.");
        loadView("/org/fran/gestortienda/ui/clientes.fxml", "clientes");
    }

    /**
     * Método que maneja el evento de clicar en el boton de proveedores en el lateral
     * @param event el evento de clic
     */
    @FXML
    void handleProveedoresClick(ActionEvent event) {
        LOGGER.info("Botón Proveedores presionado.");
        loadView("/org/fran/gestortienda/ui/proveedores.fxml", "proveedores");
    }

    /**
     * Método que maneja el evento de clicar en el boton de productos en el lateral
     * @param event el evento de clic
     */
    @FXML
    void handleProductosClick(ActionEvent event) {
        LOGGER.info("Botón Productos presionado.");
        loadView("/org/fran/gestortienda/ui/productos.fxml", "productos");
    }

    /**
     * Método que maneja el evento de clicar en el boton de ventas en el lateral
     * @param event el evento de clic
     */
    @FXML
    void handleVentasClick(ActionEvent event) {
        LOGGER.info("Botón Ventas presionado.");
        loadView("/org/fran/gestortienda/ui/ventas.fxml", "ventas");
    }

    /**
     * El motor de navegación.
     * Carga un archivo FXML, lo planta en el centro de la pantalla y configura los botones.
     */
    private void loadView(String fxmlPath, String viewName) {
        LOGGER.info("Iniciando carga de vista: " + fxmlPath);
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource(fxmlPath));
            Parent view = loader.load();


            activeController = loader.getController();
            LOGGER.info("Controlador activo establecido en: " + (activeController != null ? activeController.getClass().getName() : "null"));

            mainPane.setCenter(view);
            LOGGER.info("Vista '" + fxmlPath + "' cargada en el panel central.");


            actualizarEstadoMenu(viewName);

            // Configuramos el buscador y los filtros según qué estemos viendo.
            if (rightPanel != null) {
                rightPanel.setVisible(true);
                actualizarMenuFiltro(activeController);
                LOGGER.info("Panel derecho actualizado y visible.");
            } else {
                LOGGER.severe("¡ERROR! rightPanel es NULL y no se puede mostrar.");
            }

        } catch (IOException e) {
            LOGGER.severe("Error de E/S al cargar la vista FXML: " + fxmlPath + " - " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- BOTÓN DE BORRAR (Genérico) ---

    /**
     * Método que maneja el evento de clicar en el boton de borrar en el lateral, dependiendo del controlador activo.
     */
    @FXML
    private void handleDeleteClick() {
        LOGGER.info("Botón de borrado presionado.");
        if (activeController instanceof ClientesController) {
            ((ClientesController) activeController).borrarSeleccionados();
        } else if (activeController instanceof ProveedoresController) {
            ((ProveedoresController) activeController).borrarSeleccionados();
        } else if (activeController instanceof VentasController) {
            ((VentasController) activeController).borrarSeleccionados();
        } else if (activeController instanceof ProductosController) {
            ((ProductosController) activeController).borrarSeleccionados();
        } else {
            LOGGER.warning("El borrado no está implementado para el controlador actual: " + (activeController != null ? activeController.getClass().getName() : "null"));
        }
    }

    // Cambia el modo de búsqueda (ej: de buscar por nombre a buscar por DNI).
    private void setModoBusqueda(String modo, String promptText) {
        this.modoBusqueda = modo;
        searchField.setPromptText(promptText);
        LOGGER.info("Modo de búsqueda cambiado a: " + modo);
    }

    // Configura el menú desplegable del filtro.
    // Si estás en clientes, te deja filtrar por nombre/dir/id.
    // Si estás en ventas, por fecha/cliente, etc.
    private void actualizarMenuFiltro(Object controller) {
        if (filterMenuButton == null) {
            LOGGER.warning("filterMenuButton es null. No se puede actualizar el menú.");
            return;
        }

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
            setModoBusqueda("Nombre", "Buscar por Nombre..."); // Por defecto
        }
        else if (controller instanceof ProveedoresController) {
            MenuItem porNombre = new MenuItem("Por Nombre");
            porNombre.setOnAction(e -> setModoBusqueda("Nombre", "Buscar por Nombre..."));
            MenuItem porTelefono = new MenuItem("Por Teléfono");
            porTelefono.setOnAction(e -> setModoBusqueda("Telefono", "Buscar por Teléfono..."));
            MenuItem porCorreo = new MenuItem("Por Correo");
            porCorreo.setOnAction(e -> setModoBusqueda("Correo", "Buscar por Correo..."));
            filterMenuButton.getItems().addAll(porNombre, porTelefono, porCorreo);
            setModoBusqueda("Nombre", "Buscar por Nombre...");
        }
        else if (controller instanceof VentasController) {
            MenuItem porFecha = new MenuItem("Por Fecha");
            porFecha.setOnAction(e -> setModoBusqueda("Fecha", "Buscar por Fecha"));
            MenuItem porCliente = new MenuItem("Por ID Cliente");
            porCliente.setOnAction(e -> setModoBusqueda("Cliente", "Buscar por ID de Cliente..."));
            filterMenuButton.getItems().addAll(porFecha, porCliente);
            setModoBusqueda("Fecha", "Buscar por Fecha");
        }
        else if (controller instanceof ProductosController) {
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
            // Si no reconocemos el controlador, bloqueamos el buscador.
            MenuItem defaultItem = new MenuItem("Sin filtro");
            defaultItem.setDisable(true);
            filterMenuButton.getItems().add(defaultItem);
            searchField.setDisable(true);
            filterMenuButton.setDisable(true);
        }
        LOGGER.info("Menú de filtro actualizado para el controlador: " + (controller != null ? controller.getClass().getSimpleName() : "null"));
    }

    // --- BOTÓN DE AÑADIR (Genérico) ---

    /**
     * Abre el diálogo correspondiente (Nuevo Cliente, Nuevo Producto...) según dónde estemos.
     */
    @FXML
    private void handleAddClick() {
        LOGGER.info("Botón de añadir presionado.");
        if (activeController instanceof ClientesController) {
            abrirDialogoNuevoCliente();
        } else if (activeController instanceof ProveedoresController) {
            abrirDialogoNuevoProveedor();
        } else if (activeController instanceof VentasController) {
            abrirDialogoNuevaVenta();
        } else if (activeController instanceof ProductosController) {
            abrirDialogoNuevoProducto();
        } else {
            LOGGER.warning("La acción de añadir no está implementada para el controlador actual.");
        }
    }

    /**
     * Abre el diálogo para añadir un nuevo producto.
     */
    private void abrirDialogoNuevoProducto() {
        LOGGER.info("Abriendo diálogo para añadir nuevo producto...");
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/org/fran/gestortienda/ui/add_producto.fxml"));
            Parent view = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Añadir Nuevo Producto");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(mainPane.getScene().getWindow());
            dialogStage.setResizable(false);
            Scene scene = new Scene(view);
            dialogStage.setScene(scene);

            AddProductoController dialogController = loader.getController();
            dialogController.setDialogStage(dialogStage);
            dialogStage.showAndWait();

            if (dialogController.isGuardado()) {
                LOGGER.info("Diálogo de producto cerrado y guardado. Refrescando vista...");
                if (activeController instanceof ProductosController) {
                    ((ProductosController) activeController).cargarProductos();
                }
            } else {
                LOGGER.info("Diálogo de producto cerrado sin guardar.");
            }
        } catch (IOException e) {
            LOGGER.severe("Error al abrir el diálogo de nuevo producto: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Actualiza el estado del menú.
     * @param vistaActiva el nombre de la vista actual.
     */
    private void actualizarEstadoMenu(String vistaActiva) {
        // Primero reseteamos todos a su estado normal.
        resetButtonState(sideBtnClientes, "/org/fran/gestortienda/img/icono-clientes.png");
        resetButtonState(sideBtnProveedores, "/org/fran/gestortienda/img/icono-proveedores.png");
        resetButtonState(sideBtnProductos, "/org/fran/gestortienda/img/icono-productos.png");
        resetButtonState(sideBtnVentas, "/org/fran/gestortienda/img/icono-ventas.png");

        if (vistaActiva == null) return;

        // Luego activamos el que toca.
        // Y cambiamos la imagen para que coincida con el color del botón. y no se vea mal
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
        LOGGER.info("Estado del menú actualizado. Vista activa: " + vistaActiva);
    }

    /**
     * Cambia el estilo del botón a "Activo" (fondo oscuro e icono brillante).
     * @param button el botón a cambiar
     * @param imagePath la ruta de la imagen a usar
     */
    private void setButtonActive(Button button, String imagePath) {
        button.getStyleClass().remove("side-menu-button");
        button.getStyleClass().add("side-menu-button-active");
        ((ImageView) button.getGraphic()).setImage(new Image(getClass().getResourceAsStream(imagePath)));
    }

    /**
     * Cambia el estilo del botón a "Normal" (fondo normal e icono normal).
     * @param button el botón a cambiar
     * @param imagePath la ruta de la imagen a usar
     */
    private void resetButtonState(Button button, String imagePath) {
        button.getStyleClass().remove("side-menu-button-active");
        if (!button.getStyleClass().contains("side-menu-button")) {
            button.getStyleClass().add("side-menu-button");
        }
        ((ImageView) button.getGraphic()).setImage(new Image(getClass().getResourceAsStream(imagePath)));
    }

    /**
     * Abre el diálogo para añadir una nueva venta.
     */
    private void abrirDialogoNuevaVenta() {
        LOGGER.info("Abriendo diálogo para añadir nueva venta...");
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
                LOGGER.info("Diálogo de venta cerrado y guardado. Refrescando vista...");
                if (activeController instanceof VentasController) {
                    ((VentasController) activeController).cargarVentas();
                }
            }
        } catch (Exception e) {
            LOGGER.severe("Error al abrir diálogo de nueva venta: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void abrirDialogoNuevoProveedor() {
        LOGGER.info("Abriendo diálogo para añadir nuevo proveedor...");
        try {
            URL fxmlUrl = MainApp.class.getResource("/org/fran/gestortienda/ui/add_proveedor.fxml");
            if (fxmlUrl == null) {
                LOGGER.severe("No se pudo encontrar el recurso FXML: /org/fran/gestortienda/ui/add_proveedor.fxml");
                return;
            }
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent view = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Añadir Nuevo Proveedor");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(mainPane.getScene().getWindow());
            dialogStage.setResizable(false);
            Scene scene = new Scene(view);
            dialogStage.setScene(scene);
            URL cssUrl = getClass().getResource("/org/fran/gestortienda/css/add_formulario.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }
            AddProveedorController dialogController = loader.getController();
            dialogController.setDialogStage(dialogStage);
            dialogStage.showAndWait();
            if (dialogController.isGuardado()) {
                Proveedor nuevoProveedor = dialogController.getNuevoProveedor();
                if (nuevoProveedor != null) {
                    new ProveedorDAO().add(nuevoProveedor);
                    LOGGER.info("Nuevo proveedor guardado: " + nuevoProveedor.getNombre());
                    if (activeController instanceof ProveedoresController) {
                        ((ProveedoresController) activeController).cargarProveedores();
                    }
                }
            }
        } catch (IOException | SQLException e) {
            LOGGER.severe("Error al abrir o procesar el diálogo de nuevo proveedor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Abre el diálogo para añadir un nuevo cliente.
     */
    private void abrirDialogoNuevoCliente() {
        LOGGER.info("Abriendo diálogo para añadir nuevo cliente...");
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/org/fran/gestortienda/ui/add_cliente.fxml"));
            Parent view = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Añadir Nuevo Cliente");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(mainPane.getScene().getWindow());
            dialogStage.setResizable(false);
            Scene scene = new Scene(view);
            dialogStage.setScene(scene);
            AddClienteController dialogController = loader.getController();
            dialogController.setDialogStage(dialogStage);
            dialogStage.showAndWait();
            if (dialogController.isGuardado()) {
                Cliente nuevoCliente = dialogController.getNuevoCliente();
                if (nuevoCliente != null) {
                    new ClienteDAO().add(nuevoCliente);
                    LOGGER.info("Nuevo cliente guardado: " + nuevoCliente.getNombre());
                    if (activeController instanceof ClientesController) {
                        ((ClientesController) activeController).cargarClientes();
                    }
                }
            }
        } catch (IOException | SQLException e) {
            LOGGER.severe("Error al abrir o procesar el diálogo de nuevo cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- BÚSQUEDA ---

    /**
     * Metodo que maneja Cuando le das a la lupa o Enter en el buscador.
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
            LOGGER.warning("La búsqueda no está implementada para el controlador actual.");
        }
    }

    // --- CONTROLES DE LA VENTANA (Minimizar, Maximizar, Cerrar) ---

    @FXML
    private void handleMinimize() {
        LOGGER.info("Minimizando la ventana.");
        Stage stage = (Stage) topBar.getScene().getWindow();
        stage.setIconified(true);
    }

    private boolean isMaximized = false;
    private Rectangle2D backupWindowBounds;

    @FXML
    private void handleToggleMaximize() {
        Stage stage = (Stage) topBar.getScene().getWindow();
        if (isMaximized) {
            // Restaurar tamaño original
            if (backupWindowBounds != null) {
                stage.setX(backupWindowBounds.getMinX());
                stage.setY(backupWindowBounds.getMinY());
                stage.setWidth(backupWindowBounds.getWidth());
                stage.setHeight(backupWindowBounds.getHeight());
            }
            isMaximized = false;
            LOGGER.info("Ventana restaurada a tamaño normal.");
        } else {
            // Guardamos posición actual y maximizamos ocupando toda la pantalla visible.
            backupWindowBounds = new Rectangle2D(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            stage.setX(screenBounds.getMinX());
            stage.setY(screenBounds.getMinY());
            stage.setWidth(screenBounds.getWidth());
            stage.setHeight(screenBounds.getHeight());
            isMaximized = true;
            LOGGER.info("Ventana maximizada.");
        }
    }

    @FXML
    private void handleClose() {
        LOGGER.info("Cerrando la aplicación.");
        Platform.exit();
    }
}
