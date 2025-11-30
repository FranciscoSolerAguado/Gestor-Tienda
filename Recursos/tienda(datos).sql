-- SQL dump (estructura + datos de ejemplo, basado en opción "Realista")
-- Base de datos: `tienda` (crea la base previamente si hace falta)
-- Ejecuta en MariaDB/MySQL

SET
SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET
time_zone = "+00:00";
SET NAMES utf8mb4;

-- --------------------------------------------------------
-- Tabla: cliente
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `cliente`
(
    `id_cliente`
    INT
(
    10
) UNSIGNED ZEROFILL NOT NULL,
    `nombre` VARCHAR
(
    50
) NOT NULL,
    `telefono` VARCHAR
(
    15
) DEFAULT NULL,
    `direccion` VARCHAR
(
    100
) DEFAULT NULL,
    PRIMARY KEY
(
    `id_cliente`
)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE =utf8mb4_general_ci;

-- --------------------------------------------------------
-- Tabla: proveedor
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `proveedor`
(
    `id_proveedor`
    INT
(
    10
) UNSIGNED ZEROFILL NOT NULL,
    `nombre` VARCHAR
(
    50
) NOT NULL,
    `telefono` VARCHAR
(
    15
) NOT NULL,
    `correo` VARCHAR
(
    100
) NOT NULL,
    PRIMARY KEY
(
    `id_proveedor`
)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE =utf8mb4_general_ci;

-- --------------------------------------------------------
-- Tabla: producto
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `producto`
(
    `id_producto`
    INT
(
    10
) UNSIGNED ZEROFILL NOT NULL,
    `nombre` VARCHAR
(
    100
) NOT NULL,
    `categoria` ENUM
(
    'BEBIDAS',
    'SNACKS',
    'ELECTRONICA',
    'LIMPIEZA',
    'LACTEOS',
    'FRUTAS_Y_VERDURAS',
    'CARNICERIA',
    'PESCADERIA',
    'PANADERIA',
    'CONGELADOS',
    'CONSERVAS',
    'CUIDADO_PERSONAL',
    'MASCOTAS',
    'HOGAR'
) NOT NULL,
    `precio` DECIMAL
(
    10,
    2
) NOT NULL,
    `stock` INT
(
    11
) NOT NULL,
    `imagen` VARCHAR
(
    255
) NOT NULL,
    `id_proveedor` INT
(
    10
) UNSIGNED ZEROFILL NOT NULL,
    PRIMARY KEY
(
    `id_producto`
),
    KEY `id_proveedor`
(
    `id_proveedor`
)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE =utf8mb4_general_ci;

-- --------------------------------------------------------
-- Tabla: venta
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `venta`
(
    `id_venta`
    INT
(
    10
) UNSIGNED ZEROFILL NOT NULL,
    `fecha` DATE NOT NULL,
    `total` DECIMAL
(
    10,
    2
) NOT NULL,
    `id_cliente` INT
(
    10
) UNSIGNED ZEROFILL NOT NULL,
    PRIMARY KEY
(
    `id_venta`
),
    KEY `id_cliente`
(
    `id_cliente`
)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE =utf8mb4_general_ci;

-- --------------------------------------------------------
-- Tabla: detalle_venta
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `detalle_venta`
(
    `id_detalle`
    INT
(
    10
) UNSIGNED ZEROFILL NOT NULL,
    `id_venta` INT
(
    10
) UNSIGNED ZEROFILL NOT NULL,
    `id_producto` INT
(
    10
) UNSIGNED ZEROFILL NOT NULL,
    `cantidad` INT
(
    11
) NOT NULL,
    `descuento` DECIMAL
(
    5,
    2
) DEFAULT 0.00,
    `precio_unitario` DECIMAL
(
    10,
    2
) NOT NULL,
    `iva` DECIMAL
(
    5,
    2
) NOT NULL,
    `subtotal` DECIMAL
(
    10,
    2
) NOT NULL,
    PRIMARY KEY
(
    `id_detalle`
),
    KEY `id_venta`
(
    `id_venta`
),
    KEY `id_producto`
(
    `id_producto`
)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE =utf8mb4_general_ci;


-- --------------------------------------------------------
-- Inserciones: proveedores (ejemplos)
-- --------------------------------------------------------
INSERT INTO proveedor (id_proveedor, nombre, telefono, correo)
VALUES (0000000001, 'Distribuciones Bebidas SL', '600123123', 'contacto@bebidas.com'),
       (0000000002, 'Snacks Factory', '600456456', 'ventas@snacksfactory.com'),
       (0000000003, 'ElectroTech Import', '600789789', 'info@electrotech.com'),
       (0000000004, 'CleanHouse Proveedores', '600111222', 'soporte@cleanhouse.com'),
       (0000000005, 'Lacteos & Co', '600222333', 'lacteos@example.com'),
       (0000000006, 'Frutas y Verduras S.L.', '600333444', 'frutas@example.com');

-- --------------------------------------------------------
-- Inserciones: clientes (10 ejemplo)
-- --------------------------------------------------------
INSERT INTO cliente (id_cliente, nombre, telefono, direccion)
VALUES (0000000001, 'María García', '600111001', 'Calle Falsa 1, Ciudad'),
       (0000000002, 'Juan López', '600111002', 'Avenida Siempre Viva 2'),
       (0000000003, 'Ana Martínez', '600111003', 'Plaza Central 3'),
       (0000000004, 'Luis Fernández', '600111004', 'Calle Mayor 4'),
       (0000000005, 'Sofía Ruiz', '600111005', 'Calle Nueva 5'),
       (0000000006, 'Carlos Gómez', '600111006', 'Camino Real 6'),
       (0000000007, 'Lucía Torres', '600111007', 'Paseo de la Ribera 7'),
       (0000000008, 'Miguel Sánchez', '600111008', 'Ronda Sur 8'),
       (0000000009, 'Carmen Díaz', '600111009', 'Barrio Alto 9'),
       (0000000010, 'Diego Romero', '600111010', 'Polígono Industrial 10');

-- --------------------------------------------------------
-- Inserciones: productos (40 representativos)
-- Los ids usan formato ZEROFILL como en tu BD
-- Ruta de imagen: /org/fran/gestortienda/img/productos/<archivo>.jpg
-- --------------------------------------------------------
INSERT INTO producto (id_producto, nombre, categoria, precio, stock, imagen, id_proveedor)
VALUES (0000000009, 'Coca Cola 500ml', 'BEBIDAS', 1.20, 120, '/org/fran/gestortienda/img/productos/Coca Cola 500ml.jpg',
        0000000001),
       (0000000010, 'Papitas BBQ', 'SNACKS', 0.90, 150, '/org/fran/gestortienda/img/productos/Papitas BBQ.jpg',
        0000000002),
       (0000000011, 'Auriculares Bluetooth X10', 'ELECTRONICA', 29.99, 40,
        '/org/fran/gestortienda/img/productos/Auriculares Bluetooth X10.jpg', 0000000003),
       (0000000012, 'Desinfectante Floral 1L', 'LIMPIEZA', 2.50, 80,
        '/org/fran/gestortienda/img/productos/Desinfectante Floral 1L.jpg', 0000000004),
       (0000000014, 'Fanta Naranja 1.5L', 'BEBIDAS', 1.50, 100,
        '/org/fran/gestortienda/img/productos/Fanta Naranja 1.5L.jpg', 0000000001),
       (0000000015, 'Helado almendrado Hacendado', 'LACTEOS', 3.20, 60,
        '/org/fran/gestortienda/img/productos/Helado almendrado Hacendado.jpg', 0000000005),
       (0000000016, 'Plátano de Canarias IGP', 'FRUTAS_Y_VERDURAS', 1.80, 180,
        '/org/fran/gestortienda/img/productos/Plátano de Canarias IGP.jpg', 0000000006),
       (0000000017, 'Jamón serrano Incarlopsa lonchas', 'CARNICERIA', 7.50, 45,
        '/org/fran/gestortienda/img/productos/Jamón serrano Incarlopsa lonchas.jpg', 0000000005),
       (0000000018, 'Gamba blanca cocida Hacendado', 'PESCADERIA', 8.50, 35,
        '/org/fran/gestortienda/img/productos/Gamba blanca cocida Hacendado.jpg', 0000000003),
       (0000000019, 'Croissant de mantequilla 26%', 'PANADERIA', 0.95, 90,
        '/org/fran/gestortienda/img/productos/Croissant de mantequilla 26.jpg', 0000000006),
       (0000000020, 'Nuggets de pollo Hacendado ultracongelados', 'CONGELADOS', 4.80, 70,
        '/org/fran/gestortienda/img/productos/Nuggets de pollo Hacendado ultracongelados.jpg', 0000000004),
       (0000000021, 'Atún claro en aceite de oliva Hacendado', 'CONSERVAS', 2.10, 200,
        '/org/fran/gestortienda/img/productos/Atún claro en aceite de oliva Hacendado.jpg', 0000000002),
       (0000000022, 'Mascarilla Hydra hyaluronic Deliplus', 'CUIDADO_PERSONAL', 6.50, 55,
        '/org/fran/gestortienda/img/productos/Mascarilla Hydra hyaluronic Deliplus.jpg', 0000000004),
       (0000000023, 'Comida perro adulto Compy', 'MASCOTAS', 7.99, 65,
        '/org/fran/gestortienda/img/productos/Comida perro adulto Compy.jpg', 0000000006),
       (0000000024, 'Bolsas de basura perfumadas Bosque Verde 30L', 'HOGAR', 3.30, 140,
        '/org/fran/gestortienda/img/productos/Bolsas de basura perfumadas Bosque Verde 30L.jpg', 0000000004),
       (0000000025, 'Queso untar light Philadelphia', 'LACTEOS', 2.20, 80,
        '/org/fran/gestortienda/img/productos/Queso untar light Philadelphia.jpg', 0000000005),
       (0000000026, 'Refresco Coca-Cola 2L', 'BEBIDAS', 1.80, 110,
        '/org/fran/gestortienda/img/productos/Refresco Coca-Cola-2L.jpg', 0000000001),
       (0000000027, 'Tónica original Schweppes 1L', 'BEBIDAS', 1.10, 95,
        '/org/fran/gestortienda/img/productos/Tónica original Schweppes 1L.jpg', 0000000001),
       (0000000028, 'Sopa de pollo Gallina Blanca', 'CONSERVAS', 1.75, 120,
        '/org/fran/gestortienda/img/productos/Sopa de pollo Gallina Blanca.jpg', 0000000002),
       (0000000029, 'Pizza jamón y queso Hacendado', 'CONGELADOS', 3.50, 75,
        '/org/fran/gestortienda/img/productos/Pizza jamón y queso Hacendado.jpg', 0000000004),
       (0000000030, 'Refresco Fanta limón 2L', 'BEBIDAS', 1.60, 100,
        '/org/fran/gestortienda/img/productos/Refresco Fanta limón Botella 2 Litros.jpg', 0000000001),
       (0000000031, 'Arroz para microondas Hacendado', 'CONSERVAS', 1.00, 130,
        '/org/fran/gestortienda/img/productos/Arroz para microondas Hacendado ultracongelado.jpg', 0000000002),
       (0000000032, 'Patatas 3kg', 'HOGAR', 2.80, 55, '/org/fran/gestortienda/img/productos/Patatas 3kg.jpg',
        0000000006),
       (0000000033, 'Cava brut Bonaval', 'BEBIDAS', 6.99, 40,
        '/org/fran/gestortienda/img/productos/Cava brut Bonaval.jpg', 0000000001),
       (0000000034, 'Refresco coca Pepsi 12 Latas', 'BEBIDAS', 7.50, 60,
        '/org/fran/gestortienda/img/productos/Refresco cola Pepsi- 12 Latas.jpg', 0000000001),
       (0000000035, 'Tarta tres chocolates Hacendado', 'CONGELADOS', 5.50, 45,
        '/org/fran/gestortienda/img/productos/Tarta tres chocolates Hacendado congelada sin gluten.jpg', 0000000004),
       (0000000036, 'Tomate frito Hacendado 3BRICKS', 'CONSERVAS', 1.20, 150,
        '/org/fran/gestortienda/img/productos/Tomate frito Hacendado 3BRICKS.jpg', 0000000002),
       (0000000037, 'Bebida isotónica limón Aquarius', 'BEBIDAS', 1.10, 140,
        '/org/fran/gestortienda/img/productos/Bebida isotónica limón Aquarius.jpg', 0000000001),
       (0000000038, 'Huevos tamaños diferentes', 'LACTEOS', 2.60, 85,
        '/org/fran/gestortienda/img/productos/Huevos tamaños diferentes.jpg', 0000000005),
       (0000000039, 'Burguer de cerdo', 'CARNICERIA', 3.80, 60,
        '/org/fran/gestortienda/img/productos/Burguer de cerdo.jpg', 0000000005),
       (0000000040, 'Paté perro junior Delikuit', 'MASCOTAS', 2.60, 70,
        '/org/fran/gestortienda/img/productos/Paté perro junior Delikuit con ternera y cordero.jpg', 0000000006),
       (0000000041, 'Mascarilla Repair & Nutrition Deliplus', 'CUIDADO_PERSONAL', 7.50, 50,
        '/org/fran/gestortienda/img/productos/Mascarilla Repair & Nutrition Deliplus.jpg', 0000000004),
       (0000000042, 'Lechuga Iceberg', 'FRUTAS_Y_VERDURAS', 0.95, 140,
        '/org/fran/gestortienda/img/productos/Lechuga Iceberg.jpg', 0000000006),
       (0000000043, 'Queso Lonchas Gouda Hacendado', 'LACTEOS', 2.95, 90,
        '/org/fran/gestortienda/img/productos/QuesoLonchasGoudaDeVacaHacendado.jpg', 0000000005),
       (0000000044, 'Refresco Fanta limón 9 Latas', 'BEBIDAS', 6.99, 45,
        '/org/fran/gestortienda/img/productos/Refresco Fanta limón 9 Latas.jpg', 0000000001),
       (0000000045, 'Acondicionador Deliplus', 'CUIDADO_PERSONAL', 4.99, 70,
        '/org/fran/gestortienda/img/productos/Acondicionador Suavidad y Brillo Deliplus.jpg', 0000000004),
       (0000000046, '3 Barras de pan', 'PANADERIA', 1.50, 80,
        '/org/fran/gestortienda/img/productos/3 Barras de pan.jpg', 0000000006),
       (0000000047, 'Refresco Coca-Cola 12 Latas', 'BEBIDAS', 8.49, 50,
        '/org/fran/gestortienda/img/productos/Refresco Coca-Cola-12-Latas.jpg', 0000000001),
       (0000000048, 'Tónica original Schweppes 8 latas', 'BEBIDAS', 7.25, 55,
        '/org/fran/gestortienda/img/productos/Tónica original Schweppes 8 latas.jpg', 0000000001),
       (0000000049, 'Zumo de naranja recién exprimido Hacendado', 'BEBIDAS', 2.50, 60,
        '/org/fran/gestortienda/img/productos/Zumo de naranja recién exprimido Hacendado.jpg', 0000000001),
       (0000000050, 'Plátano de Canarias IGP (extra)', 'FRUTAS_Y_VERDURAS', 1.90, 120,
        '/org/fran/gestortienda/img/productos/Plátano de Canarias IGP.jpg', 0000000006);

-- --------------------------------------------------------
-- Inserciones: ventas (10 ejemplo)
-- --------------------------------------------------------
INSERT INTO venta (id_venta, fecha, total, id_cliente)
VALUES (0000000001, '2025-11-01', 23.40, 0000000001),
       (0000000002, '2025-11-05', 45.20, 0000000002),
       (0000000003, '2025-11-10', 12.10, 0000000003),
       (0000000004, '2025-11-12', 60.75, 0000000004),
       (0000000005, '2025-11-15', 32.00, 0000000005),
       (0000000006, '2025-11-16', 18.90, 0000000006),
       (0000000007, '2025-11-18', 78.40, 0000000007),
       (0000000008, '2025-11-19', 9.99, 0000000008),
       (0000000009, '2025-11-20', 150.00, 0000000009),
       (0000000010, '2025-11-21', 55.60, 0000000010);

-- --------------------------------------------------------
-- Inserciones: detalle_venta (30 ejemplo)
-- subtotal = cantidad * precio_unitario * (1 + iva/100) - descuento
-- IVA fijado mayoritariamente a 21% salvo productos alimentarios donde se usa 10%/4% según caso (a efectos del ejemplo se mezcla 21/10)
-- --------------------------------------------------------
INSERT INTO detalle_venta (id_detalle, id_venta, id_producto, cantidad, descuento, precio_unitario, iva, subtotal)
VALUES (0000000001, 0000000001, 0000000009, 6, 0.00, 1.20, 21.00, 8.71),
       (0000000002, 0000000001, 0000000010, 3, 0.00, 0.90, 21.00, 3.27),
       (0000000003, 0000000002, 0000000011, 1, 0.00, 29.99, 21.00, 36.29),
       (0000000004, 0000000002, 0000000012, 4, 0.00, 2.50, 21.00, 12.10),
       (0000000005, 0000000003, 0000000015, 2, 0.00, 3.20, 10.00, 7.04),
       (0000000006, 0000000004, 0000000017, 1, 0.00, 7.50, 21.00, 9.08),
       (0000000007, 0000000004, 0000000025, 2, 0.00, 2.20, 10.00, 4.84),
       (0000000008, 0000000005, 0000000026, 4, 0.00, 1.80, 21.00, 8.71),
       (0000000009, 0000000005, 0000000030, 1, 0.00, 1.60, 21.00, 1.94),
       (0000000010, 0000000006, 0000000036, 3, 0.00, 1.20, 21.00, 4.33),
       (0000000011, 0000000007, 0000000020, 5, 0.00, 4.80, 10.00, 26.40),
       (0000000012, 0000000007, 0000000033, 2, 0.00, 6.99, 21.00, 16.90),
       (0000000013, 0000000008, 0000000037, 1, 0.00, 1.10, 21.00, 1.33),
       (0000000014, 0000000009, 0000000018, 10, 0.00, 8.50, 10.00, 93.50),
       (0000000015, 0000000009, 0000000023, 5, 0.00, 7.99, 21.00, 48.35),
       (0000000016, 0000000009, 0000000027, 6, 0.00, 1.10, 21.00, 7.99),
       (0000000017, 0000000010, 0000000035, 1, 0.00, 5.50, 10.00, 6.05),
       (0000000018, 0000000010, 0000000029, 2, 0.00, 3.50, 10.00, 7.70),
       (0000000019, 0000000002, 0000000021, 3, 0.00, 2.10, 10.00, 6.93),
       (0000000020, 0000000003, 0000000022, 1, 0.00, 6.50, 21.00, 7.87),
       (0000000021, 0000000004, 0000000030, 2, 0.00, 1.60, 21.00, 3.87),
       (0000000022, 0000000006, 0000000024, 1, 0.00, 3.30, 21.00, 3.99),
       (0000000023, 0000000008, 0000000028, 2, 0.00, 1.75, 21.00, 4.23),
       (0000000024, 0000000005, 0000000031, 5, 0.00, 1.00, 21.00, 6.05),
       (0000000025, 0000000007, 0000000040, 2, 0.00, 2.60, 21.00, 6.29),
       (0000000026, 0000000001, 0000000036, 1, 0.00, 1.20, 21.00, 1.45),
       (0000000027, 0000000002, 0000000026, 3, 0.00, 1.80, 21.00, 6.54),
       (0000000028, 0000000003, 0000000042, 4, 0.00, 0.95, 21.00, 4.59),
       (0000000029, 0000000009, 0000000044, 1, 0.00, 6.99, 21.00, 8.46),
       (0000000030, 0000000010, 0000000041, 1, 0.00, 7.50, 21.00, 9.08);

-- --------------------------------------------------------
-- AUTO_INCREMENT y constraints (dejamos los AUTO_INCREMENT para que MySQL genere nuevos IDs correctamente)
-- --------------------------------------------------------
ALTER TABLE cliente MODIFY `id_cliente` INT (10) UNSIGNED ZEROFILL NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;
ALTER TABLE proveedor MODIFY `id_proveedor` INT (10) UNSIGNED ZEROFILL NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;
ALTER TABLE producto MODIFY `id_producto` INT (10) UNSIGNED ZEROFILL NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=51;
ALTER TABLE venta MODIFY `id_venta` INT (10) UNSIGNED ZEROFILL NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;
ALTER TABLE detalle_venta MODIFY `id_detalle` INT (10) UNSIGNED ZEROFILL NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=31;

-- --------------------------------------------------------
-- Foreign Keys
-- --------------------------------------------------------
ALTER TABLE detalle_venta
    ADD CONSTRAINT detalle_venta_ibfk_1 FOREIGN KEY (id_venta) REFERENCES venta (id_venta),
  ADD CONSTRAINT detalle_venta_ibfk_2 FOREIGN KEY (id_producto) REFERENCES producto (id_producto);

ALTER TABLE producto
    ADD CONSTRAINT producto_ibfk_1 FOREIGN KEY (id_proveedor) REFERENCES proveedor (id_proveedor);

ALTER TABLE venta
    ADD CONSTRAINT venta_ibfk_1 FOREIGN KEY (id_cliente) REFERENCES cliente (id_cliente);

COMMIT;
