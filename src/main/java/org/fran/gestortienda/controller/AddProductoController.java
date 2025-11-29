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

    @FXML
    private void handleSave() {
        try {
            String nombre = nombreField.getText();
            Categoria categoria = categoriaCombo.getValue();
            double precio = Double.parseDouble(precioField.getText().replace(",", "."));
            int stock = Integer.parseInt(stockField.getText());
            Proveedor proveedor = proveedorCombo.getValue();

            if (nombre.isEmpty() || categoria == null || proveedor == null) {
                new Alert(Alert.AlertType.WARNING, "Faltan campos obligatorios").showAndWait();
                return;
            }

            Producto producto = new Producto();
            producto.setNombre(nombre);
            producto.setCategoria(categoria);
            producto.setPrecio(precio);
            producto.setStock(stock);
            producto.setProveedor(proveedor);

            // Guardar imagen
            if (imagenSeleccionada != null) {
                File destino = new File("src/main/resources/org/fran/gestortienda/img/productos/" + imagenSeleccionada.getName());
                Files.copy(imagenSeleccionada.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
                producto.setImagen(imagenSeleccionada.getName());
            }

            productoDAO.add(producto);

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


