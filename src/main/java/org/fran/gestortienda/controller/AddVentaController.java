package org.fran.gestortienda.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.fran.gestortienda.DAO.ClienteDAO;
import org.fran.gestortienda.DAO.Detalle_VentaDAO;
import org.fran.gestortienda.DAO.ProductoDAO;
import org.fran.gestortienda.DAO.VentaDAO;
import org.fran.gestortienda.model.entity.Cliente;
import org.fran.gestortienda.model.entity.Detalle_Venta;
import org.fran.gestortienda.model.entity.Producto;
import org.fran.gestortienda.model.entity.Venta;
import org.fran.gestortienda.utils.LoggerUtil;
import org.fran.gestortienda.utils.ReggexUtil;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.logging.Logger;

public class AddVentaController {

    private static final Logger LOGGER = LoggerUtil.getLogger();

    @FXML private DatePicker fechaPicker;
    @FXML private ComboBox<Cliente> clienteCombo;
    @FXML private TextField totalField;

    @FXML private TableView<Detalle_Venta> tablaDetalles;
    @FXML private TableColumn<Detalle_Venta, String> colProducto;
    @FXML private TableColumn<Detalle_Venta, Integer> colCantidad;
    @FXML private TableColumn<Detalle_Venta, Double> colPrecio;
    @FXML private TableColumn<Detalle_Venta, Double> colDescuento;
    @FXML private TableColumn<Detalle_Venta, Double> colIVA;
    @FXML private TableColumn<Detalle_Venta, Double> colSubtotal;

    private ObservableList<Detalle_Venta> detallesList = FXCollections.observableArrayList();

    private Stage dialogStage;
    private boolean guardado = false;
    private Venta ventaAEditar = null;

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ProductoDAO productoDAO = new ProductoDAO();
    private final VentaDAO ventaDAO = new VentaDAO();
    private final Detalle_VentaDAO detalleDAO = new Detalle_VentaDAO();

    public void setDialogStage(Stage stage) {
        dialogStage = stage;
    }

    public boolean isGuardado() {
        return guardado;
    }

    public void setVentaParaEditar(Venta venta) {
        this.ventaAEditar = venta;

        if (venta.getFecha() != null) {
            java.util.Date date = new java.util.Date(venta.getFecha().getTime());
            LocalDate localDate = date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            fechaPicker.setValue(localDate);
        }

        if (venta.getCliente() != null) {
            clienteCombo.setValue(venta.getCliente());
        }
        totalField.setText(String.format(java.util.Locale.US, "%.2f", venta.getTotal()));

        try {
            detallesList.setAll(new Detalle_VentaDAO().getByVenta(venta.getId_venta()));
            tablaDetalles.setItems(detallesList);
        } catch (SQLException e) {
            LOGGER.severe("Error de SQL al cargar los detalles de la venta a editar: " + e.getMessage());
            e.printStackTrace();
        }
        LOGGER.info("Diálogo de venta puesto en modo edición para la venta ID: " + venta.getId_venta());
    }

    @FXML
    private void initialize() {
        cargarClientes();
        configurarTabla();
        totalField.setText("0.00");
    }

    private void cargarClientes() {
        try {
            clienteCombo.setItems(FXCollections.observableArrayList(clienteDAO.getAll()));
        } catch (SQLException e) {
            LOGGER.severe("Error de SQL al cargar clientes para el ComboBox: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void configurarTabla() {
        colProducto.setCellValueFactory(cellData -> {
            Producto producto = cellData.getValue().getProducto();
            if (producto != null) {
                return new javafx.beans.property.SimpleStringProperty(producto.getNombre());
            } else {
                return new javafx.beans.property.SimpleStringProperty("<Producto no encontrado>");
            }
        });

        colCantidad.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getCantidad()));
        colPrecio.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getPrecio_unitario()));
        colDescuento.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getDescuento()));
        colIVA.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getIva()));
        colSubtotal.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getSubtotal()));

        tablaDetalles.setItems(detallesList);
    }

    @FXML
    private void handleAddProducto() {
        try {
            Producto producto = pedirProducto();
            if (producto == null) {
                LOGGER.info("Selección de producto cancelada.");
                return;
            }

            TextInputDialog cantidadDialog = new TextInputDialog("1");
            cantidadDialog.setTitle("Añadir Producto");
            cantidadDialog.setHeaderText("Introduce la cantidad para: " + producto.getNombre());
            cantidadDialog.setContentText("Cantidad:");

            Optional<String> cantidadResult = cantidadDialog.showAndWait();
            if (cantidadResult.isEmpty() || cantidadResult.get().isBlank()) {
                LOGGER.info("Introducción de cantidad cancelada.");
                return;
            }

            TextInputDialog descuentoDialog = new TextInputDialog("0.0");
            descuentoDialog.setTitle("Añadir Producto");
            descuentoDialog.setHeaderText("Introduce el descuento (%) para: " + producto.getNombre());
            descuentoDialog.setContentText("Descuento (%):");

            Optional<String> descuentoResult = descuentoDialog.showAndWait();
            if (descuentoResult.isEmpty() || descuentoResult.get().isBlank()) {
                LOGGER.info("Introducción de descuento cancelada.");
                return;
            }

            String cantidadStr = cantidadResult.get();
            String descuentoStr = descuentoResult.get().replace(',', '.');

            if (!ReggexUtil.DECIMAL_REGEX.matcher(descuentoStr).matches()) {
                LOGGER.warning("Validación fallida: formato de descuento incorrecto -> " + descuentoStr);
                new Alert(Alert.AlertType.WARNING, "El formato del descuento no es válido (ej: 12.99).").showAndWait();
                return;
            }

            int cantidad = Integer.parseInt(cantidadStr);
            double descuento = Double.parseDouble(descuentoStr);

            if (cantidad <= 0 || descuento < 0 || descuento > 100) {
                LOGGER.warning("Validación fallida: cantidad o descuento fuera de rango -> Cantidad: " + cantidad + ", Descuento: " + descuento);
                new Alert(Alert.AlertType.WARNING, "La cantidad debe ser mayor que 0 y el descuento debe estar entre 0 y 100.").showAndWait();
                return;
            }

            double precioUnitario = producto.getPrecio();
            double iva = 21.0;
            double precioConDescuento = precioUnitario * (1 - descuento / 100);
            double subtotal = (precioConDescuento * cantidad) * (1 + iva / 100);

            Detalle_Venta dv = new Detalle_Venta(0, null, producto, cantidad, descuento, precioUnitario, iva, subtotal);
            detallesList.add(dv);
            actualizarTotal();
            LOGGER.info("Producto '" + producto.getNombre() + "' añadido a la venta con cantidad: " + cantidad + " y descuento: " + descuento + "%");

        } catch (NumberFormatException e) {
            LOGGER.warning("Error de formato de número al añadir producto a la venta: " + e.getMessage());
            new Alert(Alert.AlertType.ERROR, "La cantidad debe ser un número entero válido.").showAndWait();
        } catch (Exception e) {
            LOGGER.severe("Error inesperado al añadir producto a la venta: " + e.getMessage());
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Ocurrió un error al añadir el producto.").showAndWait();
        }
    }

    private Producto pedirProducto() throws SQLException {
        List<Producto> productosDisponibles = productoDAO.getAll();
        if (productosDisponibles.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "No hay productos disponibles para añadir.").showAndWait();
            return null;
        }

        ChoiceDialog<Producto> dialog = new ChoiceDialog<>(productosDisponibles.get(0), productosDisponibles);
        dialog.setTitle("Seleccionar Producto");
        dialog.setHeaderText("Elige un producto de la lista");
        dialog.setContentText("Producto:");

        return dialog.showAndWait().orElse(null);
    }

    private void actualizarTotal() {
        double total = detallesList.stream()
                .mapToDouble(Detalle_Venta::getSubtotal)
                .sum();
        totalField.setText(String.format(Locale.US, "%.2f", total));
    }

    // --- REEMPLAZA ESTE MÉTODO EN TU CLASE AddVentaController ---

    @FXML
    private void handleSave() {
        try {
            if (fechaPicker.getValue() == null || clienteCombo.getValue() == null) {
                new Alert(Alert.AlertType.WARNING, "Debe seleccionar fecha y cliente.").showAndWait();
                return;
            }

            if (ventaAEditar != null) {
                // MODO EDICIÓN
                // ... (la lógica de edición de la venta principal se queda igual)
                ventaAEditar.setFecha(java.sql.Date.valueOf(fechaPicker.getValue()));
                ventaAEditar.setCliente(clienteCombo.getValue());
                String totalText = totalField.getText().replace(',', '.');
                ventaAEditar.setTotal(Double.parseDouble(totalText));
                ventaDAO.update(ventaAEditar);

                // Lógica de "borrar y re-insertar" detalles
                detalleDAO.deleteByVentaId(ventaAEditar.getId_venta());
                for (Detalle_Venta dv : detallesList) {
                    dv.setVenta(ventaAEditar);
                    detalleDAO.add(dv);
                    // Aquí también iría la lógica de actualización de stock
                }
                LOGGER.info("Venta ID " + ventaAEditar.getId_venta() + " y sus detalles han sido actualizados.");

            } else {
                // MODO CREACIÓN
                Venta nuevaVenta = new Venta();
                nuevaVenta.setFecha(java.sql.Date.valueOf(fechaPicker.getValue()));
                Cliente clienteSeleccionado = clienteCombo.getValue();
                nuevaVenta.setCliente(clienteSeleccionado);
                String totalText = totalField.getText().replace(',', '.');
                nuevaVenta.setTotal(Double.parseDouble(totalText));

                boolean guardadoConExito = ventaDAO.add(nuevaVenta);
                if (!guardadoConExito) {
                    throw new SQLException("No se pudo guardar la venta principal.");
                }

                Venta ventaGuardada = ventaDAO.getLastByCliente(clienteSeleccionado.getId_cliente());
                if (ventaGuardada == null) {
                    throw new SQLException("No se pudo recuperar la venta recién guardada.");
                }
                ventaGuardada.setCliente(clienteSeleccionado);

                // --- SOLUCIÓN AQUÍ: Bucle para guardar detalles Y ACTUALIZAR STOCK ---
                for (Detalle_Venta dv : detallesList) {
                    // 1. Guardamos el detalle de la venta
                    dv.setVenta(ventaGuardada);
                    detalleDAO.add(dv);

                    // 2. Obtenemos el producto y la cantidad vendida
                    Producto productoVendido = dv.getProducto();
                    int cantidadVendida = dv.getCantidad();

                    // 3. Calculamos y establecemos el nuevo stock
                    int nuevoStock = productoVendido.getStock() - cantidadVendida;
                    productoVendido.setStock(nuevoStock);

                    // 4. Actualizamos el producto en la base de datos
                    productoDAO.update(productoVendido);
                    LOGGER.info(String.format("Stock actualizado para producto ID %d: %d -> %d",
                            productoVendido.getId_producto(),
                            productoVendido.getStock() + cantidadVendida, // Stock original
                            nuevoStock));
                }
                // --- FIN DE LA SOLUCIÓN ---
                LOGGER.info("Nueva venta guardada con ID: " + ventaGuardada.getId_venta());
            }

            guardado = true;
            dialogStage.close();

        } catch (Exception e) {
            LOGGER.severe("Error al guardar la venta: " + e.getMessage());
            new Alert(Alert.AlertType.ERROR, "Ocurrió un error al guardar la venta.\n" + e.getMessage()).showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        LOGGER.info("Operación de añadir/editar venta cancelada.");
        dialogStage.close();
    }
}