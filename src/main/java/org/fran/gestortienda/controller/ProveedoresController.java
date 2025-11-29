package org.fran.gestortienda.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
import org.fran.gestortienda.DAO.ProveedorDAO;
import org.fran.gestortienda.model.entity.Producto;
import org.fran.gestortienda.model.entity.Proveedor;
import org.fran.gestortienda.utils.LoggerUtil;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

public class ProveedoresController implements Initializable {

    private static final Logger LOGGER = LoggerUtil.getLogger();

    @FXML
    private TilePane contenedorProveedores;

    private final ProveedorDAO proveedorDAO = new ProveedorDAO();
    private final Set<Proveedor> proveedoresSeleccionados = new HashSet<>();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        LOGGER.info("Inicializando ProveedoresController y cargando proveedores...");
        cargarProveedores();
    }

    public void cargarProveedores() {
        try {
            contenedorProveedores.getChildren().clear();
            List<Proveedor> proveedores = proveedorDAO.getAll(); // <-- CAMBIO

            if (proveedores.isEmpty()) {
                contenedorProveedores.getChildren().add(new Label("No hay proveedores para mostrar."));
                return;
            }

            for (Proveedor proveedor : proveedores) { // <-- CAMBIO
                VBox tarjetaProveedor = crearTarjeta(proveedor); // <-- CAMBIO
                contenedorProveedores.getChildren().add(tarjetaProveedor);
            }
            LOGGER.info("Se cargaron " + proveedores.size() + " proveedores.");

        } catch (SQLException e) {
            LOGGER.severe("Error de SQL al cargar los proveedores: " + e.getMessage());
            e.printStackTrace();
        }
    }

// --- REEMPLAZA TU MÉTODO crearTarjeta CON ESTE ---

    // --- REEMPLAZA TU MÉTODO crearTarjeta CON ESTE ---

    private VBox crearTarjeta(Proveedor proveedor) {
        StackPane topPane = new StackPane();
        topPane.setPadding(new javafx.geometry.Insets(0, 10, 0, 15));

        Label idLabel = new Label("#" + proveedor.getId_proveedor());
        idLabel.getStyleClass().add("proveedor-id");
        StackPane.setAlignment(idLabel, Pos.CENTER_LEFT);

        CheckBox checkBox = new CheckBox();
        StackPane.setAlignment(checkBox, Pos.CENTER_RIGHT);
        checkBox.setOnAction(event -> {
            if (checkBox.isSelected()) {
                proveedoresSeleccionados.add(proveedor);
            } else {
                proveedoresSeleccionados.remove(proveedor);
            }
        });
        topPane.getChildren().addAll(idLabel, checkBox);

        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/org/fran/gestortienda/img/icono-proveedores2.png")));
        imageView.setFitWidth(90);
        imageView.setFitHeight(90);
        imageView.setPreserveRatio(true);

        Label nombreLabel = new Label(proveedor.getNombre());
        nombreLabel.getStyleClass().add("proveedor-text");

        Label telefonoLabel = new Label("Tel: " + proveedor.getTelefono());
        telefonoLabel.getStyleClass().add("proveedor-text");

        Label correoLabel = new Label(proveedor.getCorreo());
        correoLabel.getStyleClass().add("proveedor-text");
        correoLabel.setWrapText(true);

        Button editarBtn = new Button("Editar");
        editarBtn.getStyleClass().add("proveedor-accion-btn");
        editarBtn.setOnAction(e -> handleEditarProveedor(proveedor));

        // --- 1. AÑADIMOS EL NUEVO BOTÓN ---
        Button verProductosBtn = new Button("Ver Productos");
        verProductosBtn.getStyleClass().add("proveedor-accion-btn"); // Reutilizamos el mismo estilo
        verProductosBtn.setOnAction(e -> handleVerProductos(proveedor));

        // --- 2. AÑADIMOS AMBOS BOTONES AL HBOX ---
        HBox botonesBox = new HBox(10, editarBtn, verProductosBtn);
        botonesBox.setAlignment(Pos.CENTER);

        // --- 3. AÑADIMOS EL HBOX A LA TARJETA ---
        VBox tarjeta = new VBox(12, topPane, imageView, nombreLabel, telefonoLabel, correoLabel, botonesBox);
        tarjeta.setAlignment(Pos.TOP_CENTER);
        tarjeta.getStyleClass().add("proveedor-card");
        tarjeta.setPrefSize(230, 320); // Aumentamos un poco la altura para que quepan los botones

        return tarjeta;
    }
    // --- REEMPLAZA/AÑADE ESTE MÉTODO EN TU CLASE ProveedoresController ---

    /**
     * Muestra una alerta con la lista de productos de un proveedor.
     */
    private void handleVerProductos(Proveedor proveedor) {
        try {
            // 1. Obtener los productos del proveedor desde el DAO
            List<Producto> productos = new ProductoDAO().findByProveedorId(proveedor.getId_proveedor());

            // 2. Crear el Alert
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Productos del Proveedor");
            alert.setHeaderText("Productos suministrados por: " + proveedor.getNombre());

            // 3. Crear la ListView para mostrar los productos
            ListView<String> listView = new ListView<>();
            if (productos.isEmpty()) {
                listView.getItems().add("Este proveedor no tiene productos asociados.");
            } else {
                for (Producto producto : productos) {
                    // Añadimos un texto formateado a la lista
                    listView.getItems().add(
                            String.format("#%d - %s (Stock: %d)",
                                    producto.getId_producto(),
                                    producto.getNombre(),
                                    producto.getStock()
                            )
                    );
                }
            }

            // Ajustamos el tamaño de la ListView para que no sea ni muy grande ni muy pequeña
            listView.setPrefHeight(200);

            // 4. ¡LA MAGIA! Insertamos la ListView dentro del panel expandible del Alert
            alert.getDialogPane().setExpandableContent(new VBox(listView));
            alert.getDialogPane().setExpanded(true); // Mostramos el contenido expandido por defecto

            alert.showAndWait();

        } catch (SQLException e) {
            LOGGER.severe("Error al cargar los productos del proveedor: " + e.getMessage());
            new Alert(Alert.AlertType.ERROR, "No se pudieron cargar los productos.").showAndWait();
        }
    }

    /**
     * Abre el diálogo de 'Añadir Proveedor' en modo edición.
     * @param proveedor El proveedor a editar.
     */
    private void handleEditarProveedor(Proveedor proveedor) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/fran/gestortienda/ui/add_proveedor.fxml"));
            Parent view = loader.load();

            AddProveedorController dialogController = loader.getController();
            dialogController.setProveedorParaEditar(proveedor);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Editar Proveedor");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(contenedorProveedores.getScene().getWindow());

            Scene scene = new Scene(view);
            dialogStage.setScene(scene);

            dialogController.setDialogStage(dialogStage);
            dialogStage.showAndWait();

            if (dialogController.isGuardado()) {
                Proveedor proveedorEditado = dialogController.getNuevoProveedor();
                if (proveedorEditado != null) {
                    new ProveedorDAO().update(proveedorEditado);
                    LOGGER.info("Proveedor actualizado: " + proveedorEditado.getNombre());
                    cargarProveedores();
                }
            }
        } catch (IOException | SQLException e) {
            LOGGER.severe("Error al abrir o procesar el diálogo de edición de proveedor.");
            e.printStackTrace();
        }
    }

    // --- COPIA Y PEGA ESTOS MÉTODOS DENTRO DE TU CLASE ProveedoresController ---

    /**
     * MÉTODO PÚBLICO PARA FILTRAR
     * Filtra los proveedores según el modo y el texto de búsqueda.
     */
    public void filtrarProveedores(String modo, String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            cargarProveedores();
            return;
        }
        List<Proveedor> proveedoresFiltrados = new ArrayList<>();
        try {
            switch (modo.toLowerCase()) {
                case "id":
                    // El filtro por ID no estaba implementado, lo añado
                    try {
                        int id = Integer.parseInt(texto);
                        Proveedor proveedor = proveedorDAO.getById(id);
                        if (proveedor != null) {
                            proveedoresFiltrados.add(proveedor);
                        }
                    } catch (NumberFormatException e) {
                        LOGGER.warning("El texto de búsqueda por ID no es un número válido: " + texto);
                    }
                    break;
                case "nombre":
                    proveedoresFiltrados = proveedorDAO.findByNombre(texto);
                    break;
                case "telefono":
                    // Suponiendo que tienes un método findByTelefono en ProveedorDAO
                    // Si no lo tienes, este caso no hará nada.
                    LOGGER.warning("La búsqueda por teléfono aún no está implementada en el DAO.");
                    break;
                case "correo":
                    proveedoresFiltrados = proveedorDAO.findByCorreo(texto);
                    break;
            }
            actualizarVista(proveedoresFiltrados);
        } catch (SQLException e) {
            LOGGER.severe("Error de SQL al filtrar proveedores: " + e.getMessage());
        }
    }

    /**
     * Método ayudante que limpia y repuebla el TilePane con una lista de proveedores.
     */
    private void actualizarVista(List<Proveedor> proveedores) {
        contenedorProveedores.getChildren().clear();
        if (proveedores.isEmpty()) {
            contenedorProveedores.getChildren().add(new Label("No se encontraron proveedores."));
        } else {
            for (Proveedor proveedor : proveedores) {
                VBox tarjetaProveedor = crearTarjeta(proveedor);
                contenedorProveedores.getChildren().add(tarjetaProveedor);
            }
        }
    }

    /**
     * Método PÚBLICO que será llamado desde MainController para borrar.
     */
    public void borrarSeleccionados() {
        if (proveedoresSeleccionados.isEmpty()) {
            LOGGER.info("No hay proveedores seleccionados para borrar.");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Borrado");
            alert.setHeaderText(null);
            alert.setContentText("No has seleccionado ningún proveedor para borrar.");
            alert.showAndWait();
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmar Borrado");
        confirmAlert.setHeaderText("Vas a borrar " + proveedoresSeleccionados.size() + " proveedor(es).");
        confirmAlert.setContentText("¿Estás seguro de que quieres continuar? Esta acción no se puede deshacer.");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            LOGGER.info("Borrando proveedores seleccionados...");
            int borradosExitosamente = 0;
            int borradosFallidos = 0;

            for (Proveedor proveedor : proveedoresSeleccionados) {
                try {
                    if (proveedorDAO.delete(proveedor)) {
                        borradosExitosamente++;
                    }
                } catch (java.sql.SQLIntegrityConstraintViolationException e) {
                    borradosFallidos++;
                    LOGGER.warning("No se pudo borrar el proveedor ID " + proveedor.getId_proveedor() + " porque tiene productos asociados.");
                } catch (SQLException e) {
                    borradosFallidos++;
                    LOGGER.severe("Error de SQL al borrar el proveedor ID " + proveedor.getId_proveedor() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }

            proveedoresSeleccionados.clear();
            cargarProveedores();

            if (borradosFallidos > 0) {
                Alert resumenAlert = new Alert(Alert.AlertType.WARNING);
                resumenAlert.setTitle("Resultado del Borrado");
                resumenAlert.setHeaderText("Se borraron " + borradosExitosamente + " proveedores.");
                resumenAlert.setContentText(borradosFallidos + " proveedor(es) no se pudieron borrar porque tienen productos asociados.");
                resumenAlert.showAndWait();
            } else {
                LOGGER.info("Proveedores borrados exitosamente.");
            }
        } else {
            LOGGER.info("Borrado cancelado por el usuario.");
        }
    }
}
