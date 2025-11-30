package org.fran.gestortienda.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.fran.gestortienda.model.entity.Proveedor;
import org.fran.gestortienda.utils.ReggexUtil;

public class AddProveedorController {

    @FXML
    private TextField nombreField;
    @FXML
    private TextField telefonoField;
    @FXML
    private TextField correoField;
    @FXML
    private Button saveButton;

    // --- AÑADE ESTE CAMPO A TU CLASE AddProveedorController ---
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

    // --- AÑADE ESTE MÉTODO A TU CLASE AddProveedorController ---

    /**
     * Pone el controlador en "modo edición" y rellena el formulario
     * con los datos de un proveedor existente.
     * @param proveedor El proveedor a editar.
     */
    public void setProveedorParaEditar(Proveedor proveedor) {
        this.proveedorAEditar = proveedor;

        // Rellenar los campos del formulario
        nombreField.setText(proveedor.getNombre());
        telefonoField.setText(proveedor.getTelefono());
        correoField.setText(proveedor.getCorreo());
    }

    // --- REEMPLAZA TU MÉTODO handleSave CON ESTE ---

    // --- REEMPLAZA TU MÉTODO handleSave EN AddProveedorController ---

    @FXML
    private void handleSave() {
        String nombre = nombreField.getText().trim();
        String telefono = telefonoField.getText().trim();
        String correo = correoField.getText().trim();

        // --- VALIDACIÓN CON REGEX ---
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
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Datos Inválidos");
            alert.setHeaderText("Por favor, corrige los campos marcados.");
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return; // La validación falla
        }
        // --- FIN DE LA VALIDACIÓN ---

        if (proveedorAEditar != null) {
            proveedorAEditar.setNombre(nombre);
            proveedorAEditar.setTelefono(telefono);
            proveedorAEditar.setCorreo(correo);
            nuevoProveedor = proveedorAEditar;
        } else {
            nuevoProveedor = new Proveedor(nombre, telefono, correo);
        }

        guardado = true;
        dialogStage.close();
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
}
