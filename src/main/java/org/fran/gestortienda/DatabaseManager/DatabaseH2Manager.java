package org.fran.gestortienda.DatabaseManager;

import org.fran.gestortienda.Connection.H2Connection;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseH2Manager {
    public static void crearTablas() {
        String sql = """
                                    CREATE TABLE IF NOT EXISTS cliente (
                                        id_cliente INT AUTO_INCREMENT PRIMARY KEY,
                                        nombre VARCHAR(50),
                                        telefono VARCHAR(20),
                                        direccion VARCHAR(100)
                                    );
                
                                    CREATE TABLE IF NOT EXISTS proveedor (
                                        id_proveedor INT AUTO_INCREMENT PRIMARY KEY,
                                        nombre VARCHAR(50),
                                        telefono VARCHAR(20),
                                        correo VARCHAR(100)
                                    );
                
                                    CREATE TABLE IF NOT EXISTS producto (
                                        id_producto INT AUTO_INCREMENT PRIMARY KEY,
                                        nombre VARCHAR(50),
                                        categoria VARCHAR(50),
                                        precio DECIMAL(10,2),
                                        stock INT,
                                        id_proveedor INT,
                                        imagen VARCHAR(255), -- <-- AÑADIR ESTA LÍNEA
                                        FOREIGN KEY (id_proveedor) REFERENCES proveedor(id_proveedor)
                                    );
                
                                    CREATE TABLE IF NOT EXISTS venta (
                                        id_venta INT AUTO_INCREMENT PRIMARY KEY,
                                        fecha DATE,
                                        total DECIMAL(10,2),
                                        id_cliente INT,
                                        FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente)
                                    );
                
                                    CREATE TABLE IF NOT EXISTS detalle_venta (
                                        id_detalle INT AUTO_INCREMENT PRIMARY KEY,
                                        id_venta INT,
                                        id_producto INT,
                                        cantidad INT,
                                        descuento DECIMAL(10,2),
                                        precio_unitario DECIMAL(10,2),
                                        iva DECIMAL(5,2),
                                        subtotal DECIMAL(10,2),
                                        FOREIGN KEY (id_venta) REFERENCES venta(id_venta),
                                        FOREIGN KEY (id_producto) REFERENCES producto(id_producto)
                                    );
                """;

        try (Connection conn = H2Connection.getConnection(); Statement st = conn.createStatement()) {

            String[] queries = sql.split(";");
            for (String query : queries) {
                if (!query.trim().isEmpty()) {
                    st.execute(query);
                }
            }
            System.out.println("Tablas creadas o verificadas correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
