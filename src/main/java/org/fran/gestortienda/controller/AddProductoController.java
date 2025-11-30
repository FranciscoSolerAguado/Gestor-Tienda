package org.fran.gestortienda.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.fran.gestortienda.DAO.ProductoDAO;
import org.fran.gestortienda.DAO.ProveedorDAO;
import org.fran.gestortienda.model.Categoria;
import org.fran.gestortienda.model.entity.Producto;
import org.fran.gestortienda.model.entity.Proveedor;
import org.fran.gestortienda.utils.ReggexUtil;
import org.fran.gestortienda.utils.Utils;

import java.io.File;
import java.sql.SQLException;

public class AddProductoController {

    @FXML private TextField nombreField;
    @FXML private ComboBox<Categoria> categoriaCombo;
    @FXML private TextField precioField;
    @FXML private TextField stockField;
    @FXML private ComboBox<Proveedor> proveedorCombo;
    @FXML private ImageView previewImagen;

    Utils utils = new Utils();

    private File imagenSeleccionada = null;

    private Stage dialogStage;
    private boolean guardado = false;

    private Producto productoAEditar = null;

    private final ProveedorDAO proveedorDAO = new ProveedorDAO();
    private final ProductoDAO productoDAO = new ProductoDAO();

    public void setDialogStage(Stage stage) { this.dialogStage = stage; }
    public boolean isGuardado() { return guardado; }

    @FXML
    private void initialize() {

        categoriaCombo.getItems().addAll(Categoria.values());

        try {
            proveedorCombo.getItems().addAll(proveedorDAO.getAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 2. NUEVO MÉTODO para poner el controlador en "modo edición".
     * Rellena el formulario con los datos del producto existente.
     */
    public void setProductoParaEditar(Producto producto) {
        this.productoAEditar = producto;

        nombreField.setText(producto.getNombre());
        categoriaCombo.setValue(producto.getCategoria());
        precioField.setText(String.format("%.2f", producto.getPrecio()).replace(',', '.'));
        stockField.setText(String.valueOf(producto.getStock()));
        proveedorCombo.setValue(producto.getProveedor());

        // Cargar imagen existente
        if (producto.getImagen() != null && !producto.getImagen().isEmpty()) {
            try {
                Image img = new Image(getClass().getResourceAsStream("/org/fran/gestortienda/img/productos/" + producto.getImagen()));
                previewImagen.setImage(img);
            } catch (Exception e) {
                // Si la imagen no se encuentra, no hacemos nada y dejamos el preview vacío
            }
        }
    }

    @FXML
    private void handleElegirImagen() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Seleccionar imagen");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imágenes", "*.jpg", "*.png", "*.jpeg")
        );

        imagenSeleccionada = chooser.showOpenDialog(dialogStage);

        if (imagenSeleccionada != null) {
            previewImagen.setImage(new Image(imagenSeleccionada.toURI().toString()));
        }
    }

    /**
     * 3. MÉTODO GUARDAR MODIFICADO
     * Ahora sabe si tiene que crear un producto nuevo o actualizar uno existente.
     */
    @FXML
    private void handleSave() {
        String errorMessage = validarEntrada();
        if (!errorMessage.isEmpty()) {
            utils.mostrarAlerta("Campos Inválidos");
            return; // Si hay errores, no continuamos
        }

        try {
            if (productoAEditar != null) {
                productoAEditar.setNombre(nombreField.getText().trim());
                productoAEditar.setCategoria(categoriaCombo.getValue());
                productoAEditar.setPrecio(Double.parseDouble(precioField.getText().replace(',', '.')));
                productoAEditar.setStock(Integer.parseInt(stockField.getText()));
                productoAEditar.setProveedor(proveedorCombo.getValue());

                if (imagenSeleccionada != null) {
                    productoAEditar.setImagen(imagenSeleccionada.getName());
                }

                productoDAO.update(productoAEditar);

            } else {
                // Modo Creación
                Producto nuevoProducto = new Producto(
                        nombreField.getText().trim(),
                        categoriaCombo.getValue(),
                        Double.parseDouble(precioField.getText().replace(',', '.')),
                        Integer.parseInt(stockField.getText()),
                        proveedorCombo.getValue(),
                        null // La imagen se gestiona después
                );

                if (imagenSeleccionada != null) {
                    // ... (lógica para copiar imagen)
                    nuevoProducto.setImagen(imagenSeleccionada.getName());
                }
                productoDAO.add(nuevoProducto);
            }

            guardado = true;
            dialogStage.close();

        } catch (Exception e) {
            e.printStackTrace();
            utils.mostrarAlerta("Ocurrió un error al guardar el producto en la base de datos.");
        }
    }

    private String validarEntrada() {
        StringBuilder errorMessage = new StringBuilder();

        if (!ReggexUtil.NOMBRE_REGEX.matcher(nombreField.getText()).matches()) {
            errorMessage.append("El nombre no es válido.\n");
        }
        if (categoriaCombo.getValue() == null) {
            errorMessage.append("Debe seleccionar una categoría.\n");
        }
        if (proveedorCombo.getValue() == null) {
            errorMessage.append("Debe seleccionar un proveedor.\n");
        }

        // ---Validación del precio ---
        String precioStr = precioField.getText().replace(',', '.');
        if (!ReggexUtil.DECIMAL_REGEX.matcher(precioStr).matches()) {
            errorMessage.append("El formato del precio no es válido (ej: 12.99).\n");
        }

        try {
            Integer.parseInt(stockField.getText());
        } catch (NumberFormatException e) {
            errorMessage.append("El stock debe ser un número entero.\n");
        }

        return errorMessage.toString();
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
}


