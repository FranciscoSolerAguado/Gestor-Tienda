package org.fran.gestortienda.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.fran.gestortienda.DAO.ProductoDAO;
import org.fran.gestortienda.DAO.ProveedorDAO;
import org.fran.gestortienda.model.Categoria;
import org.fran.gestortienda.model.entity.Producto;
import org.fran.gestortienda.model.entity.Proveedor;
import org.fran.gestortienda.utils.LoggerUtil;
import org.fran.gestortienda.utils.ReggexUtil;
import org.fran.gestortienda.utils.Utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.logging.Logger;

public class AddProductoController {

    private static final Logger LOGGER = LoggerUtil.getLogger();

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
            LOGGER.severe("Error de SQL al cargar proveedores: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setProductoParaEditar(Producto producto) {
        this.productoAEditar = producto;

        nombreField.setText(producto.getNombre());
        categoriaCombo.setValue(producto.getCategoria());
        precioField.setText(String.format("%.2f", producto.getPrecio()).replace(',', '.'));
        stockField.setText(String.valueOf(producto.getStock()));
        proveedorCombo.setValue(producto.getProveedor());

        if (producto.getImagen() != null && !producto.getImagen().isEmpty()) {
            try {
                String imagePath = "/org/fran/gestortienda/img/productos/" + producto.getImagen();
                Image img = new Image(getClass().getResourceAsStream(imagePath));
                if (img.isError()) {
                    LOGGER.warning("No se pudo cargar la imagen existente desde la ruta: " + imagePath);
                } else {
                    previewImagen.setImage(img);
                }
            } catch (Exception e) {
                LOGGER.severe("Excepción al cargar la imagen del producto a editar: " + e.getMessage());
            }
        }
        LOGGER.info("Diálogo de producto puesto en modo edición para el producto ID: " + producto.getId_producto());
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
            LOGGER.info("Imagen seleccionada por el usuario: " + imagenSeleccionada.getAbsolutePath());
        }
    }

    @FXML
    private void handleSave() {
        String errorMessage = validarEntrada();
        if (!errorMessage.isEmpty()) {
            LOGGER.warning("Falló la validación al guardar producto: " + errorMessage.replace("\n", " "));
            utils.mostrarAlerta("Campos Inválidos");
            return;
        }

        try {
            String imageNameToSave = null;
            if (imagenSeleccionada != null) {
                Path destDir = Paths.get("src/main/resources/org/fran/gestortienda/img/productos/");
                if (!Files.exists(destDir)) {
                    Files.createDirectories(destDir);
                }
                Path destPath = destDir.resolve(imagenSeleccionada.getName());
                Files.copy(imagenSeleccionada.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);
                imageNameToSave = imagenSeleccionada.getName();
                LOGGER.info("Imagen copiada a: " + destPath);
            }

            if (productoAEditar != null) {
                productoAEditar.setNombre(nombreField.getText().trim());
                productoAEditar.setCategoria(categoriaCombo.getValue());
                productoAEditar.setPrecio(Double.parseDouble(precioField.getText().replace(',', '.')));
                productoAEditar.setStock(Integer.parseInt(stockField.getText()));
                productoAEditar.setProveedor(proveedorCombo.getValue());
                if (imageNameToSave != null) {
                    productoAEditar.setImagen(imageNameToSave);
                }
                productoDAO.update(productoAEditar);
                LOGGER.info("Producto ID " + productoAEditar.getId_producto() + " actualizado correctamente.");
            } else {
                Producto nuevoProducto = new Producto(
                        nombreField.getText().trim(),
                        categoriaCombo.getValue(),
                        Double.parseDouble(precioField.getText().replace(',', '.')),
                        Integer.parseInt(stockField.getText()),
                        proveedorCombo.getValue(),
                        imageNameToSave
                );
                productoDAO.add(nuevoProducto);
                LOGGER.info("Nuevo producto '" + nuevoProducto.getNombre() + "' creado correctamente.");
            }

            guardado = true;
            dialogStage.close();

        } catch (Exception e) {
            LOGGER.severe("Error al guardar el producto en la base de datos: " + e.getMessage());
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
        LOGGER.info("Operación de añadir/editar producto cancelada.");
        dialogStage.close();
    }
}