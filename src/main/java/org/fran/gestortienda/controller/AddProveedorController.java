package org.fran.gestortienda.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.fran.gestortienda.model.entity.Proveedor;

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

    @FXML
    private void handleSave() {
        String nombre = nombreField.getText().trim();
        if (nombre.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Datos incompletos");
            alert.setHeaderText(null);
            alert.setContentText("El nombre del proveedor no puede estar vacío.");
            alert.showAndWait();
            return;
        }

        // Si estamos en modo edición, actualizamos el objeto existente
        if (proveedorAEditar != null) {
            proveedorAEditar.setNombre(nombre);
            proveedorAEditar.setTelefono(telefonoField.getText().trim());
            proveedorAEditar.setCorreo(correoField.getText().trim());

            // Guardamos el objeto actualizado en una variable local
            nuevoProveedor = proveedorAEditar;

        } else {
            // Si no, creamos uno nuevo
            nuevoProveedor = new Proveedor(
                    nombre,
                    telefonoField.getText().trim(),
                    correoField.getText().trim()
            );
        }

        guardado = true;
        dialogStage.close();
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
}
