package org.fran.gestortienda.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MainController {

    @FXML private Button btnVentas;
    @FXML private Button btnClientes;
    @FXML private Button btnProveedores;
    @FXML private Button btnProductos;

    @FXML
    public void initialize() {
        // Acción de ejemplo: abrir consola. Sustituye por la navegación real.
        btnVentas.setOnAction(e -> System.out.println("Abrir Ventas"));
        btnClientes.setOnAction(e -> System.out.println("Abrir Clientes"));
        btnProveedores.setOnAction(e -> System.out.println("Abrir Proveedores"));
        btnProductos.setOnAction(e -> System.out.println("Abrir Productos"));
    }
}
