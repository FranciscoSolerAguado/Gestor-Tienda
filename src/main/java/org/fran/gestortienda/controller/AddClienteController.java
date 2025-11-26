package org.fran.gestortienda.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.fran.gestortienda.model.entity.Cliente;

public class AddClienteController {

    @FXML
    private TextField nombreField;
    @FXML
    private TextField telefonoField;
    @FXML
    private TextArea direccionArea;
    @FXML
    private Button saveButton;

    private Stage dialogStage;
    private Cliente nuevoCliente = null;
    private boolean guardado = false;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isGuardado() {
        return guardado;
    }

    public Cliente getNuevoCliente() {
        return nuevoCliente;
    }

    @FXML
    private void handleSave() {
        String nombre = nombreField.getText().trim();
        if (nombre.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Datos incompletos");
            alert.setHeaderText(null);
            alert.setContentText("El nombre del cliente no puede estar vacío.");
            alert.showAndWait();
            return;
        }

        nuevoCliente = new Cliente(
                nombre,
                telefonoField.getText().trim(),
                direccionArea.getText().trim()
        );
        guardado = true;
        dialogStage.close();
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
}
