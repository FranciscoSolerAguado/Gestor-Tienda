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

// Controlador principal para la pestaña de Clientes.
// Aquí nos encargamos de mostrar la rejilla con las tarjetas, filtrar y borrar.
public class ClientesController implements Initializable {

    private static final Logger LOGGER = LoggerUtil.getLogger();

    // El contenedor visual (tarjetas) de cada cliente
    @FXML
    private TilePane contenedorClientes;

    // Acceso a datos
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final Set<Cliente> clientesSeleccionados = new HashSet<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        LOGGER.info("Inicializando ClientesController...");
        cargarClientes();
    }

    /**
     * Método que carga todos los clientes desde la base de datos y los muestra en la vista.
     */
    void cargarClientes() {
        try {
            // Limpiamos lo que hubiera antes para no duplicar.
            contenedorClientes.getChildren().clear();
            List<Cliente> clientes = clienteDAO.getAll();

            // Si no hay datos
            if (clientes.isEmpty()) {
                LOGGER.info("No se encontraron clientes en la base de datos.");
                contenedorClientes.getChildren().add(new Label("No hay clientes para mostrar."));
                return;
            }

            // Si hay clientes, fabricamos una tarjeta para cada uno y la añadimos al panel.
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

    /**
     * Método para aplicar los filtros que podemos a los clientes
     * @param modo el modo que siempre sera de busqueda
     * @param texto el texto que buscamos
     */
    public void filtrarClientes(String modo, String texto) {
        // Si se borra el texto, desaplicamos el filtro
        if (texto == null || texto.trim().isEmpty()) {
            cargarClientes();
            return;
        }

        LOGGER.info("Filtrando clientes por '" + modo + "' con el texto: '" + texto + "'");
        List<Cliente> clientesFiltrados = new ArrayList<>();
        try {
            switch (modo.toLowerCase()) {
                case "id":
                    // Si busca por ID, hay que asegurarse de que sea un número.
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
            // Refrescamos la vista solo con los que coinciden.
            actualizarVista(clientesFiltrados);
        } catch (SQLException e) {
            LOGGER.severe("Error de SQL al filtrar clientes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Método que actualiza la vista con los clientes filtrados
     * @param clientes la lista de clientes a mostrar
     */
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

    /**
     * Método para borrar los clientes seleccionados, solo borra a aquellos seleccionados con el checkbox o los que no tengan ventas asociadas
     *
     */
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
                    // Si el cliente tiene ventas, no deja borrarlo
                    borradosFallidos++;
                    LOGGER.warning("No se pudo borrar el cliente ID " + cliente.getId_cliente() + " porque tiene ventas asociadas.");
                } catch (SQLException e) {
                    borradosFallidos++;
                    LOGGER.severe("Error de SQL al borrar el cliente ID " + cliente.getId_cliente() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }

            // Limpiamos la selección y recargamos la pantalla.
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

    // --- Tarjetas ---

    /**
     * Método que crea una tarjeta para un cliente
     * @param cliente el cliente a mostrar
     */
    private VBox crearTarjeta(Cliente cliente) {
        // Parte de arriba: ID a la izquierda, CheckBox a la derecha.
        StackPane topPane = new StackPane();
        topPane.setPadding(new javafx.geometry.Insets(0, 10, 0, 15));

        Label idLabel = new Label("#" + cliente.getId_cliente());
        idLabel.getStyleClass().add("cliente-id");
        StackPane.setAlignment(idLabel, Pos.CENTER_LEFT);

        CheckBox checkBox = new CheckBox();
        StackPane.setAlignment(checkBox, Pos.CENTER_RIGHT);

        // Lógica del CheckBox: Si lo marcas, te vas al Set de borrado.
        checkBox.setOnAction(event -> {
            if (checkBox.isSelected()) {
                clientesSeleccionados.add(cliente);
            } else {
                clientesSeleccionados.remove(cliente);
            }
        });

        topPane.getChildren().addAll(idLabel, checkBox);

        // Imagen del avatar (se usa una generica para la creacion, aunque luego mas tarde si tiene o el usuario la añade se cambia sola).
        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/org/fran/gestortienda/img/icono-clientes2.png")));
        imageView.setFitWidth(90);
        imageView.setFitHeight(90);
        imageView.setPreserveRatio(true);

        // Datos del cliente.
        Label nombreLabel = new Label(cliente.getNombre());
        nombreLabel.getStyleClass().add("cliente-text");

        Label telefonoLabel = new Label("Tel: " + cliente.getTelefono());
        telefonoLabel.getStyleClass().add("cliente-text");

        Label direccionLabel = new Label(cliente.getDireccion());
        direccionLabel.getStyleClass().add("cliente-text");
        direccionLabel.setWrapText(true);

        // º    Botnes de Editar y Ver Ventas
        Button editarBtn = new Button("Editar");
        editarBtn.getStyleClass().add("cliente-accion-btn");
        editarBtn.setOnAction(e -> handleEditarCliente(cliente));

        Button verVentasBtn = new Button("Ver Ventas");
        verVentasBtn.getStyleClass().add("cliente-accion-btn");
        verVentasBtn.setOnAction(e -> handleVerVentas(cliente));

        HBox botonesBox = new HBox(10, editarBtn, verVentasBtn);
        botonesBox.setAlignment(Pos.CENTER);

        // Alineacion vertical
        VBox tarjeta = new VBox(12, topPane, imageView, nombreLabel, telefonoLabel, direccionLabel, botonesBox);
        tarjeta.setAlignment(Pos.TOP_CENTER);
        tarjeta.getStyleClass().add("cliente-card");
        tarjeta.setPrefSize(230, 320);

        return tarjeta;
    }

    /**
     * Método que abre la ventana de ventas del cliente
     * @param cliente el cliente al que queremos ver las ventas
     */
    private void handleVerVentas(Cliente cliente) {
        LOGGER.info("Abriendo diálogo para ver ventas del cliente ID: " + cliente.getId_cliente());
        try {
            List<Venta> ventas = new VentaDAO().findByCliente(cliente.getId_cliente());

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ventas del Cliente");
            alert.setHeaderText("Ventas realizadas por: " + cliente.getNombre());

            // Usamos un ListView
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

    /**
     * Método que abre la ventana de edición de cliente
     * @param cliente el cliente al que queremos editar
     */
    private void handleEditarCliente(Cliente cliente) {
        LOGGER.info("Abriendo diálogo para editar cliente ID: " + cliente.getId_cliente());
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/org/fran/gestortienda/ui/add_cliente.fxml"));
            Parent view = loader.load();

            // Recuperamos el controlador y le pasamos el cliente para que rellene los campos automaticamentes
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

            // Si guardó, actualizamos en BD y actualizamos la vista
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