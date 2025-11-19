package DAO.Detalle_Venta;

public class TestDAODetalle_VentaRemove {
    public static void main(String[] args) {
        org.fran.gestortienda.DAO.Detalle_VentaDAO detalleVentaDAO = new org.fran.gestortienda.DAO.Detalle_VentaDAO();

        // ID del detalle de venta a eliminar
        int idDetalleAEliminar = 2; // Cambia este ID por el que quieras eliminar

        // Creamos un objeto Detalle_Venta con el ID a eliminar
        org.fran.gestortienda.model.entity.Detalle_Venta detalleAEliminar = new org.fran.gestortienda.model.entity.Detalle_Venta();
        detalleAEliminar.setId_detalle(idDetalleAEliminar);

        try {
            if (detalleVentaDAO.delete(detalleAEliminar)) {
                System.out.println("Detalle de venta con ID " + idDetalleAEliminar + " eliminado con éxito.");
            } else {
                System.out.println("No se pudo eliminar el detalle de venta con ID " + idDetalleAEliminar + ". Puede que no exista.");
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error de base de datos al eliminar el detalle de venta: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
