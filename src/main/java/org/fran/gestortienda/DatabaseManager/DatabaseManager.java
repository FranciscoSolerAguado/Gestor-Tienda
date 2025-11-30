package org.fran.gestortienda.DatabaseManager;

import org.fran.gestortienda.Connection.ConnectionFactory;
import org.fran.gestortienda.utils.LoggerUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Logger;

public class DatabaseManager {
    private static final Logger LOGGER = LoggerUtil.getLogger();

    public static void crearTablas() {

        // 1. Comprobamos si la base de datos activa es H2
        if ("h2".equals(ConfigManager.getActiveDatabaseType())) {
            LOGGER.info("La base de datos es H2. Creando tablas si no existen...");

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
                                categoria ENUM(
                                    'BEBIDAS', 'SNACKS', 'ELECTRONICA', 'LIMPIEZA',
                                    'LACTEOS', 'FRUTAS_Y_VERDURAS', 'CARNICERIA', 'PESCADERIA', 
                                    'PANADERIA', 'CONGELADOS', 'CONSERVAS', 'CUIDADO_PERSONAL', 
                                    'MASCOTAS', 'HOGAR'
                                ) NOT NULL,
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
                LOGGER.info("Tablas creadas o verificadas correctamente para H2.");
            } catch (Exception e) {
                LOGGER.severe("Error al crear las tablas para H2.");
                e.printStackTrace();
            }
        } else {
            LOGGER.info("La base de datos no es H2. Omitiendo creación de tablas.");
        }
    }

    public static void seedData() {
        // 2. Comprobamos si la base de datos activa es H2
        if ("h2".equals(ConfigManager.getActiveDatabaseType())) {
            LOGGER.info("La base de datos es H2. Comprobando si se necesitan datos iniciales...");

            String checkSql = "SELECT COUNT(*) FROM proveedor";
            boolean databaseIsEmpty = false;

            try (Connection conn = ConnectionFactory.getConnection();
                 Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(checkSql)) {
                if (rs.next() && rs.getInt(1) == 0) {
                    databaseIsEmpty = true;
                }
            } catch (Exception e) {
                // Este catch es problemático si las tablas realmente no existen en MySQL.
                // Al hacerlo condicional a H2, el riesgo disminuye.
                LOGGER.warning("No se pudo comprobar el contenido de las tablas (posiblemente no existen).");
                databaseIsEmpty = true; // Forzamos la inserción
            }

            if (databaseIsEmpty) {
                LOGGER.info("Base de datos H2 vacía. Insertando datos iniciales...");
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
                    LOGGER.info("Datos iniciales para H2 insertados correctamente.");
                } catch (Exception e) {
                    LOGGER.severe("Error al insertar los datos iniciales en H2.");
                    e.printStackTrace();
                }
            } else {
                LOGGER.info("La base de datos H2 ya contiene datos. No se insertarán datos iniciales.");
            }
        } else {
            LOGGER.info("La base de datos no es H2. Omitiendo inserción de datos de prueba (seed data).");
        }
    }
}