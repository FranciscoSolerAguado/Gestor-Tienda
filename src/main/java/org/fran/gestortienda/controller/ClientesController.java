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
import org.fran.gestortienda.DAO.ClienteDAO;
import org.fran.gestortienda.DAO.VentaDAO;
import org.fran.gestortienda.MainApp;
import org.fran.gestortienda.model.entity.Cliente;
import org.fran.gestortienda.model.entity.Venta;
import org.fran.gestortienda.utils.LoggerUtil;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

public class ClientesController implements Initializable {

    private static final Logger LOGGER = LoggerUtil.getLogger();

    @FXML
    private TilePane contenedorClientes;

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final Set<Cliente> clientesSeleccionados = new HashSet<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        LOGGER.info("Inicializando ClientesController...");
        cargarClientes();
    }

    void cargarClientes() {
        try {
            contenedorClientes.getChildren().clear();
            List<Cliente> clientes = clienteDAO.getAll();

            if (clientes.isEmpty()) {
                LOGGER.info("No se encontraron clientes en la base de datos.");
                contenedorClientes.getChildren().add(new Label("No hay clientes para mostrar."));
                return;
            }

            for (Cliente cliente : clientes) {
                VBox tarjetaCliente = crearTarjeta(cliente);
                contenedorClientes.getChildren().add(tarjetaCliente);
            }
            LOGGER.info("Se cargaron " + clientes.size() + " clientes en la vista.");
        } catch (SQLException e) {
            LOGGER.severe("Error de SQL al cargar los clientes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void filtrarClientes(String modo, String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            cargarClientes();
            return;
        }

        LOGGER.info("Filtrando clientes por '" + modo + "' con el texto: '" + texto + "'");
        List<Cliente> clientesFiltrados = new ArrayList<>();
        try {
            switch (modo.toLowerCase()) {
                case "id":
                    try {
                        int id = Integer.parseInt(texto);
                        Cliente cliente = clienteDAO.getById(id);
                        if (cliente != null) {
                            clientesFiltrados.add(cliente);
                        }
                    } catch (NumberFormatException e) {
                        LOGGER.warning("El texto de búsqueda por ID no es un número válido: " + texto);
                    }
                    break;
                case "nombre":
                    clientesFiltrados = clienteDAO.findByNombre(texto);
                    break;
                case "direccion":
                    clientesFiltrados = clienteDAO.findByDireccion(texto);
                    break;
            }
            actualizarVista(clientesFiltrados);
        } catch (SQLException e) {
            LOGGER.severe("Error de SQL al filtrar clientes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void actualizarVista(List<Cliente> clientes) {
        contenedorClientes.getChildren().clear();
        if (clientes.isEmpty()) {
            contenedorClientes.getChildren().add(new Label("No se encontraron clientes."));
        } else {
            for (Cliente cliente : clientes) {
                VBox tarjetaCliente = crearTarjeta(cliente);
                contenedorClientes.getChildren().add(tarjetaCliente);
            }
        }
    }

    public void borrarSeleccionados() {
        if (clientesSeleccionados.isEmpty()) {
            LOGGER.info("Intento de borrado de clientes sin selección.");
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "No has seleccionado ningún cliente para borrar.");
            alert.setTitle("Borrado");
            alert.setHeaderText(null);
            alert.showAndWait();
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmar Borrado");
        confirmAlert.setHeaderText("Vas a borrar " + clientesSeleccionados.size() + " cliente(s).");
        confirmAlert.setContentText("¿Estás seguro de que quieres continuar? Esta acción no se puede deshacer.");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            LOGGER.info("Iniciando borrado de " + clientesSeleccionados.size() + " clientes.");
            int borradosExitosamente = 0;
            int borradosFallidos = 0;

            for (Cliente cliente : clientesSeleccionados) {
                try {
                    if (clienteDAO.delete(cliente)) {
                        borradosExitosamente++;
                    }
                } catch (java.sql.SQLIntegrityConstraintViolationException e) {
                    borradosFallidos++;
                    LOGGER.warning("No se pudo borrar el cliente ID " + cliente.getId_cliente() + " porque tiene ventas asociadas.");
                } catch (SQLException e) {
                    borradosFallidos++;
                    LOGGER.severe("Error de SQL al borrar el cliente ID " + cliente.getId_cliente() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }

            clientesSeleccionados.clear();
            cargarClientes();

            if (borradosFallidos > 0) {
                Alert resumenAlert = new Alert(Alert.AlertType.WARNING);
                resumenAlert.setTitle("Resultado del Borrado");
                resumenAlert.setHeaderText("Se borraron " + borradosExitosamente + " clientes.");
                resumenAlert.setContentText(borradosFallidos + " cliente(s) no se pudieron borrar porque tienen ventas asociadas.");
                resumenAlert.showAndWait();
            } else {
                LOGGER.info(borradosExitosamente + " clientes borrados exitosamente.");
            }
        } else {
            LOGGER.info("Borrado de clientes cancelado por el usuario.");
        }
    }

    private VBox crearTarjeta(Cliente cliente) {
        StackPane topPane = new StackPane();
        topPane.setPadding(new javafx.geometry.Insets(0, 10, 0, 15));

        Label idLabel = new Label("#" + cliente.getId_cliente());
        idLabel.getStyleClass().add("cliente-id");
        StackPane.setAlignment(idLabel, Pos.CENTER_LEFT);

        CheckBox checkBox = new CheckBox();
        StackPane.setAlignment(checkBox, Pos.CENTER_RIGHT);

        checkBox.setOnAction(event -> {
            if (checkBox.isSelected()) {
                clientesSeleccionados.add(cliente);
            } else {
                clientesSeleccionados.remove(cliente);
            }
        });

        topPane.getChildren().addAll(idLabel, checkBox);

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

        Button editarBtn = new Button("Editar");
        editarBtn.getStyleClass().add("cliente-accion-btn");
        editarBtn.setOnAction(e -> handleEditarCliente(cliente));

        Button verVentasBtn = new Button("Ver Ventas");
        verVentasBtn.getStyleClass().add("cliente-accion-btn");
        verVentasBtn.setOnAction(e -> handleVerVentas(cliente));

        HBox botonesBox = new HBox(10, editarBtn, verVentasBtn);
        botonesBox.setAlignment(Pos.CENTER);

        VBox tarjeta = new VBox(12, topPane, imageView, nombreLabel, telefonoLabel, direccionLabel, botonesBox);
        tarjeta.setAlignment(Pos.TOP_CENTER);
        tarjeta.getStyleClass().add("cliente-card");
        tarjeta.setPrefSize(230, 320);

        return tarjeta;
    }

    private void handleVerVentas(Cliente cliente) {
        LOGGER.info("Abriendo diálogo para ver ventas del cliente ID: " + cliente.getId_cliente());
        try {
            List<Venta> ventas = new VentaDAO().findByCliente(cliente.getId_cliente());

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ventas del Cliente");
            alert.setHeaderText("Ventas realizadas por: " + cliente.getNombre());

            ListView<String> listView = new ListView<>();
            if (ventas.isEmpty()) {
                listView.getItems().add("Este cliente no tiene ventas registradas.");
            } else {
                for (Venta venta : ventas) {
                    listView.getItems().add(
                            String.format("Venta #%d  |  Fecha: %s  |  Total: %.2f €",
                                    venta.getId_venta(),
                                    venta.getFecha(),
                                    venta.getTotal()
                            )
                    );
                }
            }

            listView.setPrefSize(400, 200);
            alert.getDialogPane().setExpandableContent(new VBox(listView));
            alert.getDialogPane().setExpanded(true);

            alert.showAndWait();

        } catch (SQLException e) {
            LOGGER.severe("Error al cargar las ventas del cliente ID " + cliente.getId_cliente() + ": " + e.getMessage());
            new Alert(Alert.AlertType.ERROR, "No se pudieron cargar las ventas.").showAndWait();
        }
    }

    private void handleEditarCliente(Cliente cliente) {
        LOGGER.info("Abriendo diálogo para editar cliente ID: " + cliente.getId_cliente());
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/org/fran/gestortienda/ui/add_cliente.fxml"));
            Parent view = loader.load();

            AddClienteController dialogController = loader.getController();
            dialogController.setClienteParaEditar(cliente);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Editar Cliente");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(contenedorClientes.getScene().getWindow());

            Scene scene = new Scene(view);
            dialogStage.setScene(scene);

            dialogController.setDialogStage(dialogStage);
            dialogStage.showAndWait();

            if (dialogController.isGuardado()) {
                Cliente clienteEditado = dialogController.getNuevoCliente();
                if (clienteEditado != null) {
                    new ClienteDAO().update(clienteEditado);
                    LOGGER.info("Cliente ID " + clienteEditado.getId_cliente() + " actualizado a: " + clienteEditado.getNombre());
                    cargarClientes();
                }
            }
        } catch (IOException | SQLException e) {
            LOGGER.severe("Error al abrir o procesar el diálogo de edición de cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }
}