-- phpMyAdmin SQL Dump (versión vacía)
-- Base de datos: `tienda`

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";
SET NAMES utf8mb4;

-- --------------------------------------------------------
-- Tabla: cliente
-- --------------------------------------------------------

CREATE TABLE `cliente` (
                           `id_cliente` int(10) UNSIGNED ZEROFILL NOT NULL,
                           `nombre` varchar(50) NOT NULL,
                           `telefono` varchar(15) DEFAULT NULL,
                           `direccion` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------
-- Tabla: detalle_venta
-- --------------------------------------------------------

CREATE TABLE `detalle_venta` (
                                 `id_detalle` int(10) UNSIGNED ZEROFILL NOT NULL,
                                 `id_venta` int(10) UNSIGNED ZEROFILL NOT NULL,
                                 `id_producto` int(10) UNSIGNED ZEROFILL NOT NULL,
                                 `cantidad` int(11) NOT NULL,
                                 `descuento` decimal(5,2) DEFAULT 0.00,
                                 `precio_unitario` decimal(10,2) NOT NULL,
                                 `iva` decimal(5,2) NOT NULL,
                                 `subtotal` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------
-- Tabla: producto
-- --------------------------------------------------------

CREATE TABLE `producto` (
                            `id_producto` int(10) UNSIGNED ZEROFILL NOT NULL,
                            `nombre` varchar(50) NOT NULL,
                            `categoria` enum('Bebidas','Snacks','Electronica','Limpieza') NOT NULL,
                            `precio` decimal(10,2) NOT NULL,
                            `stock` int(11) NOT NULL,
                            `imagen` varchar(255) NOT NULL,
                            `id_proveedor` int(10) UNSIGNED ZEROFILL NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------
-- Tabla: proveedor
-- --------------------------------------------------------

CREATE TABLE `proveedor` (
                             `id_proveedor` int(10) UNSIGNED ZEROFILL NOT NULL,
                             `nombre` varchar(50) NOT NULL,
                             `telefono` varchar(15) NOT NULL,
                             `correo` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------
-- Tabla: venta
-- --------------------------------------------------------

CREATE TABLE `venta` (
                         `id_venta` int(10) UNSIGNED ZEROFILL NOT NULL,
                         `fecha` date NOT NULL,
                         `total` decimal(10,2) NOT NULL,
                         `id_cliente` int(10) UNSIGNED ZEROFILL NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------
-- Índices
-- --------------------------------------------------------

ALTER TABLE `cliente`
    ADD PRIMARY KEY (`id_cliente`);

ALTER TABLE `detalle_venta`
    ADD PRIMARY KEY (`id_detalle`),
  ADD KEY `id_venta` (`id_venta`),
  ADD KEY `id_producto` (`id_producto`);

ALTER TABLE `producto`
    ADD PRIMARY KEY (`id_producto`),
  ADD KEY `id_proveedor` (`id_proveedor`);

ALTER TABLE `proveedor`
    ADD PRIMARY KEY (`id_proveedor`);

ALTER TABLE `venta`
    ADD PRIMARY KEY (`id_venta`),
  ADD KEY `id_cliente` (`id_cliente`);

-- --------------------------------------------------------
-- Auto increments
-- --------------------------------------------------------

ALTER TABLE `cliente`
    MODIFY `id_cliente` int(10) UNSIGNED ZEROFILL NOT NULL AUTO_INCREMENT;

ALTER TABLE `detalle_venta`
    MODIFY `id_detalle` int(10) UNSIGNED ZEROFILL NOT NULL AUTO_INCREMENT;

ALTER TABLE `producto`
    MODIFY `id_producto` int(10) UNSIGNED ZEROFILL NOT NULL AUTO_INCREMENT;

ALTER TABLE `proveedor`
    MODIFY `id_proveedor` int(10) UNSIGNED ZEROFILL NOT NULL AUTO_INCREMENT;

ALTER TABLE `venta`
    MODIFY `id_venta` int(10) UNSIGNED ZEROFILL NOT NULL AUTO_INCREMENT;

-- --------------------------------------------------------
-- Foreign Keys
-- --------------------------------------------------------

ALTER TABLE `detalle_venta`
    ADD CONSTRAINT `detalle_venta_ibfk_1` FOREIGN KEY (`id_venta`) REFERENCES `venta` (`id_venta`),
  ADD CONSTRAINT `detalle_venta_ibfk_2` FOREIGN KEY (`id_producto`) REFERENCES `producto` (`id_producto`);

ALTER TABLE `producto`
    ADD CONSTRAINT `producto_ibfk_1` FOREIGN KEY (`id_proveedor`) REFERENCES `proveedor` (`id_proveedor`);

ALTER TABLE `venta`
    ADD CONSTRAINT `venta_ibfk_1` FOREIGN KEY (`id_cliente`) REFERENCES `cliente` (`id_cliente`);

COMMIT;
