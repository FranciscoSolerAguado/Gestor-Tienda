package org.fran.gestortienda.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import org.fran.gestortienda.DAO.ProveedorDAO;
import org.fran.gestortienda.model.entity.Proveedor;
import org.fran.gestortienda.utils.LoggerUtil;

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

    private VBox crearTarjeta(Proveedor proveedor) {
        // --- Contenedor para la parte superior (ID y CheckBox) ---
        StackPane topPane = new StackPane();
        topPane.setPadding(new javafx.geometry.Insets(0, 10, 0, 15));

        Label idLabel = new Label("#" + proveedor.getId_proveedor());
        idLabel.getStyleClass().add("proveedor-id"); // Reutilizamos estilo
        StackPane.setAlignment(idLabel, Pos.CENTER_LEFT);

        // 2. Creamos el CheckBox
        CheckBox checkBox = new CheckBox();
        StackPane.setAlignment(checkBox, Pos.CENTER_RIGHT);

        // 3. Añadimos la lógica para la selección
        checkBox.setOnAction(event -> {
            if (checkBox.isSelected()) {
                proveedoresSeleccionados.add(proveedor);
            } else {
                proveedoresSeleccionados.remove(proveedor);
            }
            LOGGER.info("Proveedores seleccionados: " + proveedoresSeleccionados.size());
        });

        topPane.getChildren().addAll(idLabel, checkBox);

        // --- Resto de componentes (sin cambios) ---
        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/org/fran/gestortienda/img/icono-proveedores2.png")));
        imageView.setFitWidth(90);
        imageView.setFitHeight(90);
        imageView.setPreserveRatio(true);

        Label nombreLabel = new Label(proveedor.getNombre());
        nombreLabel.getStyleClass().add("proveedor-text"); // Reutilizamos estilo

        Label telefonoLabel = new Label("Tel: " + proveedor.getTelefono());
        telefonoLabel.getStyleClass().add("proveedor-text"); // Reutilizamos estilo

        Label correoLabel = new Label(proveedor.getCorreo());
        correoLabel.getStyleClass().add("proveedor-text"); // Reutilizamos estilo
        correoLabel.setWrapText(true);

        // --- VBox principal de la tarjeta ---
        VBox tarjeta = new VBox(12, topPane, imageView, nombreLabel, telefonoLabel, correoLabel);
        tarjeta.setAlignment(Pos.TOP_CENTER);
        tarjeta.getStyleClass().add("proveedor-card"); // Reutilizamos estilo
        tarjeta.setPrefSize(230, 230);

        return tarjeta;
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
