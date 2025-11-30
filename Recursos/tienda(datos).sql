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
VALUES (9, 'Coca Cola 500ml', 'BEBIDAS', 1.20, 120, 'Coca Cola 500ml.jpg', 1),
       (10, 'Papitas BBQ', 'SNACKS', 0.90, 150, 'Papitas BBQ.jpg', 2),
       (11, 'Auriculares Bluetooth X10', 'ELECTRONICA', 29.99, 40, 'Auriculares Bluetooth X10.jpg', 3),
       (12, 'Desinfectante Floral 1L', 'LIMPIEZA', 2.50, 80, 'Desinfectante Floral 1L.jpg', 4),
       (14, 'Fanta Naranja 1.5L', 'BEBIDAS', 1.50, 100, 'Fanta Naranja 1.5L.jpg', 1),
       (15, 'Helado almendrado Hacendado', 'LACTEOS', 3.20, 60, 'Helado almendrado Hacendado.jpg', 5),
       (16, 'Plátano de Canarias IGP', 'FRUTAS_Y_VERDURAS', 1.80, 180, 'Plátano de Canarias IGP.jpg', 6),
       (17, 'Jamón serrano Incarlopsa lonchas', 'CARNICERIA', 7.50, 45, 'Jamón serrano Incarlopsa lonchas.jpg', 5),
       (18, 'Gamba blanca cocida Hacendado', 'PESCADERIA', 8.50, 35, 'Gamba blanca cocida Hacendado.jpg', 3),
       (19, 'Croissant de mantequilla 26%', 'PANADERIA', 0.95, 90, 'Croissant de mantequilla 26.jpg', 6),
       (20, 'Nuggets de pollo Hacendado ultracongelados', 'CONGELADOS', 4.80, 70,
        'Nuggets de pollo Hacendado ultracongelados.jpg', 4),
       (21, 'Atún claro en aceite de oliva Hacendado', 'CONSERVAS', 2.10, 200,
        'Atún claro en aceite de oliva Hacendado.jpg', 2),
       (22, 'Mascarilla Hydra hyaluronic Deliplus', 'CUIDADO_PERSONAL', 6.50, 55,
        'Mascarilla Hydra hyaluronic Deliplus.jpg', 4),
       (23, 'Comida perro adulto Compy', 'MASCOTAS', 7.99, 65, 'Comida perro adulto Compy.jpg', 6),
       (24, 'Bolsas de basura perfumadas Bosque Verde 30L', 'HOGAR', 3.30, 140,
        'Bolsas de basura perfumadas Bosque Verde 30L.jpg', 4),
       (25, 'Queso untar light Philadelphia', 'LACTEOS', 2.20, 80, 'Queso untar light Philadelphia.jpg', 5),
       (26, 'Refresco Coca-Cola 2L', 'BEBIDAS', 1.80, 110, 'Refresco Coca-Cola-2L.jpg', 1),
       (27, 'Tónica original Schweppes 1L', 'BEBIDAS', 1.10, 95, 'Tónica original Schweppes 1L.jpg', 1),
       (28, 'Sopa de pollo Gallina Blanca', 'CONSERVAS', 1.75, 120, 'Sopa de pollo Gallina Blanca.jpg', 2),
       (29, 'Pizza jamón y queso Hacendado', 'CONGELADOS', 3.50, 75, 'Pizza jamón y queso Hacendado.jpg', 4),
       (30, 'Refresco Fanta limón 2L', 'BEBIDAS', 1.60, 100, 'Refresco Fanta limón Botella 2 Litros.jpg', 1),
       (31, 'Arroz para microondas Hacendado', 'CONSERVAS', 1.00, 130,
        'Arroz para microondas Hacendado ultracongelado.jpg', 2),
       (32, 'Patatas 3kg', 'HOGAR', 2.80, 55, 'Patatas 3kg.jpg', 6),
       (33, 'Cava brut Bonaval', 'BEBIDAS', 6.99, 40, 'Cava brut Bonaval.jpg', 1),
       (34, 'Refresco coca Pepsi 12 Latas', 'BEBIDAS', 7.50, 60, 'Refresco cola Pepsi- 12 Latas.jpg', 1),
       (35, 'Tarta tres chocolates Hacendado', 'CONGELADOS', 5.50, 45,
        'Tarta tres chocolates Hacendado congelada sin gluten.jpg', 4),
       (36, 'Tomate frito Hacendado 3BRICKS', 'CONSERVAS', 1.20, 150, 'Tomate frito Hacendado 3BRICKS.jpg', 2),
       (37, 'Bebida isotónica limón Aquarius', 'BEBIDAS', 1.10, 140, 'Bebida isotónica limón Aquarius.jpg', 1),
       (38, 'Huevos tamaños diferentes', 'LACTEOS', 2.60, 85, 'Huevos tamaños diferentes.jpg', 5),
       (39, 'Burguer de cerdo', 'CARNICERIA', 3.80, 60, 'Burguer de cerdo.jpg', 5),
       (40, 'Paté perro junior Delikuit', 'MASCOTAS', 2.60, 70, 'Paté perro junior Delikuit con ternera y cordero.jpg',
        6),
       (41, 'Mascarilla Repair & Nutrition Deliplus', 'CUIDADO_PERSONAL', 7.50, 50,
        'Mascarilla Repair & Nutrition Deliplus.jpg', 4),
       (42, 'Lechuga Iceberg', 'FRUTAS_Y_VERDURAS', 0.95, 140, 'Lechuga Iceberg.jpg', 6),
       (43, 'Queso Lonchas Gouda Hacendado', 'LACTEOS', 2.95, 90, 'QuesoLonchasGoudaDeVacaHacendado.jpg', 5),
       (44, 'Refresco Fanta limón 9 Latas', 'BEBIDAS', 6.99, 45, 'Refresco Fanta limón 9 Latas.jpg', 1),
       (45, 'Acondicionador Deliplus', 'CUIDADO_PERSONAL', 4.99, 70, 'Acondicionador Suavidad y Brillo Deliplus.jpg',
        4),
       (46, '3 Barras de pan', 'PANADERIA', 1.50, 80, '3 Barras de pan.jpg', 6),
       (47, 'Refresco Coca-Cola 12 Latas', 'BEBIDAS', 8.49, 50, 'Refresco Coca-Cola-12-Latas.jpg', 1),
       (48, 'Tónica original Schweppes 8 latas', 'BEBIDAS', 7.25, 55, 'Tónica original Schweppes 8 latas.jpg', 1),
       (49, 'Zumo de naranja recién exprimido Hacendado', 'BEBIDAS', 2.50, 60,
        'Zumo de naranja recién exprimido Hacendado.jpg', 1),
       (50, 'Plátano de Canarias IGP (extra)', 'FRUTAS_Y_VERDURAS', 1.90, 120, 'Plátano de Canarias IGP.jpg', 6),
       (51, 'Zumo de piña y uva Hacendado 1L', 'BEBIDAS', 1.65, 110, 'Zumo de piña y uva Hacendado 1L.jpg', 1),
       (52, '12 Mini croissants de mantequilla', 'PANADERIA', 2.80, 70, '12 Mini croissants de mantequilla.jpg', 6),
       (53, 'Agua mineral Font Natura 1.5L', 'BEBIDAS', 0.45, 200,
        'Agua mineral grande Font Natura mineralización débil 1,5L.jpg', 1),
       (54, 'Agua mineral Font Natura pack', 'BEBIDAS', 2.20, 180,
        'Agua mineral grande Font Natura mineralización débil.jpg', 1),
       (55, 'Atún en aceite de girasol Hacendado', 'CONSERVAS', 1.95, 160, 'Atún en aceite de girasol Hacendado.jpg',
        2),
       (56, 'Bacón Hacendado cintas', 'CARNICERIA', 2.75, 85, 'Bacón Hacendado cintas.jpg', 5),
       (57, 'Bacón Oscar Mayer lonchas', 'CARNICERIA', 3.10, 70, 'Bacón Oscar Mayer lonchas.jpg', 5),
       (58, 'Bebida energética Zero Tropic Energy', 'BEBIDAS', 0.95, 140,
        'Bebida energética Zero Full Tropic Energy drink.jpg', 1),
       (59, 'Bebida isotónica naranja Aquarius', 'BEBIDAS', 1.10, 130, 'Bebida isotónica naranja Aquarius.jpg', 1),
       (60, 'Berberechos lata', 'CONSERVAS', 2.90, 90, 'Berberechos.jpg', 2),
       (61, 'Bífidus probiótico con ciruelas Hacendado', 'LACTEOS', 2.50, 75,
        'Bífidus probiótico con ciruelas pasas Hacendado.jpg', 5),
       (62, 'Bocaditos gato junior Delikuit', 'MASCOTAS', 3.20, 65,
        'Bocaditos en gelatina gato júnior Delikuit con pollo y pavo.jpg', 6),
       (63, 'Bocaditos gato adulto Lechat Excellence', 'MASCOTAS', 3.50, 55,
        'Bocaditos en salsa gato adulto Lechat Excellence con pollo, pavo y verduras.jpg', 6),
       (64, 'Castañas', 'FRUTAS_Y_VERDURAS', 4.80, 50, 'Castanas.jpg', 6),
       (65, 'Chopped cerdo Hacendado lonchas', 'CARNICERIA', 1.85, 100, 'Chopped cerdo Hacendado lonchas.jpg', 5),
       (66, 'Chorizo extra Hacendado lonchas', 'CARNICERIA', 2.50, 95, 'Chorizo extra Hacendado lonchas.jpg', 5),
       (67, 'Comida gato adulto Compy', 'MASCOTAS', 5.50, 80,
        'Comida gato adulto Compy con pollo, ternera, frutas y verduras.jpg', 6),
       (68, 'Comida gato junior Compy', 'MASCOTAS', 5.20, 75,
        'Comida gato júnior Compy con pollo, frutas y verduras.jpg', 6),
       (69, 'Comida perro júnior Compy', 'MASCOTAS', 8.20, 60,
        'Comida perro júnior Compy con pollo, arroz, frutas y verduras razas medianas y grandes.jpg', 6),
       (70, 'Compango asturiano', 'CARNICERIA', 4.30, 70, 'Compango.jpg', 5),
       (71, 'Costilla de cerdo salada', 'CARNICERIA', 5.10, 50, 'Costilla de cerdo salada.jpg', 5),
       (72, 'Desinfectante tejidos Bosque Verde', 'LIMPIEZA', 2.95, 110,
        'Desinfectante Tejidos y Lavadora Bosque Verde sin lejía.jpg', 4),
       (73, 'Detergente Micolor en gel', 'LIMPIEZA', 6.80, 90, 'Detergente ropa colada mixta Micolor en gel.jpg', 4),
       (74, 'Detergente Bosque Verde Marsella', 'LIMPIEZA', 4.20, 120,
        'Detergente ropa jabón natural de Marsella Bosque Verde líquido.jpg', 4),
       (75, 'Foie gras entero de pato Hacendado', 'CARNICERIA', 6.50, 45, 'Foie gras entero de pato Hacendado.jpg', 5),
       (76, 'Fuet espetec Hacendado', 'CARNICERIA', 1.80, 150, 'Fuet espetec Hacendado.jpg', 5),
       (77, 'Galletas Cookies Hacendado', 'SNACKS', 1.75, 140, 'GalletasCookiesHacendado.jpg', 2),
       (78, 'Granada', 'FRUTAS_Y_VERDURAS', 2.20, 90, 'Granada.jpg', 6),
       (79, 'Guisante fino ultracongelado', 'CONGELADOS', 1.60, 120, 'Guisante fino Hacendado ultracongelado.jpg', 4),
       (80, 'Helado mini surtido Hacendado', 'CONGELADOS', 3.90, 70, 'Helado mini surtido Hacendado.jpg', 4),
       (81, 'Jamón de bellota ibérico Candelita', 'CARNICERIA', 12.90, 30,
        'Jamón de bellota ibérico 100% Candelita.jpg', 5),
       (82, 'Kakis', 'FRUTAS_Y_VERDURAS', 1.70, 100, 'Kakis.jpg', 6),
       (83, 'Kit Makeup Cloud Deliplus', 'CUIDADO_PERSONAL', 12.99, 40,
        'Kit Makeup Cloud Deliplus contiene polvo iluminador, brocha, labial líquido y bolso con cadena.jpg', 4),
       (84, 'Lasaña boloñesa familiar Hacendado', 'CONGELADOS', 4.50, 55, 'Lasaña boloñesa familiar Hacendado.jpg', 4),
       (85, 'Leche semidesnatada Hacendado', 'LACTEOS', 0.95, 180, 'Leche semidesnatada Hacendado.jpg', 5),
       (86, 'Leche semidesnatada sin lactosa Hacendado', 'LACTEOS', 1.10, 170,
        'Leche semidesnatada sin lactosa Hacendado.jpg', 5),
       (87, 'Limpiador baños Bosque Verde gel', 'LIMPIEZA', 2.10, 120, 'Limpiador Baños Bosque Verde en gel.jpg', 4),
       (88, 'Mandarina', 'FRUTAS_Y_VERDURAS', 1.80, 140, 'Mandarina.jpg', 6),
       (89, 'Mantequilla sin sal Hacendado', 'LACTEOS', 2.10, 95, 'Mantequilla sin sal añadida Hacendado.jpg', 5),
       (90, 'Manzana roja dulce', 'FRUTAS_Y_VERDURAS', 1.65, 130, 'Manzana roja dulce.jpg', 6),
       (91, 'Manzanas rojas acidulces', 'FRUTAS_Y_VERDURAS', 1.60, 120, 'Manzanas rojas acidulces.jpg', 6),
       (92, 'Medio conejo troceado', 'CARNICERIA', 6.00, 50, 'Medio Conejo Troceado.jpg', 5),
       (93, 'Medio melón piel de sapo', 'FRUTAS_Y_VERDURAS', 2.95, 75, 'Medio melón piel de sapo.jpg', 6),
       (94, 'Mejillón fresco', 'PESCADERIA', 4.50, 60, 'Mejillon.jpg', 3),
       (95, 'Naranja de mesa', 'FRUTAS_Y_VERDURAS', 1.40, 150, 'Naranja de mesa.jpg', 6),
       (96, 'Paté gato adulto Delikuit', 'MASCOTAS', 1.80, 90, 'Paté gato adulto Delikuit con ternera.jpg', 6),
       (97, 'Perfilador de labios Deliplus 15', 'CUIDADO_PERSONAL', 3.60, 80,
        'Perfilador de labios Long Lasting Deliplus 15 canela claro.jpg', 4),
       (98, 'Pizza barbacoa Hacendado', 'CONGELADOS', 3.80, 80, 'Pizza barbacoa Hacendado.jpg', 4),
       (99, 'Pizza jamón y queso familiar Hacendado', 'CONGELADOS', 4.80, 85,
        'Pizza jamón y queso familiar Hacendado.jpg', 4),
       (100, 'Pizza pepperoni Hacendado', 'CONGELADOS', 3.95, 70, 'Pizza pepperoni Hacendado.jpg', 4),
       (101, 'Pizza romana Hacendado', 'CONGELADOS', 4.20, 65, 'Pizza romana Hacendado con champiñones salteados.jpg',
        4),
       (102, 'Pizzas tomate y queso Hacendado', 'CONGELADOS', 3.60, 80,
        'Pizzas tomate y queso Hacendado ultracongeladas.jpg', 4),
       (103, 'Preparado paella y sopa Hacendado', 'CONGELADOS', 3.20, 100,
        'Preparado de paella y sopa Hacendado ultracongelado.jpg', 4),
       (104, 'Queso en polvo Grana Padano', 'LACTEOS', 2.60, 110, 'QuesoEnPolvoGranaPadanoHacendado.jpg', 5),
       (105, 'Queso rallado especial fundir', 'LACTEOS', 2.30, 120, 'QuesoRalladoEspecialFundirMezclaHacendado.jpg', 5),
       (106, 'Refresco Fanta naranja 9 Latas', 'BEBIDAS', 6.99, 50, 'Refresco Fanta naranja 9 Latas.jpg', 1),
       (107, 'Refresco lima limón 7Up 9 Latas', 'BEBIDAS', 6.99, 55, 'Refresco lima limón 7 Up 9 Latas.jpg', 1),
       (108, 'Servilleta papel Bosque Verde', 'HOGAR', 1.20, 200,
        'Servilleta papel color Bosque Verde 2 capas 33 x 33 cm.jpg', 4),
       (109, 'Spaghetti carbonara Hacendado', 'CONSERVAS', 2.80, 110, 'Spaghetti carbonara Hacendado.jpg', 2);

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
