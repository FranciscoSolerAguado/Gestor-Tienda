package DAO.Detalle_Venta;

public class TestDAODetalle_VentaUpdate {
    public static void main(String[] args) {
        org.fran.gestortienda.DAO.Detalle_VentaDAO detalleVentaDAO = new org.fran.gestortienda.DAO.Detalle_VentaDAO();

        // ID del detalle de venta a actualizar
        int idDetalleAActualizar = 2; // Cambia este ID por el que quieras actualizar

        // Asumimos que ya existe una Venta con id_venta = 1
        org.fran.gestortienda.model.entity.Venta ventaExistente = new org.fran.gestortienda.model.entity.Venta();
        ventaExistente.setId_venta(3);

        // Asumimos que ya existe un Producto con id_producto = 1
        org.fran.gestortienda.model.entity.Producto productoExistente = new org.fran.gestortienda.model.entity.Producto();
        productoExistente.setId_producto(10);

        // Creamos un objeto Detalle_Venta con los nuevos datos
        org.fran.gestortienda.model.entity.Detalle_Venta detalleActualizado = new org.fran.gestortienda.model.entity.Detalle_Venta(
                idDetalleAActualizar,
                ventaExistente,
                productoExistente,
                3, // Nueva cantidad
                0.10, // Nuevo descuento
                12.00, // Nuevo precio unitario
                0.21,
                32.40 // Nuevo subtotal (3 * 12.00 * (1 - 0.10) * (1 + 0.21))
        );

        try {
            if (detalleVentaDAO.update(detalleActualizado)) {
                System.out.println("Detalle de venta con ID " + idDetalleAActualizar + " actualizado con éxito.");
            } else {
                System.out.println("No se pudo actualizar el detalle de venta con ID " + idDetalleAActualizar + ". Puede que no exista.");
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error de base de datos al actualizar el detalle de venta: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
