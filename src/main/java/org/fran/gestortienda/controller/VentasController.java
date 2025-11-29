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
    private String modoFiltro = "NINGUNO";


    @FXML
    private TilePane contenedorVentas;
    @FXML
    private void handleFiltroPorID() {
        modoFiltro = "ID";
    }

    @FXML
    private void handleFiltroPorFecha() {
        modoFiltro = "FECHA";
    }

    @FXML
    private void handleFiltroPorCliente() {
        modoFiltro = "CLIENTE";
    }


    private final VentaDAO ventaDAO = new VentaDAO();
    private final Detalle_VentaDAO detalleDAO = new Detalle_VentaDAO();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        LOGGER.info("Inicializando VentasController y cargando ventas...");
        cargarVentas();
    }

    public void cargarVentas() {
        try {
            contenedorVentas.getChildren().clear();
            List<Venta> ventas = ventaDAO.getAll();

            if (ventas.isEmpty()) {
                contenedorVentas.getChildren().add(new Label("No hay ventas para mostrar."));
                return;
            }

            for (Venta venta : ventas) {
                VBox tarjeta = crearTarjeta(venta);
                contenedorVentas.getChildren().add(tarjeta);
            }

        } catch (SQLException e) {
            LOGGER.severe("Error SQL al cargar ventas: " + e.getMessage());
        }
    }

    // --- REEMPLAZA TU MÉTODO crearTarjeta CON ESTE ---

    private VBox crearTarjeta(Venta venta) {

        String nombreCliente = "Sin cliente";
        try {
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

        StackPane topPane = new StackPane();
        topPane.setPadding(new Insets(0, 10, 0, 15));
        Label idLabel = new Label("#" + venta.getId_venta());
        idLabel.getStyleClass().add("venta-id");
        StackPane.setAlignment(idLabel, Pos.CENTER_LEFT);
        CheckBox checkBox = new CheckBox();
        StackPane.setAlignment(checkBox, Pos.CENTER_RIGHT);
        checkBox.setOnAction(e -> {
            if (checkBox.isSelected()) ventasSeleccionadas.add(venta);
            else ventasSeleccionadas.remove(venta);
        });
        topPane.getChildren().addAll(idLabel, checkBox);

        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/org/fran/gestortienda/img/icono-ventas2.png")));
        imageView.setFitWidth(90);
        imageView.setFitHeight(90);
        imageView.setPreserveRatio(true);

        Label fechaLabel = new Label("Fecha: " + venta.getFecha());
        fechaLabel.getStyleClass().add("venta-text");

        Label totalLabel = new Label("Total: " + String.format(java.util.Locale.US, "%.2f", venta.getTotal()) + " €");
        totalLabel.getStyleClass().add("venta-text");

        Label clienteLabel = new Label("Cliente: " + nombreCliente);
        clienteLabel.setWrapText(true);
        clienteLabel.getStyleClass().add("venta-text");

        // --- LÓGICA DE BOTONES ---
        Button detallesBtn = new Button("Detalles");
        detallesBtn.getStyleClass().add("venta-detalles-btn");
        detallesBtn.setOnAction(e -> mostrarDetallesVenta(venta));

        Button editarBtn = new Button("Editar"); // Botón añadido
        editarBtn.getStyleClass().add("venta-detalles-btn");
        editarBtn.setOnAction(e -> handleEditarVenta(venta)); // Acción añadida

        HBox botonesBox = new HBox(10, detallesBtn, editarBtn); // Contenedor para botones
        botonesBox.setAlignment(Pos.CENTER);
        // --- FIN LÓGICA DE BOTONES ---

        VBox tarjeta = new VBox(12, topPane, imageView, fechaLabel, totalLabel, clienteLabel, botonesBox);
        tarjeta.setAlignment(Pos.TOP_CENTER);
        tarjeta.getStyleClass().add("venta-card");
        tarjeta.setPrefSize(230, 290);

        return tarjeta;
    }

    // --- AÑADE ESTE MÉTODO A TU CLASE VentasController ---

    /**
     * Abre el diálogo de 'Añadir Venta' en modo edición.
     * @param venta La venta a editar.
     */
    private void handleEditarVenta(Venta venta) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/fran/gestortienda/ui/add_venta.fxml"));
            Parent view = loader.load();

            AddVentaController dialogController = loader.getController();

            // Le pasamos la venta que queremos editar
            dialogController.setVentaParaEditar(venta);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Editar Venta");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(contenedorVentas.getScene().getWindow());

            Scene scene = new Scene(view);
            dialogStage.setScene(scene);

            dialogController.setDialogStage(dialogStage);
            dialogStage.showAndWait();

            // Si se guardó, refrescamos la vista
            if (dialogController.isGuardado()) {
                cargarVentas();
            }

        } catch (IOException e) {
            LOGGER.severe("Error al abrir el diálogo de edición de venta.");
            e.printStackTrace();
        }
    }



    private void mostrarDetallesVenta(Venta venta) {
        try {
            // ===============================
            // 1. CARGAR DETALLES DE LA VENTA
            // ===============================
            List<Detalle_Venta> detalles = detalleDAO.getByVenta(venta.getId_venta());

            // ===============================
            // 2. CARGAR CLIENTE SI ES NECESARIO
            // ===============================
            String nombreCliente = "Sin cliente";
            if (venta.getCliente() != null && venta.getCliente().getId_cliente() > 0) {
                nombreCliente = venta.getCliente().getNombre();
            } else {
                try {
                    ClienteDAO clienteDAO = new ClienteDAO();
                    var cli = clienteDAO.getById(venta.getCliente().getId_cliente());
                    if (cli != null) nombreCliente = cli.getNombre();
                } catch (Exception ignored) {}
            }

            // ===============================
            // 3. CREAR POPUP
            // ===============================
            VBox root = new VBox(12);
            root.setStyle("-fx-padding: 20; -fx-background-color: #f0f0f0;");
            root.setAlignment(Pos.TOP_LEFT);

            Label titulo = new Label("Detalles de Venta #" + venta.getId_venta());
            titulo.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");

            Label infoCliente = new Label("Cliente: " + nombreCliente);
            Label infoFecha   = new Label("Fecha: " + venta.getFecha());
            Label infoTotal   = new Label("Total: " + venta.getTotal() + " €");

            // ===============================
            // 4. TABLA DE DETALLES
            // ===============================
            VBox listaDetalles = new VBox(8);
            listaDetalles.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-color: black;");

            if (detalles.isEmpty()) {
                listaDetalles.getChildren().add(new Label("No hay detalles para esta venta."));
            } else {
                for (Detalle_Venta dv : detalles) {

                    Label linea = new Label(
                            "Producto ID: " + dv.getProducto().getId_producto() +
                                    " | Cantidad: " + dv.getCantidad() +
                                    " | Precio: " + dv.getPrecio_unitario() + " €" +
                                    " | Desc: " + dv.getDescuento() +
                                    " | IVA: " + dv.getIva() +
                                    " | Subtotal: " + dv.getSubtotal() + " €"
                    );
                    linea.setStyle("-fx-font-size: 14;");

                    listaDetalles.getChildren().add(linea);
                }
            }

            // ===============================
            // 5. BOTÓN CERRAR
            // ===============================
            Button cerrar = new Button("Cerrar");
            cerrar.setOnAction(e -> cerrar.getScene().getWindow().hide());
            cerrar.setStyle("-fx-background-color: #b29e84; -fx-text-fill: white; -fx-font-weight: bold;");

            // ===============================
            // 6. ENSAMBLAR VENTANA
            // ===============================
            root.getChildren().addAll(titulo, infoCliente, infoFecha, infoTotal, listaDetalles, cerrar);

            javafx.stage.Stage popup = new javafx.stage.Stage();
            popup.setTitle("Detalles de la venta");
            popup.setScene(new javafx.scene.Scene(root, 800, 450));
            popup.setResizable(false);
            popup.show();

        } catch (SQLException e) {
            LOGGER.severe("Error al cargar detalles de venta: " + e.getMessage());
        }
    }

    /**
     * MÉTODO PÚBLICO PARA FILTRAR
     * Filtra las ventas según el modo y el texto de búsqueda.
     */
    public void filtrarVentas(String modo, String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            cargarVentas();
            return;
        }

        List<Venta> ventasFiltradas = new ArrayList<>();

        try {
            switch (modo.toLowerCase()) {

                case "id":
                    try {
                        int id = Integer.parseInt(texto);
                        Venta venta = ventaDAO.getById(id);
                        if (venta != null) {
                            ventasFiltradas.add(venta);
                        }
                    } catch (NumberFormatException e) {
                        LOGGER.warning("El texto de búsqueda por ID no es un número válido: " + texto);
                    }
                    break;

                case "fecha":
                    try {
                        java.sql.Date fecha = java.sql.Date.valueOf(texto); // yyyy-MM-dd
                        ventasFiltradas = ventaDAO.findByFecha(fecha);
                    } catch (IllegalArgumentException e) {
                        LOGGER.warning("El texto de búsqueda no es una fecha válida (formato yyyy-MM-dd): " + texto);
                    }
                    break;


                case "total":
                    try {
                        double total = Double.parseDouble(texto);
                        ventasFiltradas = ventaDAO.findByTotal(total);
                    } catch (NumberFormatException e) {
                        LOGGER.warning("El texto de búsqueda por Total no es un número válido: " + texto);
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

    public void borrarSeleccionados() {

        if (ventasSeleccionadas.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Borrado");
            alert.setHeaderText(null);
            alert.setContentText("No has seleccionado ninguna venta para borrar.");
            alert.showAndWait();
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmar Borrado");
        confirmAlert.setHeaderText("Vas a borrar " + ventasSeleccionadas.size() + " venta(s).");
        confirmAlert.setContentText("¿Estás seguro? Esta acción no se puede deshacer.");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (!result.isPresent() || result.get() != ButtonType.OK) {
            LOGGER.info("Borrado cancelado por el usuario.");
            return;
        }

        int borradas = 0;
        int fallidas = 0;

        for (Venta venta : ventasSeleccionadas) {
            try {
                if (ventaDAO.delete(venta)) {
                    borradas++;
                }
            } catch (java.sql.SQLIntegrityConstraintViolationException e) {
                // No puede borrarse porque tiene detalles asociados
                fallidas++;
                LOGGER.warning("Venta ID " + venta.getId_venta() + " no se pudo borrar porque tiene detalles asociados.");
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
            resumen.setContentText(fallidas + " ventas no se pudieron borrar porque tienen detalles asociados.");
        }
        resumen.showAndWait();
    }


    /**
     * Método ayudante que limpia y repuebla el TilePane con una lista de ventas.
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
