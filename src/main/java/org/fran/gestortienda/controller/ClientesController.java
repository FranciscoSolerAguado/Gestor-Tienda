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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.fran.gestortienda.DAO.ClienteDAO;
import org.fran.gestortienda.model.entity.Cliente;
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
        LOGGER.info("Inicializando ClientesController y cargando clientes...");
        cargarClientes();
    }

    /**
     * Obtiene los clientes de la base de datos y los muestra en la vista.
     */
    void cargarClientes() {
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
     * 3. MÉTODO PÚBLICO PARA FILTRAR
     * Filtra los clientes según el modo y el texto de búsqueda.
     */
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

    /**
     * Método ayudante que limpia y repuebla el TilePane con una lista de clientes.
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
    // --- REEMPLAZA TU MÉTODO crearTarjeta CON ESTE ---

    private VBox crearTarjeta(Cliente cliente) {
        // --- Contenedor para la parte superior (ID y CheckBox) ---
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
            LOGGER.info("Clientes seleccionados: " + clientesSeleccionados.size());
        });

        topPane.getChildren().addAll(idLabel, checkBox);

        // --- Resto de componentes ---
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
        editarBtn.getStyleClass().add("cliente-accion-btn"); // <-- APLICAMOS EL NUEVO ESTILO
        editarBtn.setOnAction(e -> handleEditarCliente(cliente));

        // --- VBox principal de la tarjeta ---
        VBox tarjeta = new VBox(12, topPane, imageView, nombreLabel, telefonoLabel, direccionLabel, editarBtn);
        tarjeta.setAlignment(Pos.TOP_CENTER);
        tarjeta.getStyleClass().add("cliente-card");
        tarjeta.setPrefSize(230, 290); // Un poco más de alto para el botón

        return tarjeta;
    }

    // --- REEMPLAZA/AÑADE ESTE MÉTODO EN TU CLASE ClientesController ---

    /**
     * Abre el diálogo de 'Añadir Cliente' en modo edición.
     * @param cliente El cliente a editar.
     */
    private void handleEditarCliente(Cliente cliente) {
        try {
            // 1. Cargar el FXML del diálogo
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/fran/gestortienda/ui/add_cliente.fxml"));
            Parent view = loader.load();

            // 2. Obtener el controlador del diálogo
            AddClienteController dialogController = loader.getController();

            // 3. Pasar el cliente que queremos editar
            dialogController.setClienteParaEditar(cliente);

            // 4. Crear y configurar el Stage (la ventana) del diálogo
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Editar Cliente");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            // El dueño de la ventana es la ventana actual del contenedor de clientes
            dialogStage.initOwner(contenedorClientes.getScene().getWindow());

            Scene scene = new Scene(view);
            dialogStage.setScene(scene);

            // Le pasamos el Stage al controlador del diálogo para que pueda cerrarse
            dialogController.setDialogStage(dialogStage);

            // 5. Mostrar el diálogo y esperar a que el usuario lo cierre
            dialogStage.showAndWait();

            // 6. Si el usuario guardó los cambios, procesarlos
            if (dialogController.isGuardado()) {
                Cliente clienteEditado = dialogController.getNuevoCliente();
                if (clienteEditado != null) {
                    // Llamamos al DAO para actualizar la base de datos
                    new ClienteDAO().update(clienteEditado);
                    LOGGER.info("Cliente actualizado: " + clienteEditado.getNombre());

                    // Refrescamos la vista para mostrar los cambios
                    cargarClientes();
                }
            }

        } catch (IOException | SQLException e) {
            LOGGER.severe("Error al abrir o procesar el diálogo de edición de cliente.");
            e.printStackTrace();
        }
    }
}