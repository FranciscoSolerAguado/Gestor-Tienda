package org.fran.gestortienda.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.fran.gestortienda.model.entity.Proveedor;
import org.fran.gestortienda.utils.LoggerUtil;
import org.fran.gestortienda.utils.ReggexUtil;

import java.util.logging.Logger;

// Controlador para la ventana de añadir o editar proveedores.
public class AddProveedorController {

    private static final Logger LOGGER = LoggerUtil.getLogger();

    // --- Elementos de la interfaz (FXML) ---
    @FXML
    private TextField nombreField;
    @FXML
    private TextField telefonoField;
    @FXML
    private TextField correoField;
    @FXML
    private Button saveButton;

    // --- Variables de control interno ---
    private Proveedor proveedorAEditar = null;
    private Stage dialogStage; // Referencia a la ventana.
    private Proveedor nuevoProveedor = null;
    private boolean guardado = false;

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
     * Método que devuelve el proveedor que hayamos editado o añadido
     * @return Los datos del proveedor que va a ser llamdao desde fuera
     */
    public Proveedor getNuevoProveedor() {
        return nuevoProveedor;
    }

    /**
     * Formulario para la edicion de un Proveedor
     * @param proveedor El proveedor que queremos editar
     */
    public void setProveedorParaEditar(Proveedor proveedor) {
        // Rellenamos los campos con los datos del proveedor que recibimos.
        this.proveedorAEditar = proveedor;
        nombreField.setText(proveedor.getNombre());
        telefonoField.setText(proveedor.getTelefono());
        correoField.setText(proveedor.getCorreo());
        LOGGER.info("Diálogo de proveedor puesto en modo edición para el proveedor ID: " + proveedor.getId_proveedor());
    }

    /**
     * Acción del botón Guardar.
     */
    @FXML
    private void handleSave() {
        // Recogemos lo que ha escrito el usuario, quitando espacios
        String nombre = nombreField.getText().trim();
        String telefono = telefonoField.getText().trim();
        String correo = correoField.getText().trim();

        // --- Validación de datos ---
        String errorMessage = "";

        // Validamos nombre, teléfono y que el correo sea de Gmail.
        if (!ReggexUtil.NOMBRE_REGEX.matcher(nombre).matches()) {
            errorMessage += "El nombre no es válido (no puede estar vacío).\n";
        }
        if (!ReggexUtil.TELEFONO_REGEX.matcher(telefono).matches()) {
            errorMessage += "El teléfono no es válido (debe tener 9 dígitos y empezar por 6, 7, 8 o 9).\n";
        }
        if (!ReggexUtil.GMAIL_REGEX.matcher(correo).matches()) {
            errorMessage += "El correo no es válido (debe ser una dirección de @gmail.com).\n";
        }

        // Si hay errores
        if (!errorMessage.isEmpty()) {
            LOGGER.warning("Falló la validación al guardar proveedor: " + errorMessage.replace("\n", " "));
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Datos Inválidos");
            alert.setHeaderText("Por favor, corrige los campos marcados.");
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return;
        }

        // Si todo está correcto, preparamos el objeto.
        if (proveedorAEditar != null) {
            //actualizamos los datos del objeto existente.
            proveedorAEditar.setNombre(nombre);
            proveedorAEditar.setTelefono(telefono);
            proveedorAEditar.setCorreo(correo);
            nuevoProveedor = proveedorAEditar;
            LOGGER.info("Preparando para actualizar proveedor ID: " + proveedorAEditar.getId_proveedor());
        } else {
            // generamos uno nuevo.
            nuevoProveedor = new Proveedor(nombre, telefono, correo);
            LOGGER.info("Preparando para crear nuevo proveedor con nombre: " + nombre);
        }

        // si todo sale bien
        guardado = true;
        dialogStage.close();
    }

    /**
     * Acción del botón Cancelar.
     */
    @FXML
    private void handleCancel() {
        LOGGER.info("Operación de añadir/editar proveedor cancelada.");
        dialogStage.close();
    }
}
