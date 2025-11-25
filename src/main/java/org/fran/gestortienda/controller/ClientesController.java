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

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controlador para la vista de clientes (clientes.fxml).
 * Se encarga de la lógica de la interfaz de usuario para la gestión de clientes.
 */
public class ClientesController implements Initializable {

    @FXML
    private TilePane contenedorClientes; // Contenedor para las tarjetas de clientes

    /**
     * Este método se llama automáticamente después de que el FXML ha sido cargado.
     * Carga los datos de los clientes desde la base de datos y los muestra en la interfaz.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("ClientesController inicializado. Cargando clientes desde la BD...");
        cargarClientes();
    }

    /**
     * Obtiene los clientes de la base de datos usando ClienteDAO y los muestra en el TilePane.
     */
    private void cargarClientes() {
        // 1. Limpiar las tarjetas de ejemplo que puedan existir en el FXML.
        contenedorClientes.getChildren().clear();

        // 2. Instanciar el DAO para acceder a los datos.
        ClienteDAO clienteDAO = new ClienteDAO();

        try {
            // 3. Obtener la lista de todos los clientes.
            List<Cliente> listaClientes = clienteDAO.getAll();

            // 4. Recorrer la lista y crear una tarjeta por cada cliente.
            for (Cliente cliente : listaClientes) {
                VBox tarjeta = crearTarjetaCliente(cliente);
                contenedorClientes.getChildren().add(tarjeta);
            }
            System.out.println("Se han cargado " + listaClientes.size() + " clientes.");

        } catch (SQLException e) {
            System.err.println("Error al cargar los clientes desde la base de datos.");
            e.printStackTrace();
            // Opcional: Mostrar un mensaje de error en la interfaz.
        }
    }

    /**
     * Crea un VBox que representa la tarjeta de un cliente con su información.
     * @param cliente El objeto Cliente con los datos a mostrar.
     * @return Un VBox estilizado que representa la tarjeta del cliente.
     */
    private VBox crearTarjetaCliente(Cliente cliente) {
        // Contenedor principal de la tarjeta
        VBox tarjeta = new VBox();
        tarjeta.setAlignment(Pos.TOP_CENTER);
        tarjeta.setSpacing(10);
        tarjeta.getStyleClass().add("cliente-card");
        tarjeta.setPrefSize(230, 230);

        // Etiqueta para el ID
        Label idLabel = new Label("ID: " + cliente.getId_cliente());
        idLabel.getStyleClass().add("cliente-id");

        // Imagen del cliente (usando un ícono por defecto)
        ImageView imageView = new ImageView();
        imageView.setFitWidth(90);
        imageView.setFitHeight(90);
        imageView.setPreserveRatio(true);
        try {
            // Carga la imagen desde los recursos
            Image image = new Image(getClass().getResourceAsStream("/org/fran/gestortienda/img/icono-clientes.png"));
            imageView.setImage(image);
        } catch (Exception e) {
            System.err.println("No se pudo cargar la imagen del cliente: " + e.getMessage());
        }


        // Etiqueta para el Nombre
        Label nombreLabel = new Label(cliente.getNombre());
        nombreLabel.getStyleClass().add("cliente-text");

        // Etiqueta para el Teléfono
        Label telefonoLabel = new Label(cliente.getTelefono());
        telefonoLabel.getStyleClass().add("cliente-text");

        // Etiqueta para la Dirección
        Label direccionLabel = new Label(cliente.getDireccion());
        direccionLabel.getStyleClass().add("cliente-text");
        direccionLabel.setWrapText(true); // Permite que el texto se ajuste si es muy largo

        // Añadir todos los elementos a la tarjeta
        tarjeta.getChildren().addAll(idLabel, imageView, nombreLabel, telefonoLabel, direccionLabel);

        return tarjeta;
    }
}