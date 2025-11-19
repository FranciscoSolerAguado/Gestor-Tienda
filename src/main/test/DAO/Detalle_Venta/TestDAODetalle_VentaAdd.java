package DAO.Detalle_Venta;

import org.fran.gestortienda.DAO.Detalle_VentaDAO;
import org.fran.gestortienda.model.entity.Detalle_Venta;
import org.fran.gestortienda.model.entity.Producto;
import org.fran.gestortienda.model.entity.Venta;

import java.sql.SQLException;

/**
 * No funciona
 */
public class TestDAODetalle_VentaAdd {
    public static void main(String[] args) {
        // Asumimos que ya existe una Venta con id_venta = 1
        Venta ventaExistente = new Venta();
        ventaExistente.setId_venta(3);

        // Asumimos que ya existe un Producto con id_producto = 1
        Producto productoExistente = new Producto();
        productoExistente.setId_producto(9);

        Detalle_Venta nuevoDetalle = new Detalle_Venta(
                0, // El ID se autoincrementará
                ventaExistente,
                productoExistente,
                2, // Cantidad
                0,
                10.50, // Precio unitario
                0.21,
                150.75 // Subtotal
        );

        Detalle_VentaDAO detalleVentaDAO = new Detalle_VentaDAO();

        try {
            if (detalleVentaDAO.add(nuevoDetalle)) {
                System.out.println("Detalle de venta añadido con éxito.");
            } else {
                System.out.println("No se pudo añadir el detalle de venta.");
            }
        } catch (SQLException e) {
            System.err.println("Error de base de datos al añadir el detalle de venta: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
