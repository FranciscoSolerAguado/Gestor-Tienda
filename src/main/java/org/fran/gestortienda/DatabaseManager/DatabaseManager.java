package org.fran.gestortienda.DatabaseManager;

import org.fran.gestortienda.Connection.ConnectionFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseManager {

    public static void crearTablas() {
        // Este método se mantiene igual, solo crea la estructura.
        String sql = """
                CREATE TABLE IF NOT EXISTS proveedor (
                    id_proveedor INT AUTO_INCREMENT PRIMARY KEY,
                    nombre VARCHAR(50) NOT NULL,
                    telefono VARCHAR(20) NOT NULL,
                    correo VARCHAR(100) NOT NULL
                );
                CREATE TABLE IF NOT EXISTS producto (
                    id_producto INT AUTO_INCREMENT PRIMARY KEY,
                    nombre VARCHAR(50) NOT NULL,
                    categoria VARCHAR(50) NOT NULL,
                    precio DECIMAL(10,2) NOT NULL,
                    stock INT NOT NULL,
                    imagen VARCHAR(255),
                    id_proveedor INT NOT NULL,
                    FOREIGN KEY (id_proveedor) REFERENCES proveedor(id_proveedor)
                );
                CREATE TABLE IF NOT EXISTS cliente (
                    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
                    nombre VARCHAR(50) NOT NULL,
                    telefono VARCHAR(20),
                    direccion VARCHAR(100)
                );
                CREATE TABLE IF NOT EXISTS venta (
                    id_venta INT AUTO_INCREMENT PRIMARY KEY,
                    fecha DATE NOT NULL,
                    total DECIMAL(10,2) NOT NULL,
                    id_cliente INT NOT NULL,
                    FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente)
                );
                CREATE TABLE IF NOT EXISTS detalle_venta (
                    id_detalle INT AUTO_INCREMENT PRIMARY KEY,
                    id_venta INT NOT NULL,
                    id_producto INT NOT NULL,
                    cantidad INT NOT NULL,
                    descuento DECIMAL(5,2) DEFAULT 0.00,
                    precio_unitario DECIMAL(10,2) NOT NULL,
                    iva DECIMAL(5,2) NOT NULL,
                    subtotal DECIMAL(10,2) NOT NULL,
                    FOREIGN KEY (id_venta) REFERENCES venta(id_venta),
                    FOREIGN KEY (id_producto) REFERENCES producto(id_producto)
                );
        """;
        try (Connection conn = ConnectionFactory.getConnection();
             Statement st = conn.createStatement()) {
            String[] queries = sql.split(";");
            for (String query : queries) {
                if (!query.trim().isEmpty()) {
                    st.execute(query);
                }
            }
            System.out.println("Tablas creadas o verificadas correctamente.");
        } catch (Exception e) {
            System.err.println("Error al crear las tablas.");
            e.printStackTrace();
        }
    }

    public static void seedData() {
        String checkSql = "SELECT COUNT(*) FROM proveedor";
        boolean databaseIsEmpty = false;

        // --- PASO 1: Comprobar si la base de datos está vacía ---
        try (Connection conn = ConnectionFactory.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(checkSql)) {

            if (rs.next() && rs.getInt(1) == 0) {
                databaseIsEmpty = true;
            }

        } catch (Exception e) {
            System.err.println("Tablas no existen todavía. Será necesario crearlas.");
            DatabaseManager.crearTablas();
            databaseIsEmpty = true;  // forzar inserción
        }

        // --- PASO 2: Si está vacía, insertar los datos ---
        if (databaseIsEmpty) {
            System.out.println("Base de datos vacía. Insertando datos iniciales...");
            String insertSql = """
            INSERT INTO proveedor (id_proveedor, nombre, telefono, correo) VALUES
                (1, 'Distribuciones Bebidas SL', '600123123', 'contacto@bebidas.com'),
                (2, 'Snacks Factory', '600456456', 'ventas@snacksfactory.com'),
                (3, 'ElectroTech Import', '600789789', 'info@electrotech.com'),
                (4, 'CleanHouse Proveedores', '600111222', 'soporte@cleanhouse.com');

            INSERT INTO producto (id_producto, nombre, categoria, precio, stock, imagen, id_proveedor) VALUES
                (9, 'Coca Cola 500ml', 'Bebidas', 1.20, 80, 'org/fran/gestortienda/img/coca_cola_500ml.jpg', 1),
                (10, 'Papitas BBQ', 'Snacks', 0.90, 120, 'org/fran/gestortienda/img/papitas_bbq.png', 2),
                (11, 'Auriculares Bluetooth X10', 'Electronica', 15.99, 40, 'org/fran/gestortienda/img/auriculares_x10.jpg', 3),
                (12, 'Desinfectante Floral 1L', 'Limpieza', 2.50, 35, 'org/fran/gestortienda/img/desinfectante_floral.jpg', 4);

            INSERT INTO cliente (id_cliente, nombre, telefono, direccion) VALUES
                (2, 'Juan Pérez', '666555444', 'Calle Falsa 123');

            INSERT INTO venta (id_venta, fecha, total, id_cliente) VALUES
                (3, '2025-11-19', 200.50, 2);
        """;

            try (Connection conn = ConnectionFactory.getConnection();
                 Statement st = conn.createStatement()) {

                String[] queries = insertSql.split(";");
                for (String query : queries) {
                    if (!query.trim().isEmpty()) {
                        st.execute(query);
                    }
                }
                System.out.println("Datos iniciales insertados correctamente.");

            } catch (Exception e) {
                System.err.println("Error al insertar los datos iniciales.");
                e.printStackTrace();
            }
        } else {
            System.out.println("La base de datos ya contiene datos. No se insertarán datos iniciales.");
        }
    }
}