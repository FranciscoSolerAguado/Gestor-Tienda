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

// Controlador para la ventana de editar o añadir ventas
public class AddVentaController {

    private static final Logger LOGGER = LoggerUtil.getLogger();

    // --- Elementos de la interfaz (FXML) ---
    @FXML private DatePicker fechaPicker;
    @FXML private ComboBox<Cliente> clienteCombo;
    @FXML private TextField totalField;

    // Tabla de líneas de venta (el carrito)
    @FXML private TableView<Detalle_Venta> tablaDetalles;
    @FXML private TableColumn<Detalle_Venta, String> colProducto;
    @FXML private TableColumn<Detalle_Venta, Integer> colCantidad;
    @FXML private TableColumn<Detalle_Venta, Double> colPrecio;
    @FXML private TableColumn<Detalle_Venta, Double> colDescuento;
    @FXML private TableColumn<Detalle_Venta, Double> colIVA;
    @FXML private TableColumn<Detalle_Venta, Double> colSubtotal;

    // Lista observable para que la tabla se refresque sola cuando añadimos cosas.
    private ObservableList<Detalle_Venta> detallesList = FXCollections.observableArrayList();


    private Stage dialogStage;
    private boolean guardado = false;
    private Venta ventaAEditar = null;

    // --- conexión con la BD ---
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ProductoDAO productoDAO = new ProductoDAO();
    private final VentaDAO ventaDAO = new VentaDAO();
    private final Detalle_VentaDAO detalleDAO = new Detalle_VentaDAO();

    /**
     * Método que establece la ventana de diálogo
     * @param dialogStage La ventana de diálogo que queremos establecer
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /**
     * Método que confirma si se ha guardado
     * @return True si se ha guardado, false si no
     */
    public boolean isGuardado() {
        return guardado;
    }

    /**
     * Método que prepara los datos de la venta que se quiere editar
     * @param venta La venta que recibimos de fuera y queremos editar
     */
    public void setVentaParaEditar(Venta venta) {
        this.ventaAEditar = venta;

        // Convertimos la fecha de SQL a la que entiende el DatePicker.
        if (venta.getFecha() != null) {
            java.util.Date date = new java.util.Date(venta.getFecha().getTime());
            LocalDate localDate = date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            fechaPicker.setValue(localDate);
        }

        if (venta.getCliente() != null) {
            clienteCombo.setValue(venta.getCliente());
        }
        // Mostramos el total formateado (punto para decimales).
        totalField.setText(String.format(java.util.Locale.US, "%.2f", venta.getTotal()));

        // Cargamos los productos que tenía esta venta.
        try {
            detallesList.setAll(new Detalle_VentaDAO().getByVenta(venta.getId_venta()));
            tablaDetalles.setItems(detallesList);
        } catch (SQLException e) {
            LOGGER.severe("Error de SQL al cargar los detalles de la venta a editar: " + e.getMessage());
            e.printStackTrace();
        }
        LOGGER.info("Diálogo de venta puesto en modo edición para la venta ID: " + venta.getId_venta());
    }

    // --- Inicialización ---

    /**
     * Método inicializador de la ventana.
     * Configuramos la tabla y cargamos los clientes
     */
    @FXML
    private void initialize() {
        cargarClientes();
        configurarTabla();
        totalField.setText("0.00");
    }

    /**
     * Método que carga los clientes en el ComboBox
     */
    private void cargarClientes() {
        try {
            clienteCombo.setItems(FXCollections.observableArrayList(clienteDAO.getAll()));
        } catch (SQLException e) {
            LOGGER.severe("Error de SQL al cargar clientes para el ComboBox: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Método que configura la tabla de detalles
     */
    private void configurarTabla() {
        colProducto.setCellValueFactory(cellData -> {
            Producto producto = cellData.getValue().getProducto();
            if (producto != null) {
                return new javafx.beans.property.SimpleStringProperty(producto.getNombre());
            } else {
                return new javafx.beans.property.SimpleStringProperty("<Producto no encontrado>");
            }
        });

        // Mapeo directo de propiedades numéricas.
        colCantidad.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getCantidad()));
        colPrecio.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getPrecio_unitario()));
        colDescuento.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getDescuento()));
        colIVA.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getIva()));
        colSubtotal.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getSubtotal()));

        tablaDetalles.setItems(detallesList);
    }

    // --- Botón: AÑADIR PRODUCTO ---

    /**
     * Acción del botón Añadir Producto.
     */
    @FXML
    private void handleAddProducto() {
        try {
            // Pedir al usuario que elija un producto de la lista.
            Producto producto = pedirProducto();
            if (producto == null) {
                return; // Si cancela o no hay productos.
            }

            // --- CONTROL DE STOCK ---
            // Si no queda stock.
            if (producto.getStock() <= 0) {
                LOGGER.warning("Intento de añadir producto sin stock: " + producto.getNombre());
                new Alert(Alert.AlertType.WARNING, "El producto '" + producto.getNombre() + "' está agotado y no se puede añadir a la venta.").showAndWait();
                return;
            }

            // Cuanta cantidad de ese producto se va a añadir
            TextInputDialog cantidadDialog = new TextInputDialog("1");
            cantidadDialog.setTitle("Añadir Producto");
            cantidadDialog.setHeaderText("Introduce la cantidad para: " + producto.getNombre() + "\nStock disponible: " + producto.getStock());
            cantidadDialog.setContentText("Cantidad:");

            Optional<String> cantidadResult = cantidadDialog.showAndWait();
            if (cantidadResult.isEmpty() || cantidadResult.get().isBlank()) {
                return; // Si cancela.
            }

            //Pedir si lleva descuento.
            TextInputDialog descuentoDialog = new TextInputDialog("0.0");
            descuentoDialog.setTitle("Añadir Producto");
            descuentoDialog.setHeaderText("Introduce el descuento (%) para: " + producto.getNombre());
            descuentoDialog.setContentText("Descuento (%):");

            Optional<String> descuentoResult = descuentoDialog.showAndWait();
            if (descuentoResult.isEmpty() || descuentoResult.get().isBlank()) {
                return; // Si cancela.
            }

            // Validar números y formatos.
            String cantidadStr = cantidadResult.get();
            String descuentoStr = descuentoResult.get().replace(',', '.');

            if (!ReggexUtil.DECIMAL_REGEX.matcher(descuentoStr).matches()) {
                new Alert(Alert.AlertType.WARNING, "El formato del descuento no es válido (ej: 12.99).").showAndWait();
                return;
            }

            int cantidad = Integer.parseInt(cantidadStr);
            double descuento = Double.parseDouble(descuentoStr);

            // --- CONTROL DE STOCK  ---
            // Comprobamos si está pidiendo más de lo que hay.
            if (cantidad > producto.getStock()) {
                LOGGER.warning(String.format("Intento de vender %d unidades de '%s', pero solo hay %d en stock.", cantidad, producto.getNombre(), producto.getStock()));
                new Alert(Alert.AlertType.WARNING, "No puedes vender " + cantidad + " unidades. Solo hay " + producto.getStock() + " disponibles en stock.").showAndWait();
                return;
            }

            // Validaciones
            if (cantidad <= 0 || descuento < 0 || descuento > 100) {
                new Alert(Alert.AlertType.WARNING, "La cantidad debe ser mayor que 0 y el descuento debe estar entre 0 y 100.").showAndWait();
                return;
            }

            // Calculo de precio, IVA y total de la línea.
            double precioUnitario = producto.getPrecio();
            double iva = 21.0; // IVA fijo
            double precioConDescuento = precioUnitario * (1 - descuento / 100);
            double subtotal = (precioConDescuento * cantidad) * (1 + iva / 100);

            // Creamos el objeto detalle y lo metemos a la tabla.
            Detalle_Venta dv = new Detalle_Venta(0, null, producto, cantidad, descuento, precioUnitario, iva, subtotal);
            detallesList.add(dv);

            // Recalculamos el total de la factura.
            actualizarTotal();

        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "La cantidad debe ser un número entero válido.").showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Ocurrió un error al añadir el producto.").showAndWait();
        }
    }

    /**
     * Método que se encarga de la acción de pedir un producto, mostrando los productos que el usuario puede añadir
     * @throws SQLException
     */
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

    // Suma todos los subtotales de la tabla y actualiza el campo Total.
    private void actualizarTotal() {
        double total = detallesList.stream()
                .mapToDouble(Detalle_Venta::getSubtotal)
                .sum();
        totalField.setText(String.format(Locale.US, "%.2f", total));
    }

    // --- Botón: GUARDAR VENTA ---

    /**
     * Acción del botón Guardar.
     */
    @FXML
    private void handleSave() {
        // Que no haya campos vacios o sin seleccionar
        try {
            if (fechaPicker.getValue() == null || clienteCombo.getValue() == null) {
                new Alert(Alert.AlertType.WARNING, "Debe seleccionar fecha y cliente.").showAndWait();
                return;
            }

            if (ventaAEditar != null) {
                ventaAEditar.setFecha(java.sql.Date.valueOf(fechaPicker.getValue()));
                ventaAEditar.setCliente(clienteCombo.getValue());
                String totalText = totalField.getText().replace(',', '.');
                ventaAEditar.setTotal(Double.parseDouble(totalText));
                ventaDAO.update(ventaAEditar);
                // Actualizamos los detalles.
                detalleDAO.deleteByVentaId(ventaAEditar.getId_venta());
                for (Detalle_Venta dv : detallesList) {
                    dv.setVenta(ventaAEditar);
                    detalleDAO.add(dv);
                }
                LOGGER.info("Venta ID " + ventaAEditar.getId_venta() + " y sus detalles han sido actualizados.");

            } else {
                // --- MODO CREACIÓN (Venta Nueva) ---
                Venta nuevaVenta = new Venta();
                nuevaVenta.setFecha(java.sql.Date.valueOf(fechaPicker.getValue()));
                Cliente clienteSeleccionado = clienteCombo.getValue();
                nuevaVenta.setCliente(clienteSeleccionado);
                String totalText = totalField.getText().replace(',', '.');
                nuevaVenta.setTotal(Double.parseDouble(totalText));

                // Guardamos la cabecera de la venta.
                boolean guardadoConExito = ventaDAO.add(nuevaVenta);
                if (!guardadoConExito) {
                    throw new SQLException("No se pudo guardar la venta principal.");
                }

                // Recuperamos la venta recién creada para saber su ID.
                Venta ventaGuardada = ventaDAO.getLastByCliente(clienteSeleccionado.getId_cliente());
                if (ventaGuardada == null) {
                    throw new SQLException("No se pudo recuperar la venta recién guardada.");
                }
                ventaGuardada.setCliente(clienteSeleccionado);

                // Guardamos los detalles y restamos el stock
                for (Detalle_Venta dv : detallesList) {
                    // Guardamos la línea de venta en BD.
                    dv.setVenta(ventaGuardada);
                    detalleDAO.add(dv);

                    // Actualizamos el inventario.
                    Producto productoVendido = dv.getProducto();
                    int cantidadVendida = dv.getCantidad();

                    // Calculamos stock restante.
                    int nuevoStock = productoVendido.getStock() - cantidadVendida;
                    productoVendido.setStock(nuevoStock);

                    // Guardamos el nuevo stock en la BD.
                    productoDAO.update(productoVendido);

                    LOGGER.info(String.format("Stock actualizado para producto ID %d: %d -> %d",
                            productoVendido.getId_producto(),
                            productoVendido.getStock() + cantidadVendida, // Stock original
                            nuevoStock));
                }
                LOGGER.info("Nueva venta guardada con ID: " + ventaGuardada.getId_venta());
            }

            // Si sale bien
            guardado = true;
            dialogStage.close();

        } catch (Exception e) {
            LOGGER.severe("Error al guardar la venta: " + e.getMessage());
            new Alert(Alert.AlertType.ERROR, "Ocurrió un error al guardar la venta.\n" + e.getMessage()).showAndWait();
            e.printStackTrace();
        }
    }

    /**
     * Acción del botón Cancelar.
     */
    @FXML
    private void handleCancel() {
        LOGGER.info("Operación de añadir/editar venta cancelada.");
        dialogStage.close();
    }
}