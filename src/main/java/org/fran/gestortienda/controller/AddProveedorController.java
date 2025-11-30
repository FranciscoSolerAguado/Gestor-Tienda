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

public class AddProveedorController {

    private static final Logger LOGGER = LoggerUtil.getLogger();

    @FXML
    private TextField nombreField;
    @FXML
    private TextField telefonoField;
    @FXML
    private TextField correoField;
    @FXML
    private Button saveButton;

    private Proveedor proveedorAEditar = null;
    private Stage dialogStage;
    private Proveedor nuevoProveedor = null;
    private boolean guardado = false;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isGuardado() {
        return guardado;
    }

    public Proveedor getNuevoProveedor() {
        return nuevoProveedor;
    }

    public void setProveedorParaEditar(Proveedor proveedor) {
        this.proveedorAEditar = proveedor;
        nombreField.setText(proveedor.getNombre());
        telefonoField.setText(proveedor.getTelefono());
        correoField.setText(proveedor.getCorreo());
        LOGGER.info("Diálogo de proveedor puesto en modo edición para el proveedor ID: " + proveedor.getId_proveedor());
    }

    @FXML
    private void handleSave() {
        String nombre = nombreField.getText().trim();
        String telefono = telefonoField.getText().trim();
        String correo = correoField.getText().trim();

        String errorMessage = "";

        if (!ReggexUtil.NOMBRE_REGEX.matcher(nombre).matches()) {
            errorMessage += "El nombre no es válido (no puede estar vacío).\n";
        }
        if (!ReggexUtil.TELEFONO_REGEX.matcher(telefono).matches()) {
            errorMessage += "El teléfono no es válido (debe tener 9 dígitos y empezar por 6, 7, 8 o 9).\n";
        }
        if (!ReggexUtil.GMAIL_REGEX.matcher(correo).matches()) {
            errorMessage += "El correo no es válido (debe ser una dirección de @gmail.com).\n";
        }

        if (!errorMessage.isEmpty()) {
            LOGGER.warning("Falló la validación al guardar proveedor: " + errorMessage.replace("\n", " "));
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Datos Inválidos");
            alert.setHeaderText("Por favor, corrige los campos marcados.");
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return;
        }

        if (proveedorAEditar != null) {
            proveedorAEditar.setNombre(nombre);
            proveedorAEditar.setTelefono(telefono);
            proveedorAEditar.setCorreo(correo);
            nuevoProveedor = proveedorAEditar;
            LOGGER.info("Preparando para actualizar proveedor ID: " + proveedorAEditar.getId_proveedor());
        } else {
            nuevoProveedor = new Proveedor(nombre, telefono, correo);
            LOGGER.info("Preparando para crear nuevo proveedor con nombre: " + nombre);
        }

        guardado = true;
        dialogStage.close();
    }

    @FXML
    private void handleCancel() {
        LOGGER.info("Operación de añadir/editar proveedor cancelada.");
        dialogStage.close();
    }
}
