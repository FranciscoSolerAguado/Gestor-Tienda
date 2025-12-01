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
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.fran.gestortienda.DAO.ClienteDAO;
import org.fran.gestortienda.DAO.VentaDAO;
import org.fran.gestortienda.DAO.Detalle_VentaDAO;
import org.fran.gestortienda.MainApp;
import org.fran.gestortienda.model.entity.Venta;
import org.fran.gestortienda.model.entity.Detalle_Venta;
import org.fran.gestortienda.utils.LoggerUtil;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class VentasController implements Initializable {

    private static final Logger LOGGER = LoggerUtil.getLogger();

    private final List<Venta> ventasSeleccionadas = new ArrayList<>();

    @FXML
    private TilePane contenedorVentas;

    // DAOs para el acceso a datos
    private final VentaDAO ventaDAO = new VentaDAO();
    private final Detalle_VentaDAO detalleDAO = new Detalle_VentaDAO();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        LOGGER.info("Inicializando VentasController...");
        cargarVentas();
    }

    /**
     * Carga todas las ventas de la base de datos y las muestra
     * * Si no hay ventas, muestra un mensaje informativo.
     */
    public void cargarVentas() {
        try {
            // Limpiamos el panel antes de añadir elementos nuevos
            contenedorVentas.getChildren().clear();
            List<Venta> ventas = ventaDAO.getAll();

            // Comprobamos si la lista está vacía
            if (ventas.isEmpty()) {
                LOGGER.info("No se encontraron ventas en la base de datos.");
                contenedorVentas.getChildren().add(new Label("No hay ventas para mostrar."));
                return;
            }

            // Generamos una tarjeta por cada venta
            for (Venta venta : ventas) {
                VBox tarjeta = crearTarjeta(venta);
                contenedorVentas.getChildren().add(tarjeta);
            }
            LOGGER.info("Se cargaron " + ventas.size() + " ventas en la vista.");
        } catch (SQLException e) {
            LOGGER.severe("Error SQL al cargar ventas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Construye la Tarjeta que representa una venta.
     * Incluye lógica para obtener el nombre del cliente asociado.
     * @param venta El objeto venta con los datos a mostrar.
     * @return Un VBox con la estructura visual de la tarjeta.
     */
    private VBox crearTarjeta(Venta venta) {
        String nombreCliente = "Sin cliente";
        try {
            // Intentamos recuperar el nombre del cliente si la venta tiene un ID de cliente válido
            if (venta.getCliente() != null && venta.getCliente().getId_cliente() > 0) {
                ClienteDAO clienteDAO = new ClienteDAO();
                var cliente = clienteDAO.getById(venta.getCliente().getId_cliente());
                if (cliente != null) {
                    venta.setCliente(cliente);
                    nombreCliente = cliente.getNombre();
                }
            }
        } catch (Exception e) {
            nombreCliente = "Error al cargar cliente";
        }

        // id y checkbox
        StackPane topPane = new StackPane();
        topPane.setPadding(new Insets(0, 10, 0, 15));

        Label idLabel = new Label("#" + venta.getId_venta());
        idLabel.getStyleClass().add("venta-id");
        StackPane.setAlignment(idLabel, Pos.CENTER_LEFT);

        CheckBox checkBox = new CheckBox();
        StackPane.setAlignment(checkBox, Pos.CENTER_RIGHT);

        // borrado multiple si no hace una cosa hace otra
        checkBox.setOnAction(e -> {
            if (checkBox.isSelected()) ventasSeleccionadas.add(venta);
            else ventasSeleccionadas.remove(venta);
        });
        topPane.getChildren().addAll(idLabel, checkBox);

        // Icono de la venta
        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/org/fran/gestortienda/img/icono-ventas2.png")));
        imageView.setFitWidth(90);
        imageView.setFitHeight(90);
        imageView.setPreserveRatio(true);

        // Fecha, Total, Cliente
        Label fechaLabel = new Label("Fecha: " + venta.getFecha());
        fechaLabel.getStyleClass().add("venta-text");

        Label totalLabel = new Label("Total: " + String.format(java.util.Locale.US, "%.2f", venta.getTotal()) + " €");
        totalLabel.getStyleClass().add("venta-text");

        Label clienteLabel = new Label("Cliente: " + nombreCliente);
        clienteLabel.setWrapText(true);
        clienteLabel.getStyleClass().add("venta-text");

        // Detalles y Editar
        Button detallesBtn = new Button("Detalles");
        detallesBtn.getStyleClass().add("venta-detalles-btn");
        detallesBtn.setOnAction(e -> mostrarDetallesVenta(venta));

        Button editarBtn = new Button("Editar");
        editarBtn.getStyleClass().add("venta-detalles-btn");
        editarBtn.setOnAction(e -> handleEditarVenta(venta));

        HBox botonesBox = new HBox(10, detallesBtn, editarBtn);
        botonesBox.setAlignment(Pos.CENTER);

        //Alineacion vertical
        VBox tarjeta = new VBox(12, topPane, imageView, fechaLabel, totalLabel, clienteLabel, botonesBox);
        tarjeta.setAlignment(Pos.TOP_CENTER);
        tarjeta.getStyleClass().add("venta-card");
        tarjeta.setPrefSize(230, 320);

        return tarjeta;
    }

    /**
     * Abre el formulario para editar una venta existente.
     * Permite modificar la cabecera y las líneas de detalle.
     * @param venta La venta que se va a editar.
     */
    private void handleEditarVenta(Venta venta) {
        LOGGER.info("Abriendo diálogo para editar venta ID: " + venta.getId_venta());
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/org/fran/gestortienda/ui/add_venta.fxml"));
            Parent view = loader.load();

            AddVentaController dialogController = loader.getController();
            // Pasamos la venta al controlador del diálogo para que rellene los campos automaticamnete
            dialogController.setVentaParaEditar(venta);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Editar Venta");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(contenedorVentas.getScene().getWindow());

            Scene scene = new Scene(view);
            dialogStage.setScene(scene);

            dialogController.setDialogStage(dialogStage);
            dialogStage.showAndWait();

            // Si se guardaron cambios, recargamos la lista
            if (dialogController.isGuardado()) {
                LOGGER.info("Diálogo de edición de venta cerrado y guardado. Refrescando vista...");
                cargarVentas();
            } else {
                LOGGER.info("Diálogo de edición de venta cerrado sin guardar.");
            }
        } catch (IOException e) {
            LOGGER.severe("Error al abrir el diálogo de edición de venta: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * @param venta La venta de la cual queremos ver los detalles.
     */
    private void mostrarDetallesVenta(Venta venta) {
        LOGGER.info("Mostrando detalles para la venta ID: " + venta.getId_venta());
        try {
            List<Detalle_Venta> detalles = detalleDAO.getByVenta(venta.getId_venta());
            String nombreCliente = (venta.getCliente() != null) ? venta.getCliente().getNombre() : "Sin cliente";

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Detalles de la venta");
            alert.setHeaderText("Venta #" + venta.getId_venta() + " - Cliente: " + nombreCliente);

            // Usamos un ListView
            ListView<String> listView = new ListView<>();
            if (detalles.isEmpty()) {
                listView.getItems().add("No hay detalles para esta venta.");
            } else {
                for (Detalle_Venta dv : detalles) {
                    listView.getItems().add(
                            String.format("Producto: %s | Cant: %d | Precio: %.2f€ | Desc: %.1f%% | Subtotal: %.2f€",
                                    dv.getProducto() != null ? dv.getProducto().getNombre() : "N/A",
                                    dv.getCantidad(),
                                    dv.getPrecio_unitario(),
                                    dv.getDescuento(),
                                    dv.getSubtotal()
                            )
                    );
                }
            }
            listView.setPrefSize(500, 200);
            alert.getDialogPane().setExpandableContent(new VBox(listView));
            alert.getDialogPane().setExpanded(true);
            alert.showAndWait();
        } catch (SQLException e) {
            LOGGER.severe("Error al cargar detalles de venta: " + e.getMessage());
        }
    }

    /**
     * Filtra las ventas mostradas en pantalla según el criterio seleccionado.
     * @param modo El criterio de búsqueda ("id", "fecha", "cliente").
     * @param texto El valor a buscar.
     */
    public void filtrarVentas(String modo, String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            cargarVentas(); // Si el texto está vacío, mostramos todo
            return;
        }
        LOGGER.info("Filtrando ventas por '" + modo + "' con el texto: '" + texto + "'");
        List<Venta> ventasFiltradas = new ArrayList<>();
        try {
            switch (modo.toLowerCase()) {
                case "id":
                    try {
                        int id = Integer.parseInt(texto);
                        Venta venta = ventaDAO.getById(id);
                        if (venta != null) ventasFiltradas.add(venta);
                    } catch (NumberFormatException e) {
                        LOGGER.warning("El texto de búsqueda por ID no es un número válido: " + texto);
                    }
                    break;
                case "fecha":
                    try {

                        java.sql.Date fecha = java.sql.Date.valueOf(texto);
                        ventasFiltradas = ventaDAO.findByFecha(fecha);
                    } catch (IllegalArgumentException e) {
                        LOGGER.warning("El texto de búsqueda no es una fecha válida (formato yyyy-MM-dd): " + texto);
                    }
                    break;
                case "cliente":
                    try {
                        int idCliente = Integer.parseInt(texto);
                        ventasFiltradas = ventaDAO.findByCliente(idCliente);
                    } catch (NumberFormatException e) {
                        LOGGER.warning("El texto de búsqueda por Cliente no es un ID válido: " + texto);
                    }
                    break;
            }
            actualizarVista(ventasFiltradas);
        } catch (SQLException e) {
            LOGGER.severe("Error de SQL al filtrar ventas: " + e.getMessage());
        }
    }

    /**
     * Elimina las ventas seleccionadas.
     * Primero borra los detalles (líneas de producto) para evitar errores de integridad referencial.
     */
    public void borrarSeleccionados() {
        if (ventasSeleccionadas.isEmpty()) {
            LOGGER.info("Intento de borrado de ventas sin selección.");
            new Alert(Alert.AlertType.INFORMATION, "No has seleccionado ninguna venta para borrar.").showAndWait();
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmar Borrado");
        confirmAlert.setHeaderText("Vas a borrar " + ventasSeleccionadas.size() + " venta(s).");
        confirmAlert.setContentText("¿Estás seguro? Esta acción no se puede deshacer.");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            LOGGER.info("Borrado de ventas cancelado por el usuario.");
            return;
        }

        LOGGER.info("Iniciando borrado de " + ventasSeleccionadas.size() + " ventas.");
        int borradas = 0;
        int fallidas = 0;

        for (Venta venta : ventasSeleccionadas) {
            try {
                detalleDAO.deleteByVentaId(venta.getId_venta());
                if (ventaDAO.delete(venta)) {
                    borradas++;
                }
            } catch (SQLException e) {
                fallidas++;
                LOGGER.severe("Error al borrar venta ID " + venta.getId_venta() + ": " + e.getMessage());
            }
        }

        ventasSeleccionadas.clear();
        cargarVentas();

        Alert resumen = new Alert(Alert.AlertType.INFORMATION);
        resumen.setTitle("Resultado del borrado");
        resumen.setHeaderText(borradas + " ventas borradas.");
        if (fallidas > 0) {
            resumen.setContentText(fallidas + " ventas no se pudieron borrar.");
        }
        resumen.showAndWait();
        LOGGER.info(borradas + " ventas borradas, " + fallidas + " fallidas.");
    }

    /**
     * Método auxiliar para limpiar el contenedor y recargarlo con una lista específica.
     * @param ventas Lista de ventas a mostrar.
     */
    private void actualizarVista(List<Venta> ventas) {
        contenedorVentas.getChildren().clear();
        if (ventas == null || ventas.isEmpty()) {
            contenedorVentas.getChildren().add(new Label("No se encontraron ventas."));
            return;
        }
        for (Venta venta : ventas) {
            VBox tarjetaVenta = crearTarjeta(venta);
            contenedorVentas.getChildren().add(tarjetaVenta);
        }
    }
}