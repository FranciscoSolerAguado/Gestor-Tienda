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
import org.fran.gestortienda.MainApp;
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
        LOGGER.info("Inicializando ProveedoresController...");
        cargarProveedores();
    }

    /**
     * Recupera la lista completa de proveedores de la base de datos
     * y genera las tarjetas visuales para mostrarlas en la interfaz.
     */
    public void cargarProveedores() {
        try {
            // Limpiamos el contenedor antes de repoblarlo
            contenedorProveedores.getChildren().clear();
            List<Proveedor> proveedores = proveedorDAO.getAll();

            // Verificación de lista vacía
            if (proveedores.isEmpty()) {
                LOGGER.info("No se encontraron proveedores en la base de datos.");
                contenedorProveedores.getChildren().add(new Label("No hay proveedores para mostrar."));
                return;
            }

            // creamos una tarjeta por cada proveedor
            for (Proveedor proveedor : proveedores) {
                VBox tarjetaProveedor = crearTarjeta(proveedor);
                contenedorProveedores.getChildren().add(tarjetaProveedor);
            }
            LOGGER.info("Se cargaron " + proveedores.size() + " proveedores en la vista.");
        } catch (SQLException e) {
            LOGGER.severe("Error de SQL al cargar los proveedores: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Construye la tarjeta para un proveedor.
     * @param proveedor El objeto proveedor con los datos a mostrar.
     * @return VBox configurado con la interfaz de la tarjeta.
     */
    private VBox crearTarjeta(Proveedor proveedor) {
        // Contenedor superior para el ID y el CheckBox
        StackPane topPane = new StackPane();
        topPane.setPadding(new javafx.geometry.Insets(0, 10, 0, 15));

        Label idLabel = new Label("#" + proveedor.getId_proveedor());
        idLabel.getStyleClass().add("proveedor-id");
        StackPane.setAlignment(idLabel, Pos.CENTER_LEFT);

        // CheckBox para la selección múltiple (borrado)
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

        // Configuración de la imagen del proveedor
        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/org/fran/gestortienda/img/icono-proveedores2.png")));
        imageView.setFitWidth(90);
        imageView.setFitHeight(90);
        imageView.setPreserveRatio(true);

        // Etiquetas de información (Nombre, Teléfono, Correo)
        Label nombreLabel = new Label(proveedor.getNombre());
        nombreLabel.getStyleClass().add("proveedor-text");

        Label telefonoLabel = new Label("Tel: " + proveedor.getTelefono());
        telefonoLabel.getStyleClass().add("proveedor-text");

        Label correoLabel = new Label(proveedor.getCorreo());
        correoLabel.getStyleClass().add("proveedor-text");
        correoLabel.setWrapText(true);

        // Botones de acción: Editar y Ver Productos
        Button editarBtn = new Button("Editar");
        editarBtn.getStyleClass().add("proveedor-accion-btn");
        editarBtn.setOnAction(e -> handleEditarProveedor(proveedor));

        Button verProductosBtn = new Button("Ver Productos");
        verProductosBtn.getStyleClass().add("proveedor-accion-btn");
        verProductosBtn.setOnAction(e -> handleVerProductos(proveedor));

        HBox botonesBox = new HBox(10, editarBtn, verProductosBtn);
        botonesBox.setAlignment(Pos.CENTER);

        // Estructura final vertical de la tarjeta
        VBox tarjeta = new VBox(12, topPane, imageView, nombreLabel, telefonoLabel, correoLabel, botonesBox);
        tarjeta.setAlignment(Pos.TOP_CENTER);
        tarjeta.getStyleClass().add("proveedor-card");
        tarjeta.setPrefSize(230, 320);

        return tarjeta;
    }

    /**
     * Muestra un diálogo informativo con la lista de productos suministrados por el proveedor.
     * @param proveedor El proveedor del que queremos consultar el stock.
     */
    private void handleVerProductos(Proveedor proveedor) {
        LOGGER.info("Abriendo diálogo para ver productos del proveedor ID: " + proveedor.getId_proveedor());
        try {
            // Consultamos a la base de datos por los productos de este proveedor
            List<Producto> productos = new ProductoDAO().findByProveedorId(proveedor.getId_proveedor());

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Productos del Proveedor");
            alert.setHeaderText("Productos suministrados por: " + proveedor.getNombre());

            // Usamos un ListView para listar los resultados
            ListView<String> listView = new ListView<>();
            if (productos.isEmpty()) {
                listView.getItems().add("Este proveedor no tiene productos asociados.");
            } else {
                for (Producto producto : productos) {
                    listView.getItems().add(
                            String.format("#%d - %s (Stock: %d)",
                                    producto.getId_producto(),
                                    producto.getNombre(),
                                    producto.getStock()
                            )
                    );
                }
            }

            listView.setPrefHeight(200);
            alert.getDialogPane().setExpandableContent(new VBox(listView));
            alert.getDialogPane().setExpanded(true);

            alert.showAndWait();

        } catch (SQLException e) {
            LOGGER.severe("Error al cargar los productos del proveedor ID " + proveedor.getId_proveedor() + ": " + e.getMessage());
            new Alert(Alert.AlertType.ERROR, "No se pudieron cargar los productos.").showAndWait();
        }
    }

    /**
     * Abre la ventana para editar los datos de un proveedor existente.
     * @param proveedor El objeto proveedor a modificar.
     */
    private void handleEditarProveedor(Proveedor proveedor) {
        LOGGER.info("Abriendo diálogo para editar proveedor ID: " + proveedor.getId_proveedor());
        try {
            // Carga de la vista FXML de edición
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/org/fran/gestortienda/ui/add_proveedor.fxml"));
            Parent view = loader.load();

            AddProveedorController dialogController = loader.getController();
            // Pre-cargamos los datos en el formulario
            dialogController.setProveedorParaEditar(proveedor);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Editar Proveedor");
            dialogStage.initModality(Modality.WINDOW_MODAL); // Bloqueo modal
            dialogStage.initOwner(contenedorProveedores.getScene().getWindow());

            Scene scene = new Scene(view);
            dialogStage.setScene(scene);

            dialogController.setDialogStage(dialogStage);
            dialogStage.showAndWait();

            // actualizamos en BD y refrescamos la vista
            if (dialogController.isGuardado()) {
                Proveedor proveedorEditado = dialogController.getNuevoProveedor();
                if (proveedorEditado != null) {
                    new ProveedorDAO().update(proveedorEditado);
                    LOGGER.info("Proveedor ID " + proveedorEditado.getId_proveedor() + " actualizado a: " + proveedorEditado.getNombre());
                    cargarProveedores();
                }
            }
        } catch (IOException | SQLException e) {
            LOGGER.severe("Error al abrir o procesar el diálogo de edición de proveedor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Filtra la lista de proveedores según el criterio seleccionado.
     * @param modo El campo por el que buscar (ID, Nombre, Telefono, Correo).
     * @param texto El texto a buscar.
     */
    public void filtrarProveedores(String modo, String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            cargarProveedores(); // Si no hay texto, mostramos todos
            return;
        }
        LOGGER.info("Filtrando proveedores por '" + modo + "' con el texto: '" + texto + "'");
        List<Proveedor> proveedoresFiltrados = new ArrayList<>();
        try {
            switch (modo.toLowerCase()) {
                case "id":
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
     * Carga contenedor con una lista específica de proveedores.
     * @param proveedores La lista filtrada o actualizada a mostrar.
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
     * Elimina de la base de datos los proveedores seleccionados mediante los checkbox.
     * Gestiona las restricciones de integridad (no borra si tienen productos asociados).
     */
    public void borrarSeleccionados() {
        if (proveedoresSeleccionados.isEmpty()) {
            LOGGER.info("Intento de borrado de proveedores sin selección.");
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "No has seleccionado ningún proveedor para borrar.");
            alert.setTitle("Borrado");
            alert.setHeaderText(null);
            alert.showAndWait();
            return;
        }


        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmar Borrado");
        confirmAlert.setHeaderText("Vas a borrar " + proveedoresSeleccionados.size() + " proveedor(es).");
        confirmAlert.setContentText("¿Estás seguro de que quieres continuar? Esta acción no se puede deshacer.");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            LOGGER.info("Iniciando borrado de " + proveedoresSeleccionados.size() + " proveedores.");
            int borradosExitosamente = 0;
            int borradosFallidos = 0;

            // borrar uno a uno
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

            // recarga
            proveedoresSeleccionados.clear();
            cargarProveedores();


            if (borradosFallidos > 0) {
                Alert resumenAlert = new Alert(Alert.AlertType.WARNING);
                resumenAlert.setTitle("Resultado del Borrado");
                resumenAlert.setHeaderText("Se borraron " + borradosExitosamente + " proveedores.");
                resumenAlert.setContentText(borradosFallidos + " proveedor(es) no se pudieron borrar porque tienen productos asociados.");
                resumenAlert.showAndWait();
            } else {
                LOGGER.info(borradosExitosamente + " proveedores borrados exitosamente.");
            }
        } else {
            LOGGER.info("Borrado de proveedores cancelado por el usuario.");
        }
    }
}