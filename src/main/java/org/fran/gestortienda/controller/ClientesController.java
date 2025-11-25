package org.fran.gestortienda.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import org.fran.gestortienda.DAO.ClienteDAO;
import org.fran.gestortienda.model.entity.Cliente;
import org.fran.gestortienda.utils.LoggerUtil;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class ClientesController implements Initializable {

    private static final Logger LOGGER = LoggerUtil.getLogger();

    @FXML
    private TilePane contenedorClientes;

    private final ClienteDAO clienteDAO = new ClienteDAO();

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
     * Crea un VBox (tarjeta) para un cliente específico.
     *
     * @param cliente El objeto Cliente con los datos a mostrar.
     * @return Un VBox configurado como una tarjeta de cliente.
     */
    private VBox crearTarjeta(Cliente cliente) {
        // Crear los componentes de la tarjeta
        Label idLabel = new Label("#" + cliente.getId_cliente());
        idLabel.getStyleClass().add("cliente-id");

        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/org/fran/gestortienda/img/icono-clientes.png")));
        imageView.setFitWidth(90);
        imageView.setFitHeight(90);
        imageView.setPreserveRatio(true);

        Label nombreLabel = new Label(cliente.getNombre());
        nombreLabel.getStyleClass().add("cliente-text");

        Label telefonoLabel = new Label("Tel: " + cliente.getTelefono());
        telefonoLabel.getStyleClass().add("cliente-text");

        Label direccionLabel = new Label(cliente.getDireccion());
        direccionLabel.getStyleClass().add("cliente-text");
        direccionLabel.setWrapText(true); // Para que el texto se ajuste si es muy largo

        // Crear el contenedor VBox para la tarjeta
        VBox tarjeta = new VBox(10, idLabel, imageView, nombreLabel, telefonoLabel, direccionLabel);
        tarjeta.setAlignment(Pos.TOP_CENTER);
        tarjeta.getStyleClass().add("cliente-card");
        tarjeta.setPrefSize(230, 230);

        return tarjeta;
    }
}