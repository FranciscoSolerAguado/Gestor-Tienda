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

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;

public class AddProductoController {

    @FXML private TextField nombreField;
    @FXML private ComboBox<Categoria> categoriaCombo;
    @FXML private TextField precioField;
    @FXML private TextField stockField;
    @FXML private ComboBox<Proveedor> proveedorCombo;
    @FXML private ImageView previewImagen;

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
        try {
            String nombre = nombreField.getText();
            // ... (resto de la validación se queda igual)

            // Si productoAEditar no es null, estamos en modo edición
            if (productoAEditar != null) {
                // Actualizamos el objeto existente
                productoAEditar.setNombre(nombre);
                productoAEditar.setCategoria(categoriaCombo.getValue());
                productoAEditar.setPrecio(Double.parseDouble(precioField.getText().replace(",", ".")));
                productoAEditar.setStock(Integer.parseInt(stockField.getText()));
                productoAEditar.setProveedor(proveedorCombo.getValue());

                if (imagenSeleccionada != null) {
                    // ... (lógica para copiar la nueva imagen)
                    productoAEditar.setImagen(imagenSeleccionada.getName());
                }

                productoDAO.update(productoAEditar); // Llamamos a UPDATE

            } else {
                // Si no, estamos en modo creación (lógica que ya tenías)
                Producto nuevoProducto = new Producto();
                // ... (código para crear un nuevo producto)
                productoDAO.add(nuevoProducto);
            }

            guardado = true;
            dialogStage.close();

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error al guardar el producto").showAndWait();
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
}


