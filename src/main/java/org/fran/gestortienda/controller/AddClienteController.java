package org.fran.gestortienda.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.fran.gestortienda.model.entity.Cliente;
import org.fran.gestortienda.utils.ReggexUtil;

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

    // --- AÑADE ESTE CAMPO A TU CLASE AddClienteController ---
    private Cliente clienteAEditar = null;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isGuardado() {
        return guardado;
    }

    // --- REEMPLAZA TU MÉTODO getNuevoCliente CON ESTE ---

    public Cliente getNuevoCliente() {
        String nombre = nombreField.getText().trim();
        String telefono = telefonoField.getText().trim();
        String direccion = direccionArea.getText().trim();

        String errorMessage = "";

        if (!ReggexUtil.NOMBRE_REGEX.matcher(nombre).matches()) {
            errorMessage += "El nombre no es válido (no puede estar vacío).\n";
        }
        // El teléfono es opcional, pero si se introduce, debe ser válido
        if (!telefono.isEmpty() && !ReggexUtil.TELEFONO_REGEX.matcher(telefono).matches()) {
            errorMessage += "El teléfono no es válido (debe tener 9 dígitos y empezar por 6, 7, 8 o 9).\n";
        }

        if (!errorMessage.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Datos Inválidos");
            alert.setHeaderText("Por favor, corrige los campos marcados.");
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return null;
        }


        if (clienteAEditar != null) {
            clienteAEditar.setNombre(nombre);
            clienteAEditar.setTelefono(telefono);
            clienteAEditar.setDireccion(direccion);
            return clienteAEditar;
        } else {
            return new Cliente(nombre, telefono, direccion);
        }
    }


    /**
     * Pone el controlador en "modo edición" y rellena el formulario
     * con los datos de un cliente existente.
     * @param cliente El cliente a editar.
     */
    public void setClienteParaEditar(Cliente cliente) {
        this.clienteAEditar = cliente;

        // Rellenar los campos del formulario
        nombreField.setText(cliente.getNombre());
        telefonoField.setText(cliente.getTelefono());
        direccionArea.setText(cliente.getDireccion());
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
