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

public class ProductosController implements Initializable {
    private static final Logger LOGGER = LoggerUtil.getLogger();

    @FXML
    private TilePane contenedorProductos;

    private final ProductoDAO productoDAO = new ProductoDAO();
    private final List<Producto> productosSeleccionados = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        LOGGER.info("Inicializando ProductosController...");
        cargarProductos();
    }

    public void cargarProductos() {
        try {
            contenedorProductos.getChildren().clear();
            List<Producto> lista = productoDAO.getAll();

            if (lista.isEmpty()) {
                LOGGER.info("No se encontraron productos en la base de datos.");
                contenedorProductos.getChildren().add(new Label("No hay productos registrados."));
                return;
            }

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

    private VBox crearTarjetaProducto(Producto producto) {
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

        Label nombreLabel = new Label(producto.getNombre());
        nombreLabel.getStyleClass().add("producto-text");

        Button detallesBtn = new Button("Más detalles");
        detallesBtn.getStyleClass().add("venta-detalles-btn");
        detallesBtn.setOnAction(e -> mostrarDetalles(producto));

        Button editarBtn = new Button("Editar");
        editarBtn.getStyleClass().add("venta-detalles-btn");
        editarBtn.setOnAction(e -> handleEditarProducto(producto));

        HBox botonesBox = new HBox(10, detallesBtn, editarBtn);
        botonesBox.setAlignment(Pos.CENTER);

        VBox tarjeta = new VBox(12, topPane, imageView, nombreLabel, botonesBox);
        tarjeta.setAlignment(Pos.TOP_CENTER);
        tarjeta.getStyleClass().add("producto-card");
        tarjeta.setPrefSize(230, 320);

        return tarjeta;
    }

    private void handleEditarProducto(Producto producto) {
        LOGGER.info("Abriendo diálogo para editar producto ID: " + producto.getId_producto());
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/org/fran/gestortienda/ui/add_producto.fxml"));
            Parent view = loader.load();

            AddProductoController dialogController = loader.getController();
            dialogController.setProductoParaEditar(producto);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Editar Producto");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(contenedorProductos.getScene().getWindow());

            Scene scene = new Scene(view);
            dialogStage.setScene(scene);

            dialogController.setDialogStage(dialogStage);
            dialogStage.showAndWait();

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

    private void mostrarDetalles(Producto p) {
        LOGGER.info("Mostrando detalles para el producto ID: " + p.getId_producto());
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

    public void borrarSeleccionados() {
        if (productosSeleccionados.isEmpty()) {
            LOGGER.info("Intento de borrado de productos sin selección.");
            new Alert(Alert.AlertType.INFORMATION, "No has seleccionado ningún producto para borrar.").showAndWait();
            return;
        }

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
        cargarProductos();

        new Alert(Alert.AlertType.INFORMATION, borrados + " producto(s) borrado(s)").showAndWait();
        LOGGER.info(borrados + " productos borrados exitosamente.");
    }

    public void filtrarProductos(String modo, String texto) {
        if (texto == null || texto.isBlank()) {
            cargarProductos();
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

