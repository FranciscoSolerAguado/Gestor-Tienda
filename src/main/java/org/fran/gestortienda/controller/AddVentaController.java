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

import java.sql.SQLException;

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

    private ObservableList<Detalle_Venta> detallesList = FXCollections.observableArrayList();

    private Stage dialogStage;
    private boolean guardado = false;

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

    private void configurarTabla() {
        colProducto.setCellValueFactory(data -> javafx.beans.property.SimpleStringProperty.stringExpression(
                javafx.beans.binding.Bindings.createStringBinding(
                        () -> data.getValue().getProducto().getNombre()
                )
        ));

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
            // Se abre un diálogo simple para seleccionar producto y cantidad
            Producto producto = pedirProducto();
            if (producto == null) return;

            TextInputDialog cantidadDialog = new TextInputDialog("1");
            cantidadDialog.setHeaderText("Cantidad para " + producto.getNombre());
            int cantidad = Integer.parseInt(cantidadDialog.showAndWait().orElse("1"));

            double precio = producto.getPrecio();
            double descuento = 0.0;
            double iva = 21.0;
            double subtotal = (precio * cantidad) * (1 + iva / 100);

            Detalle_Venta dv = new Detalle_Venta(0, null, producto, cantidad, descuento, precio, iva, subtotal);

            detallesList.add(dv);
            actualizarTotal();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Producto pedirProducto() throws SQLException {
        ChoiceDialog<Producto> dialog = new ChoiceDialog<>();
        dialog.getItems().addAll(productoDAO.getAll());
        dialog.setHeaderText("Selecciona un producto");
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

        // Usamos Locale.US para que el separador decimal sea siempre un punto (.)
        totalField.setText(String.format(java.util.Locale.US, "%.2f", total));
    }

    // --- REEMPLAZA ESTE MÉTODO EN TU CLASE AddVentaController ---

    @FXML
    private void handleSave() {
        try {
            if (fechaPicker.getValue() == null || clienteCombo.getValue() == null) {
                new Alert(Alert.AlertType.WARNING, "Debe seleccionar fecha y cliente.").showAndWait();
                return;
            }

            Venta ventaSinId = new Venta();
            ventaSinId.setFecha(java.sql.Date.valueOf(fechaPicker.getValue()));
            ventaSinId.setCliente(clienteCombo.getValue());
            String totalText = totalField.getText().replace(',', '.');
            ventaSinId.setTotal(Double.parseDouble(totalText));

            // --- SOLUCIÓN AQUÍ ---
            // 1. Guardamos la venta y recuperamos el objeto con el ID
            Venta ventaGuardada = ventaDAO.addVenta(ventaSinId);

            if (ventaGuardada == null) {
                throw new SQLException("No se pudo guardar la venta principal y obtener su ID.");
            }

            // 2. Guardar detalles USANDO la venta con el ID correcto
            for (Detalle_Venta dv : detallesList) {
                dv.setVenta(ventaGuardada); // Asignamos la venta con su nuevo ID
                detalleDAO.add(dv);
            }
            // --- FIN DE LA SOLUCIÓN ---

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
