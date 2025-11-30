package org.fran.gestortienda.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.fran.gestortienda.model.entity.Cliente;
import org.fran.gestortienda.utils.LoggerUtil;
import org.fran.gestortienda.utils.ReggexUtil;

import java.util.logging.Logger;

public class AddClienteController {

    private static final Logger LOGGER = LoggerUtil.getLogger();

    @FXML
    private TextField nombreField;
    @FXML
    private TextField telefonoField;
    @FXML
    private TextArea direccionArea;
    @FXML
    private Button saveButton;

    private Stage dialogStage;
    private boolean guardado = false;
    private Cliente clienteAEditar = null;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isGuardado() {
        return guardado;
    }

    public void setClienteParaEditar(Cliente cliente) {
        this.clienteAEditar = cliente;
        nombreField.setText(cliente.getNombre());
        telefonoField.setText(cliente.getTelefono());
        direccionArea.setText(cliente.getDireccion());
        LOGGER.info("Diálogo de cliente puesto en modo edición para el cliente ID: " + cliente.getId_cliente());
    }

    @FXML
    private void handleSave() {
        String nombre = nombreField.getText().trim();
        String telefono = telefonoField.getText().trim();
        String direccion = direccionArea.getText().trim();

        // --- VALIDACIÓN CON REGEX Y LOGS ---
        String errorMessage = "";

        if (!ReggexUtil.NOMBRE_REGEX.matcher(nombre).matches()) {
            errorMessage += "El nombre no es válido (no puede estar vacío).\n";
        }
        if (!telefono.isEmpty() && !ReggexUtil.TELEFONO_REGEX.matcher(telefono).matches()) {
            errorMessage += "El teléfono no es válido (debe tener 9 dígitos y empezar por 6, 7, 8 o 9).\n";
        }

        if (!errorMessage.isEmpty()) {
            LOGGER.warning("Falló la validación al guardar cliente: " + errorMessage.replace("\n", " "));
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Datos Inválidos");
            alert.setHeaderText("Por favor, corrige los campos marcados.");
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return; // Detenemos el guardado
        }

        // Si la validación pasa, procedemos a crear o actualizar
        if (clienteAEditar != null) {
            // Modo Edición
            clienteAEditar.setNombre(nombre);
            clienteAEditar.setTelefono(telefono);
            clienteAEditar.setDireccion(direccion);
            LOGGER.info("Preparando para actualizar cliente ID: " + clienteAEditar.getId_cliente());
        } else {
            // Modo Creación
            clienteAEditar = new Cliente(nombre, telefono, direccion);
            LOGGER.info("Preparando para crear nuevo cliente con nombre: " + nombre);
        }

        guardado = true;
        dialogStage.close();
    }

    @FXML
    private void handleCancel() {
        LOGGER.info("Operación de añadir/editar cliente cancelada por el usuario.");
        dialogStage.close();
    }

    /**
     * Este método ahora devuelve el cliente que se ha preparado para guardar,
     * ya sea uno nuevo o uno actualizado.
     */
    public Cliente getNuevoCliente() {
        return clienteAEditar;
    }
}
