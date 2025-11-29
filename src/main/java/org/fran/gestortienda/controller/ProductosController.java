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
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.fran.gestortienda.DAO.ProductoDAO;
import org.fran.gestortienda.model.entity.Producto;
import org.fran.gestortienda.utils.LoggerUtil;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

public class ProductosController implements Initializable {
    private static final Logger LOGGER = LoggerUtil.getLogger();


    @FXML
    private TilePane contenedorProductos;

    private final ProductoDAO productoDAO = new ProductoDAO();

    private final List<Producto> productosSeleccionados = new ArrayList<>();

    private String modoFiltro = "Nombre";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cargarProductos();
    }

    // =========================================================
    //          CARGAR PRODUCTOS COMO TARJETAS
    // =========================================================
    public void cargarProductos() {
        try {
            contenedorProductos.getChildren().clear();

            List<Producto> lista = productoDAO.getAll();

            if (lista.isEmpty()) {
                contenedorProductos.getChildren().add(new Label("No hay productos registrados."));
                return;
            }

            for (Producto p : lista) {
                VBox tarjeta = crearTarjetaProducto(p);
                contenedorProductos.getChildren().add(tarjeta);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // =========================================================
    //          TARJETA DE PRODUCTO
    // =========================================================
    // --- REEMPLAZA ESTE MÉTODO EN TU CLASE ProductosController ---

    private VBox crearTarjetaProducto(Producto producto) {

        // --- Barra superior con ID y checkbox (COMO LO TENÍAS) ---
        StackPane topPane = new StackPane();
        topPane.setPadding(new Insets(0, 10, 0, 15));

        Label idLabel = new Label("#" + producto.getId_producto());
        idLabel.getStyleClass().add("producto-id");
        StackPane.setAlignment(idLabel, Pos.CENTER_LEFT);

        CheckBox checkBox = new CheckBox();
        StackPane.setAlignment(checkBox, Pos.CENTER_RIGHT);

        checkBox.setOnAction(e -> {
            if (checkBox.isSelected()) {
                productosSeleccionados.add(producto);
            } else {
                productosSeleccionados.remove(producto);
            }
        });

        topPane.getChildren().addAll(idLabel, checkBox);

        // --- Imagen (COMO LA TENÍAS) ---
        ImageView imageView;
        try {
            imageView = new ImageView(
                    new Image(getClass().getResourceAsStream(
                            "/org/fran/gestortienda/img/productos/" + producto.getImagen()
                    ))
            );
        } catch (Exception e) {
            imageView = new ImageView(
                    new Image(getClass().getResourceAsStream(
                            "/org/fran/gestortienda/img/icono-productos.png"
                    ))
            );
        }

        imageView.setFitWidth(160);
        imageView.setFitHeight(160);
        imageView.setPreserveRatio(true);

        // --- Nombre (COMO LO TENÍAS) ---
        Label nombreLabel = new Label(producto.getNombre());
        nombreLabel.getStyleClass().add("producto-text");

        // --- 1. CREAMOS LOS BOTONES DE ACCIÓN ---
        Button detallesBtn = new Button("Más detalles");
        detallesBtn.getStyleClass().add("venta-detalles-btn"); // Reutilizamos estilo
        detallesBtn.setOnAction(e -> mostrarDetalles(producto));

        Button editarBtn = new Button("Editar"); // <-- BOTÓN AÑADIDO
        editarBtn.getStyleClass().add("venta-detalles-btn"); // Reutilizamos estilo
        editarBtn.setOnAction(e -> handleEditarProducto(producto)); // <-- ACCIÓN AÑADIDA

        // --- 2. LOS METEMOS EN UN HBOX PARA QUE ESTÉN UNO AL LADO DEL OTRO ---
        HBox botonesBox = new HBox(10, detallesBtn, editarBtn);
        botonesBox.setAlignment(Pos.CENTER);

        // --- Tarjeta final ---
        // 3. AÑADIMOS EL HBOX DE BOTONES A LA TARJETA
        VBox tarjeta = new VBox(12, topPane, imageView, nombreLabel, botonesBox);
        tarjeta.setAlignment(Pos.TOP_CENTER);
        tarjeta.getStyleClass().add("producto-card");
        tarjeta.setPrefSize(230, 230); // Un poco más de alto para que quepan los botones

        return tarjeta;
    }

    /**
     * 5. NUEVO MÉTODO para abrir el diálogo en modo edición.
     */
    private void handleEditarProducto(Producto producto) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/fran/gestortienda/ui/add_producto.fxml"));
            Parent view = loader.load();

            // Obtenemos el controlador del diálogo
            AddProductoController dialogController = loader.getController();

            // Le pasamos el producto que queremos editar
            dialogController.setProductoParaEditar(producto);

            // Creamos y mostramos el diálogo
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Editar Producto");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(contenedorProductos.getScene().getWindow());

            Scene scene = new Scene(view);
            dialogStage.setScene(scene);

            dialogController.setDialogStage(dialogStage);
            dialogStage.showAndWait();

            // Si se guardó, refrescamos la vista
            if (dialogController.isGuardado()) {
                cargarProductos();
            }

        } catch (IOException e) {
            LOGGER.severe("Error al abrir el diálogo de edición de producto.");
            e.printStackTrace();
        }
    }

    // =========================================================
    //          ALERTA DE DETALLES DEL PRODUCTO
    // =========================================================
    private void mostrarDetalles(Producto p) {

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

    // =========================================================
    //          BORRAR PRODUCTOS SELECCIONADOS
    // =========================================================
    public void borrarSeleccionados() {
        if (productosSeleccionados.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION,
                    "No has seleccionado ningún producto para borrar.").showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar borrado");
        confirm.setHeaderText("¿Deseas borrar los productos seleccionados?");
        confirm.setContentText("Productos a borrar: " + productosSeleccionados.size());

        Optional<ButtonType> r = confirm.showAndWait();
        if (r.isEmpty() || r.get() != ButtonType.OK) return;

        int borrados = 0;

        for (Producto p : productosSeleccionados) {
            try {
                productoDAO.delete(p);
                borrados++;
            } catch (SQLException ignored) {}
        }

        productosSeleccionados.clear();
        cargarProductos();

        new Alert(Alert.AlertType.INFORMATION,
                borrados + " producto(s) borrado(s)").showAndWait();
    }

    // =========================================================
    //          FILTRADO
    // =========================================================
    public void filtrarProductos(String modo, String texto) {

        if (texto == null || texto.isBlank()) {
            cargarProductos();
            return;
        }

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
                            .filter(p -> p.getCategoria().name()
                                    .toLowerCase()
                                    .contains(texto.toLowerCase()))
                            .toList();
                    break;

                case "proveedor":
                    filtrados = todos.stream()
                            .filter(p -> p.getProveedor() != null &&
                                    p.getProveedor().getNombre()
                                            .toLowerCase()
                                            .contains(texto.toLowerCase()))
                            .toList();
                    break;
            }

            actualizarVista(filtrados);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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


