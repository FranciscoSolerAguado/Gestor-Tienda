package org.fran.gestortienda.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.fran.gestortienda.DAO.ProductoDAO;
import org.fran.gestortienda.MainApp;
import org.fran.gestortienda.model.entity.Producto;
import org.fran.gestortienda.utils.LoggerUtil;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

// Controlador principal para la pestaña de Productos.
// Aquí nos encargamos de mostrar la rejilla con las tarjetas, filtrar y borrar.
public class ProductosController implements Initializable {

    private static final Logger LOGGER = LoggerUtil.getLogger();

    // El contenedor donde se insertan los clientes
    @FXML
    private TilePane contenedorProductos;

    // Conexión a datos
    private final ProductoDAO productoDAO = new ProductoDAO();
    private final List<Producto> productosSeleccionados = new ArrayList<>();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        LOGGER.info("Inicializando ProductosController...");
        cargarProductos(); // Nada más entrar, pintamos el catálogo.
    }

    /**
     * Método principal para traer datos y mostrarlos por pantalla.
     */
    public void cargarProductos() {
        try {
            // Limpiamos la rejilla por si había datos anteriores.
            contenedorProductos.getChildren().clear();
            List<Producto> lista = productoDAO.getAll();

            // Si no hay datos, o está vacía
            if (lista.isEmpty()) {
                LOGGER.info("No se encontraron productos en la base de datos.");
                contenedorProductos.getChildren().add(new Label("No hay productos registrados."));
                return;
            }

            // Recorremos la lista y por cada producto insertamos una tarjeta
            for (Producto p : lista) {
                VBox tarjeta = crearTarjetaProducto(p);
                contenedorProductos.getChildren().add(tarjeta);
            }
            LOGGER.info("Se cargaron " + lista.size() + " productos en la vista.");
        } catch (SQLException e) {
            LOGGER.severe("Error de SQL al cargar productos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Método que crea una tarjeta para un producto
     * @param producto el producto del que vamos a crear la tarjeta
     */
    private VBox crearTarjetaProducto(Producto producto) {
        // Parte superior: ID a la izquierda, CheckBox a la derecha.
        StackPane topPane = new StackPane();
        topPane.setPadding(new Insets(0, 10, 0, 15));

        Label idLabel = new Label("#" + producto.getId_producto());
        idLabel.getStyleClass().add("producto-id");
        StackPane.setAlignment(idLabel, Pos.CENTER_LEFT);

        CheckBox checkBox = new CheckBox();
        StackPane.setAlignment(checkBox, Pos.CENTER_RIGHT);

       //Si se selecciona el checkkbox puedes hacer una cosa o otra
        checkBox.setOnAction(e -> {
            if (checkBox.isSelected()) {
                productosSeleccionados.add(producto);
            } else {
                productosSeleccionados.remove(producto);
            }
        });

        topPane.getChildren().addAll(idLabel, checkBox);

        // --- Gestión de la Imagen ---
        ImageView imageView;
        try {
            // Intentamos cargar la foto real del producto.
            imageView = new ImageView(
                    new Image(getClass().getResourceAsStream(
                            "/org/fran/gestortienda/img/productos/" + producto.getImagen()
                    ))
            );
        } catch (Exception e) {
            // Si falla (o es null), ponemos una imagen por defecto para que no quede el hueco feo.
            imageView = new ImageView(
                    new Image(getClass().getResourceAsStream(
                            "/org/fran/gestortienda/img/icono-productos.png"
                    ))
            );
        }

        // Ajustamos tamaño para que todas las tarjetas sean iguales.
        imageView.setFitWidth(160);
        imageView.setFitHeight(160);
        imageView.setPreserveRatio(true);

        // Nombre del producto.
        Label nombreLabel = new Label(producto.getNombre());
        nombreLabel.getStyleClass().add("producto-text");

        // Botones de detalles y editar
        Button detallesBtn = new Button("Más detalles");
        detallesBtn.getStyleClass().add("venta-detalles-btn");
        detallesBtn.setOnAction(e -> mostrarDetalles(producto));

        Button editarBtn = new Button("Editar");
        editarBtn.getStyleClass().add("venta-detalles-btn");
        editarBtn.setOnAction(e -> handleEditarProducto(producto));

        HBox botonesBox = new HBox(10, detallesBtn, editarBtn);
        botonesBox.setAlignment(Pos.CENTER);

        // Alineacion vertical
        VBox tarjeta = new VBox(12, topPane, imageView, nombreLabel, botonesBox);
        tarjeta.setAlignment(Pos.TOP_CENTER);
        tarjeta.getStyleClass().add("producto-card");
        tarjeta.setPrefSize(230, 320);

        return tarjeta;
    }

    /**
     * Metodo que abre la ventana de edición de producto
     * @param producto el producto al que queremos editar
     */
    private void handleEditarProducto(Producto producto) {
        LOGGER.info("Abriendo diálogo para editar producto ID: " + producto.getId_producto());
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/org/fran/gestortienda/ui/add_producto.fxml"));
            Parent view = loader.load();

            AddProductoController dialogController = loader.getController();
            // Le pasamos el producto para que rellene los campos ya automaticamente
            dialogController.setProductoParaEditar(producto);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Editar Producto");
            dialogStage.initModality(Modality.WINDOW_MODAL); // Bloquea la ventana de fondo.
            dialogStage.initOwner(contenedorProductos.getScene().getWindow());

            Scene scene = new Scene(view);
            dialogStage.setScene(scene);

            dialogController.setDialogStage(dialogStage);
            dialogStage.showAndWait();

            // Si guardó cambios, actualizamos la vista
            if (dialogController.isGuardado()) {
                LOGGER.info("Diálogo de edición de producto cerrado y guardado. Refrescando vista...");
                cargarProductos();
            } else {
                LOGGER.info("Diálogo de edición de producto cerrado sin guardar.");
            }
        } catch (IOException e) {
            LOGGER.severe("Error al abrir el diálogo de edición de producto: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Método que muestra los detalles de un producto
     * @param p el producto del que queremos ver los detalles
     */
    private void mostrarDetalles(Producto p) {
        LOGGER.info("Mostrando detalles para el producto ID: " + p.getId_producto());
        // Usamos un bloque de texto formateado para que quede bonito.
        String info = """
                ID: %d
                Nombre: %s
                Categoría: %s
                Precio: %.2f €
                Stock: %d
                Proveedor: %s
                Imagen: %s
                """.formatted(
                p.getId_producto(),
                p.getNombre(),
                p.getCategoria().name(),
                p.getPrecio(),
                p.getStock(),
                p.getProveedor() != null ? p.getProveedor().getNombre() : "Sin proveedor",
                p.getImagen()
        );

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detalles del producto");
        alert.setHeaderText("Información de " + p.getNombre());
        alert.setContentText(info);
        alert.showAndWait();
    }

    /**
     * Borra todos los productos que tengan el checkbox marcado.
     */
    public void borrarSeleccionados() {
        if (productosSeleccionados.isEmpty()) {
            LOGGER.info("Intento de borrado de productos sin selección.");
            new Alert(Alert.AlertType.INFORMATION, "No has seleccionado ningún producto para borrar.").showAndWait();
            return;
        }

        // Confirmación
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar borrado");
        confirm.setHeaderText("¿Deseas borrar los productos seleccionados?");
        confirm.setContentText("Productos a borrar: " + productosSeleccionados.size());

        Optional<ButtonType> r = confirm.showAndWait();
        if (r.isEmpty() || r.get() != ButtonType.OK) {
            LOGGER.info("Borrado de productos cancelado por el usuario.");
            return;
        }

        LOGGER.info("Iniciando borrado de " + productosSeleccionados.size() + " productos.");
        int borrados = 0;
        for (Producto p : productosSeleccionados) {
            try {
                productoDAO.delete(p);
                borrados++;
            } catch (SQLException e) {
                LOGGER.severe("Error de SQL al borrar el producto ID " + p.getId_producto() + ": " + e.getMessage());
            }
        }

        productosSeleccionados.clear();
        cargarProductos(); // Actualizamos la vista

        new Alert(Alert.AlertType.INFORMATION, borrados + " producto(s) borrado(s)").showAndWait();
        LOGGER.info(borrados + " productos borrados exitosamente.");
    }

    /**
     * Método para filtrar productos
     * @param modo el modo de busqueda
     * @param texto el texto que buscamos
     */
    public void filtrarProductos(String modo, String texto) {
        if (texto == null || texto.isBlank()) {
            cargarProductos(); // Si borran el texto, mostramos todo.
            return;
        }

        LOGGER.info("Filtrando productos por '" + modo + "' con el texto: '" + texto + "'");
        List<Producto> filtrados = new ArrayList<>();
        try {

            List<Producto> todos = productoDAO.getAll();
            switch (modo.toLowerCase()) {
                case "nombre":
                    filtrados = todos.stream()
                            .filter(p -> p.getNombre().toLowerCase().contains(texto.toLowerCase()))
                            .toList();
                    break;
                case "categoria":
                    filtrados = todos.stream()
                            .filter(p -> p.getCategoria().name().toLowerCase().contains(texto.toLowerCase()))
                            .toList();
                    break;
                case "proveedor":
                    filtrados = todos.stream()
                            .filter(p -> p.getProveedor() != null &&
                                    p.getProveedor().getNombre().toLowerCase().contains(texto.toLowerCase()))
                            .toList();
                    break;
            }
            actualizarVista(filtrados);
        } catch (SQLException e) {
            LOGGER.severe("Error de SQL al filtrar productos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Actualiza la vista con los productos filtrados
     * @param productos la lista de productos filtrados
     */
    private void actualizarVista(List<Producto> productos) {
        contenedorProductos.getChildren().clear();
        if (productos == null || productos.isEmpty()) {
            contenedorProductos.getChildren().add(new Label("No se encontraron productos."));
            return;
        }
        for (Producto p : productos) {
            contenedorProductos.getChildren().add(crearTarjetaProducto(p));
        }
    }
}

