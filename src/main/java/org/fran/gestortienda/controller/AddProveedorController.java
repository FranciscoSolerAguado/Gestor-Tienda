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

        nuevoProveedor = new Proveedor(
                nombre,
                telefonoField.getText().trim(),
                correoField.getText().trim()
        );
        guardado = true;
        dialogStage.close();
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
}
