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

// Controlador para gestionar el alta o edición de productos.
public class AddProductoController {

    private static final Logger LOGGER = LoggerUtil.getLogger();

    // --- Elementos de la interfaz (FXML) ---
    @FXML private TextField nombreField;
    @FXML private ComboBox<Categoria> categoriaCombo;
    @FXML private TextField precioField;
    @FXML private TextField stockField;
    @FXML private ComboBox<Proveedor> proveedorCombo;
    @FXML private ImageView previewImagen; // Para mostrar la foto elegida.

    Utils utils = new Utils();
    private File imagenSeleccionada = null; // Aquí guardamos el archivo si el usuario elige foto nueva.
    private Stage dialogStage;
    private boolean guardado = false;
    private Producto productoAEditar = null;

    // --- DAOs: Los que hacen el trabajo sucio con la base de datos ---
    private final ProveedorDAO proveedorDAO = new ProveedorDAO();
    private final ProductoDAO productoDAO = new ProductoDAO();

    /**
     * Método que establece la ventana de diálogo
     * @param dialogStage La ventana de diálogo que queremos establecer
     */
    public void setDialogStage(Stage dialogStage) { this.dialogStage = dialogStage; }

    /**
     * Método que confirma si se ha guardado
     * @return True si se ha guardado, false si no
     */
    public boolean isGuardado() { return guardado; }

    // --- Inicialización ---
    // Esto se ejecuta nada más abrir la ventana.

    /**
     * Método inicializador de la ventana.
     */
    @FXML
    private void initialize() {
        // Cargamos todas las categorías del enum.
        categoriaCombo.getItems().addAll(Categoria.values());
        try {
            // Intentamos traer los proveedores de la BD.
            proveedorCombo.getItems().addAll(proveedorDAO.getAll());
        } catch (SQLException e) {
            LOGGER.severe("Error de SQL al cargar proveedores: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Método que prepara y carga los datos de el producto que se va a editar
     * @param producto El producto que recibimos de fuera y queremos editar
     */
    public void setProductoParaEditar(Producto producto) {
        this.productoAEditar = producto;

        // Ponemos los datos de texto y números.
        nombreField.setText(producto.getNombre());
        categoriaCombo.setValue(producto.getCategoria());
        // Formateamos el precio con 2 decimales y nos aseguramos de usar puntos.
        precioField.setText(String.format("%.2f", producto.getPrecio()).replace(',', '.'));
        stockField.setText(String.valueOf(producto.getStock()));
        proveedorCombo.setValue(producto.getProveedor());

        // --- Carga de imagen existente ---
        // Si el producto ya tenía foto, intentamos mostrarla.
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

    /**
     * Acción del botón Elegir Imagen.
     */
    @FXML
    private void handleElegirImagen() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Seleccionar imagen");
        // Filtramos para que solo nos dejen elegir imágenes y no un PDF o un Word.
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imágenes", "*.jpg", "*.png", "*.jpeg")
        );

        // Mostramos la ventana de selección.
        imagenSeleccionada = chooser.showOpenDialog(dialogStage);

        // Si eligió algo, actualizamos la previsualización.
        if (imagenSeleccionada != null) {
            previewImagen.setImage(new Image(imagenSeleccionada.toURI().toString()));
            LOGGER.info("Imagen seleccionada por el usuario: " + imagenSeleccionada.getAbsolutePath());
        }
    }

    /**
     * Acción del botón Guardar.
     */
    @FXML
    private void handleSave() {
        // Validacion de que esten vacios
        String errorMessage = validarEntrada();
        if (!errorMessage.isEmpty()) {
            LOGGER.warning("Falló la validación al guardar producto: " + errorMessage.replace("\n", " "));
            utils.mostrarAlerta("Campos Inválidos");
            return;
        }

        try {
            // --- Gestión de la imagen ---
            String imageNameToSave = null;
            // Si el usuario seleccionó una foto nueva, hay que guardarla en disco.
            if (imagenSeleccionada != null) {
                Path destDir = Paths.get("src/main/resources/org/fran/gestortienda/img/productos/");
                // Si la carpeta no existe, se crea
                if (!Files.exists(destDir)) {
                    Files.createDirectories(destDir);
                }
                // Copiamos el archivo. Si ya existe uno igual, lo borrams.
                Path destPath = destDir.resolve(imagenSeleccionada.getName());
                Files.copy(imagenSeleccionada.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);

                imageNameToSave = imagenSeleccionada.getName(); // Guardamos solo el nombre para la BD.
                LOGGER.info("Imagen copiada a: " + destPath);
            }

            // --- Guardado en Base de Datos ---
            if (productoAEditar != null) {
                // UPDATE: Actualizamos el objeto existente.
                productoAEditar.setNombre(nombreField.getText().trim());
                productoAEditar.setCategoria(categoriaCombo.getValue());
                productoAEditar.setPrecio(Double.parseDouble(precioField.getText().replace(',', '.')));
                productoAEditar.setStock(Integer.parseInt(stockField.getText()));
                productoAEditar.setProveedor(proveedorCombo.getValue());

                // Solo actualizamos la imagen si se eligió una nueva.
                if (imageNameToSave != null) {
                    productoAEditar.setImagen(imageNameToSave);
                }
                // Llamamos al DAO para hacer el UPDATE en SQL.
                productoDAO.update(productoAEditar);
                LOGGER.info("Producto ID " + productoAEditar.getId_producto() + " actualizado correctamente.");
            } else {
                // INSERT: Creamos un objeto nuevo desde cero.
                Producto nuevoProducto = new Producto(
                        nombreField.getText().trim(),
                        categoriaCombo.getValue(),
                        Double.parseDouble(precioField.getText().replace(',', '.')),
                        Integer.parseInt(stockField.getText()),
                        proveedorCombo.getValue(),
                        imageNameToSave
                );
                // Llamamos al DAO para hacer el INSERT en SQL.
                productoDAO.add(nuevoProducto);
                LOGGER.info("Nuevo producto '" + nuevoProducto.getNombre() + "' creado correctamente.");
            }

            // Si todo va bien
            guardado = true;
            dialogStage.close();

        } catch (Exception e) {
            LOGGER.severe("Error al guardar el producto en la base de datos: " + e.getMessage());
            e.printStackTrace();
            utils.mostrarAlerta("Ocurrió un error al guardar el producto en la base de datos.");
        }
    }

    /**
     * Metodo auxiliar para comprobar que los datos son correctos
     */
    private String validarEntrada() {
        StringBuilder errorMessage = new StringBuilder();

        // Validamos nombre con Regex.
        if (!ReggexUtil.NOMBRE_REGEX.matcher(nombreField.getText()).matches()) {
            errorMessage.append("El nombre no es válido.\n");
        }
        // Obligatorio elegir categoría y proveedor.
        if (categoriaCombo.getValue() == null) {
            errorMessage.append("Debe seleccionar una categoría.\n");
        }
        if (proveedorCombo.getValue() == null) {
            errorMessage.append("Debe seleccionar un proveedor.\n");
        }

        // El precio debe ser decimal (ej: 10.50). Aceptamos coma cambiándola por punto.
        String precioStr = precioField.getText().replace(',', '.');
        if (!ReggexUtil.DECIMAL_REGEX.matcher(precioStr).matches()) {
            errorMessage.append("El formato del precio no es válido (ej: 12.99).\n");
        }

        // El stock debe ser un número entero, nada de decimales.
        try {
            Integer.parseInt(stockField.getText());
        } catch (NumberFormatException e) {
            errorMessage.append("El stock debe ser un número entero.\n");
        }

        return errorMessage.toString();
    }

    /**
     * Acción del botón Cancelar.
     * No guardamos nada si clicamos aqui
     */
    @FXML
    private void handleCancel() {
        LOGGER.info("Operación de añadir/editar producto cancelada.");
        dialogStage.close();
    }
}