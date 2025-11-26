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
import org.fran.gestortienda.DAO.ClienteDAO;
import org.fran.gestortienda.model.entity.Cliente;
import org.fran.gestortienda.utils.LoggerUtil;

import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

public class ClientesController implements Initializable {

    private static final Logger LOGGER = LoggerUtil.getLogger();

    private Object activeController;


    @FXML
    private TilePane contenedorClientes;

    private final ClienteDAO clienteDAO = new ClienteDAO();

    private final Set<Cliente> clientesSeleccionados = new HashSet<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        LOGGER.info("Inicializando ClientesController y cargando clientes...");
        cargarClientes();
    }

    /**
     * Obtiene los clientes de la base de datos y los muestra en la vista.
     */
    private void cargarClientes() {
        try {
            // 1. Limpiar el contenido de ejemplo que pueda haber en el FXML
            contenedorClientes.getChildren().clear();

            // 2. Obtener la lista de clientes desde el DAO
            List<Cliente> clientes = clienteDAO.getAll();

            if (clientes.isEmpty()) {
                LOGGER.info("No se encontraron clientes en la base de datos.");
                // Opcional: Mostrar un mensaje en la UI
                contenedorClientes.getChildren().add(new Label("No hay clientes para mostrar."));
                return;
            }

            // 3. Recorrer la lista y crear una tarjeta por cada cliente
            for (Cliente cliente : clientes) {
                VBox tarjetaCliente = crearTarjeta(cliente);
                contenedorClientes.getChildren().add(tarjetaCliente);
            }

            LOGGER.info("Se cargaron " + clientes.size() + " clientes.");

        } catch (SQLException e) {
            LOGGER.severe("Error de SQL al cargar los clientes: " + e.getMessage());
            e.printStackTrace();
            // Opcional: Mostrar una alerta al usuario
        }
    }

    /**
     * Método PÚBLICO que será llamado desde MainController para borrar.
     * VERSIÓN MEJORADA: Captura errores de integridad de datos.
     */
    public void borrarSeleccionados() {
        if (clientesSeleccionados.isEmpty()) {
            LOGGER.info("No hay clientes seleccionados para borrar.");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Borrado");
            alert.setHeaderText(null);
            alert.setContentText("No has seleccionado ningún cliente para borrar.");
            alert.showAndWait();
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmar Borrado");
        confirmAlert.setHeaderText("Vas a borrar " + clientesSeleccionados.size() + " cliente(s).");
        confirmAlert.setContentText("¿Estás seguro de que quieres continuar? Esta acción no se puede deshacer.");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            LOGGER.info("Borrando clientes seleccionados...");
            int borradosExitosamente = 0;
            int borradosFallidos = 0;

            for (Cliente cliente : clientesSeleccionados) {
                try {
                    // Intentamos borrar cada cliente individualmente
                    if (clienteDAO.delete(cliente)) {
                        borradosExitosamente++;
                    }
                } catch (java.sql.SQLIntegrityConstraintViolationException e) {
                    // --- ESTA ES LA LÓGICA CLAVE ---
                    // Si falla por una restricción de clave foránea, lo contamos y seguimos.
                    borradosFallidos++;
                    LOGGER.warning("No se pudo borrar el cliente ID " + cliente.getId_cliente() + " porque tiene ventas asociadas.");
                } catch (SQLException e) {
                    // Para cualquier otro error de SQL
                    borradosFallidos++;
                    LOGGER.severe("Error de SQL al borrar el cliente ID " + cliente.getId_cliente() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }

            // Limpiar la selección y recargar la vista
            clientesSeleccionados.clear();
            cargarClientes();

            // Mostrar un resumen al usuario
            if (borradosFallidos > 0) {
                Alert resumenAlert = new Alert(Alert.AlertType.WARNING);
                resumenAlert.setTitle("Resultado del Borrado");
                resumenAlert.setHeaderText("Se borraron " + borradosExitosamente + " clientes.");
                resumenAlert.setContentText(borradosFallidos + " cliente(s) no se pudieron borrar porque tienen ventas asociadas.");
                resumenAlert.showAndWait();
            } else {
                LOGGER.info("Clientes borrados exitosamente.");
            }
        } else {
            LOGGER.info("Borrado cancelado por el usuario.");
        }
    }

    /**
     * Crea un VBox (tarjeta) para un cliente específico.
     *
     * @param cliente El objeto Cliente con los datos a mostrar.
     * @return Un VBox configurado como una tarjeta de cliente.
     */
    private VBox crearTarjeta(Cliente cliente) {
        // --- Contenedor para la parte superior (ID y CheckBox) ---
        StackPane topPane = new StackPane();
        topPane.setPadding(new javafx.geometry.Insets(0, 10, 0, 15));

        Label idLabel = new Label("#" + cliente.getId_cliente());
        idLabel.getStyleClass().add("cliente-id");
        StackPane.setAlignment(idLabel, Pos.CENTER_LEFT);

        // Creamos el CheckBox
        CheckBox checkBox = new CheckBox();
        StackPane.setAlignment(checkBox, Pos.CENTER_RIGHT);

        // Añadimos la lógica para la selección
        checkBox.setOnAction(event -> {
            if (checkBox.isSelected()) {
                clientesSeleccionados.add(cliente);
            } else {
                clientesSeleccionados.remove(cliente);
            }
            LOGGER.info("Clientes seleccionados: " + clientesSeleccionados.size());
        });

        topPane.getChildren().addAll(idLabel, checkBox);

        // --- Resto de componentes (sin cambios) ---
        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/org/fran/gestortienda/img/icono-clientes2.png")));
        imageView.setFitWidth(90);
        imageView.setFitHeight(90);
        imageView.setPreserveRatio(true);

        Label nombreLabel = new Label(cliente.getNombre());
        nombreLabel.getStyleClass().add("cliente-text");

        Label telefonoLabel = new Label("Tel: " + cliente.getTelefono());
        telefonoLabel.getStyleClass().add("cliente-text");

        Label direccionLabel = new Label(cliente.getDireccion());
        direccionLabel.getStyleClass().add("cliente-text");
        direccionLabel.setWrapText(true);

        // --- VBox principal de la tarjeta ---
        VBox tarjeta = new VBox(12, topPane, imageView, nombreLabel, telefonoLabel, direccionLabel);
        tarjeta.setAlignment(Pos.TOP_CENTER);
        tarjeta.getStyleClass().add("cliente-card");
        tarjeta.setPrefSize(230, 230);

        return tarjeta;
    }
}