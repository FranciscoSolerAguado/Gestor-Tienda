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

// Controlador para la ventana de añadir o editar clientes.
public class AddClienteController {

    private static final Logger LOGGER = LoggerUtil.getLogger();

    // --- Elementos de la interfaz (FXML) ---
    @FXML
    private TextField nombreField;
    @FXML
    private TextField telefonoField;
    @FXML
    private TextArea direccionArea;
    @FXML
    private Button saveButton;


    private Stage dialogStage; // Referencia a la propia ventana.
    private boolean guardado = false;
    private Cliente clienteAEditar = null; // Aquí guardamos el cliente si estamos editando uno existente.

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
     * Método que prepara los datos de cliente que se quiere editar
     * @param cliente El cliente que recibimos de fuera y queremos editar
     */
    public void setClienteParaEditar(Cliente cliente) {
        this.clienteAEditar = cliente;
        nombreField.setText(cliente.getNombre());
        telefonoField.setText(cliente.getTelefono());
        direccionArea.setText(cliente.getDireccion());
        LOGGER.info("Diálogo de cliente puesto en modo edición para el cliente ID: " + cliente.getId_cliente());
    }

    /**
     * Acción del botón Guardar.
     */
    @FXML
    private void handleSave() {
        // lo que ha escrito el usuario.
        String nombre = nombreField.getText().trim();
        String telefono = telefonoField.getText().trim();
        String direccion = direccionArea.getText().trim();

        // --- Validación de datos ---
        String errorMessage = "";

        if (!ReggexUtil.NOMBRE_REGEX.matcher(nombre).matches()) {
            errorMessage += "El nombre no es válido (no puede estar vacío).\n";
        }
        // El teléfono solo se valida si escribieron algo (si es opcional).
        if (!telefono.isEmpty() && !ReggexUtil.TELEFONO_REGEX.matcher(telefono).matches()) {
            errorMessage += "El teléfono no es válido (debe tener 9 dígitos y empezar por 6, 7, 8 o 9).\n";
        }

        // Si hemos encontrado fallos, mostramos un aviso y no seguimos.
        if (!errorMessage.isEmpty()) {
            LOGGER.warning("Falló la validación al guardar cliente: " + errorMessage.replace("\n", " "));

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Datos Inválidos");
            alert.setHeaderText("Por favor, corrige los campos marcados.");
            alert.setContentText(errorMessage);
            alert.showAndWait();

            return; // Cortamos aquí la ejecución.
        }

        // Si todo está correcto, procedemos.
        if (clienteAEditar != null) {
            // Estábamos editando: actualizamos los datos del objeto existente.
            clienteAEditar.setNombre(nombre);
            clienteAEditar.setTelefono(telefono);
            clienteAEditar.setDireccion(direccion);
            LOGGER.info("Preparando para actualizar cliente ID: " + clienteAEditar.getId_cliente());
        } else {
            clienteAEditar = new Cliente(nombre, telefono, direccion);
            LOGGER.info("Preparando para crear nuevo cliente con nombre: " + nombre);
        }

        // Marcamos como guardado y cerramos la ventana.
        guardado = true;
        dialogStage.close();
    }

    /**
     * Acción del botón Cancelar.
     */
    @FXML
    private void handleCancel() {
        LOGGER.info("Operación de añadir/editar cliente cancelada por el usuario.");
        dialogStage.close();
    }

    /**
     * Devuelve el objeto cliente con los datos listos.
     * Ya sea el nuevo que creamos o el que editamos.
     */
    public Cliente getNuevoCliente() {
        return clienteAEditar;
    }
}