package DAO.Venta;

public class TestDAOVentaRemove {
    public static void main(String[] args) {
        org.fran.gestortienda.DAO.VentaDAO ventaDAO = new org.fran.gestortienda.DAO.VentaDAO();

        // ID de la venta a eliminar
        int idVentaAEliminar = 2; // Cambia este ID por el que quieras eliminar

        // Creamos un objeto Venta con el ID a eliminar
        org.fran.gestortienda.model.entity.Venta ventaAEliminar = new org.fran.gestortienda.model.entity.Venta();
        ventaAEliminar.setId_venta(idVentaAEliminar);

        try {
            if (ventaDAO.delete(ventaAEliminar)) {
                System.out.println("Venta con ID " + idVentaAEliminar + " eliminada con éxito.");
            } else {
                System.out.println("No se pudo eliminar la venta con ID " + idVentaAEliminar + ". Puede que no exista.");
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error de base de datos al eliminar la venta: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
