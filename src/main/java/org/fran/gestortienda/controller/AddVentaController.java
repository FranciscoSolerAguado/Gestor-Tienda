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
import org.fran.gestortienda.utils.ReggexUtil;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.logging.Logger;

public class AddVentaController {

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

    private static final Logger LOGGER = Logger.getLogger(AddVentaController.class.getName());


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

// --- REEMPLAZA ESTE MÉTODO EN TU CLASE AddVentaController ---

    public void setVentaParaEditar(Venta venta) {
        this.ventaAEditar = venta;

        // Rellenar los campos del formulario
        // --- SOLUCIÓN AQUÍ ---
        // Comprobamos que la fecha no sea null antes de convertirla
        if (venta.getFecha() != null) {
            java.util.Date date = new java.util.Date(venta.getFecha().getTime());
            LocalDate localDate = date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            fechaPicker.setValue(localDate);
        }
        // --- FIN DE LA SOLUCIÓN ---

        if (venta.getCliente() != null) {
            clienteCombo.setValue(venta.getCliente());
        }
        totalField.setText(String.format(java.util.Locale.US, "%.2f", venta.getTotal()));

        try {
            detallesList.setAll(new Detalle_VentaDAO().getByVenta(venta.getId_venta()));
            tablaDetalles.setItems(detallesList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
            e.printStackTrace();
        }
    }

// --- REEMPLAZA ESTE MÉTODO EN TU CLASE AddVentaController ---

    private void configurarTabla() {
        // --- SOLUCIÓN AQUÍ ---
        // Para la columna de Producto, creamos una CellValueFactory que extrae el nombre.
        colProducto.setCellValueFactory(cellData -> {
            // cellData.getValue() nos da el objeto Detalle_Venta de la fila
            Producto producto = cellData.getValue().getProducto();
            if (producto != null) {
                // Devolvemos una propiedad de String simple con el nombre del producto
                return new javafx.beans.property.SimpleStringProperty(producto.getNombre());
            } else {
                return new javafx.beans.property.SimpleStringProperty("<Producto no encontrado>");
            }
        });
        // --- FIN DE LA SOLUCIÓN ---

        colCantidad.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getCantidad()));
        colPrecio.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getPrecio_unitario()));
        colDescuento.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getDescuento()));
        colIVA.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getIva()));
        colSubtotal.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getSubtotal()));

        tablaDetalles.setItems(detallesList);
    }

// --- REEMPLAZA ESTE MÉTODO EN TU CLASE AddVentaController ---

    // --- REEMPLAZA ESTE MÉTODO EN TU CLASE AddVentaController ---

    @FXML
    private void handleAddProducto() {
        try {
            // 1. Pedir al usuario que elija un producto
            Producto producto = pedirProducto();
            if (producto == null) {
                return; // El usuario canceló
            }

            // 2. Pedir la cantidad
            TextInputDialog cantidadDialog = new TextInputDialog("1");
            cantidadDialog.setTitle("Añadir Producto");
            cantidadDialog.setHeaderText("Introduce la cantidad para: " + producto.getNombre());
            cantidadDialog.setContentText("Cantidad:");

            Optional<String> cantidadResult = cantidadDialog.showAndWait();
            if (cantidadResult.isEmpty() || cantidadResult.get().isBlank()) {
                return; // El usuario canceló o dejó el campo vacío
            }

            // 3. Pedir el descuento
            TextInputDialog descuentoDialog = new TextInputDialog("0.0");
            descuentoDialog.setTitle("Añadir Producto");
            descuentoDialog.setHeaderText("Introduce el descuento (%) para: " + producto.getNombre());
            descuentoDialog.setContentText("Descuento (%):");

            Optional<String> descuentoResult = descuentoDialog.showAndWait();
            if (descuentoResult.isEmpty() || descuentoResult.get().isBlank()) {
                return; // El usuario canceló o dejó el campo vacío
            }

            // --- VALIDACIÓN CON REGEX ---
            String cantidadStr = cantidadResult.get();
            String descuentoStr = descuentoResult.get().replace(',', '.');

            // 4. Validar el formato del descuento con DECIMAL_REGEX
            if (!ReggexUtil.DECIMAL_REGEX.matcher(descuentoStr).matches()) {
                new Alert(Alert.AlertType.WARNING, "El formato del descuento no es válido (ej: 12.99).").showAndWait();
                return;
            }

            // 5. Convertir a número (ahora es seguro)
            int cantidad = Integer.parseInt(cantidadStr);
            double descuento = Double.parseDouble(descuentoStr);

            // 6. Validar los valores
            if (cantidad <= 0 || descuento < 0 || descuento > 100) {
                new Alert(Alert.AlertType.WARNING, "La cantidad debe ser mayor que 0 y el descuento debe estar entre 0 y 100.").showAndWait();
                return;
            }

            // 7. Calcular precios y añadir a la tabla
            double precioUnitario = producto.getPrecio();
            double iva = 21.0;
            double precioConDescuento = precioUnitario * (1 - descuento / 100);
            double subtotal = (precioConDescuento * cantidad) * (1 + iva / 100);

            Detalle_Venta dv = new Detalle_Venta(0, null, producto, cantidad, descuento, precioUnitario, iva, subtotal);
            detallesList.add(dv);
            actualizarTotal();

        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "La cantidad debe ser un número entero válido.").showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Ocurrió un error al añadir el producto.").showAndWait();
        }
    }

// --- REEMPLAZA ESTE MÉTODO EN TU CLASE AddVentaController ---

    // --- REEMPLAZA ESTE MÉTODO EN TU CLASE AddVentaController.java ---

    private Producto pedirProducto() throws SQLException {
        List<Producto> productosDisponibles = productoDAO.getAll();
        if (productosDisponibles.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "No hay productos disponibles para añadir.").showAndWait();
            return null;
        }

        // El ChoiceDialog ahora usará automáticamente el método toString() del Producto
        ChoiceDialog<Producto> dialog = new ChoiceDialog<>(productosDisponibles.get(0), productosDisponibles);
        dialog.setTitle("Seleccionar Producto");
        dialog.setHeaderText("Elige un producto de la lista");
        dialog.setContentText("Producto:");

        return dialog.showAndWait().orElse(null);
    }


    // --- REEMPLAZA ESTOS DOS MÉTODOS EN AddVentaController.java ---

    /**
     * Actualiza el campo de texto del total.
     * VERSIÓN CORREGIDA: Usa Locale.US para asegurar el punto decimal.
     */
    private void actualizarTotal() {
        double total = detallesList.stream()
                .mapToDouble(Detalle_Venta::getSubtotal)
                .sum();
        totalField.setText(String.format(Locale.US, "%.2f", total));
    }

    @FXML
    private void handleSave() {
        try {
            if (fechaPicker.getValue() == null || clienteCombo.getValue() == null) {
                new Alert(Alert.AlertType.WARNING, "Debe seleccionar fecha y cliente.").showAndWait();
                return;
            }

            if (ventaAEditar != null) {
                // MODO EDICIÓN
                ventaAEditar.setFecha(java.sql.Date.valueOf(fechaPicker.getValue()));
                ventaAEditar.setCliente(clienteCombo.getValue());
                String totalText = totalField.getText().replace(',', '.');
                ventaAEditar.setTotal(Double.parseDouble(totalText));

                // 1. Actualizamos la venta principal
                ventaDAO.update(ventaAEditar);

                // 2. Borramos todos los detalles antiguos
                detalleDAO.deleteByVentaId(ventaAEditar.getId_venta());

                // 3. Re-insertamos todos los detalles de la tabla
                for (Detalle_Venta dv : detallesList) {
                    dv.setVenta(ventaAEditar); // Nos aseguramos de que tienen el ID de venta correcto
                    detalleDAO.add(dv);
                }
                LOGGER.info("Venta ID " + ventaAEditar.getId_venta() + " y sus detalles han sido actualizados.");

            } else {
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

                for (Detalle_Venta dv : detallesList) {
                    dv.setVenta(ventaGuardada);
                    detalleDAO.add(dv);
                }
                LOGGER.info("Nueva venta guardada con ID: " + ventaGuardada.getId_venta());
            }

            guardado = true;
            dialogStage.close();

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Ocurrió un error al guardar la venta.\n" + e.getMessage()).showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
}
