package DAO.Detalle_Venta;

public class TestDAODetalle_VentaGetAll {
    public static void main(String[] args) {
        org.fran.gestortienda.DAO.Detalle_VentaDAO detalleVentaDAO = new org.fran.gestortienda.DAO.Detalle_VentaDAO();

        try {
            java.util.List<org.fran.gestortienda.model.entity.Detalle_Venta> detalles = detalleVentaDAO.getAll();

            if (!detalles.isEmpty()) {
                System.out.println("Lista de todos los detalles de venta:");
                for (org.fran.gestortienda.model.entity.Detalle_Venta detalle : detalles) {
                    System.out.println(detalle);
                }
            } else {
                System.out.println("No se encontraron detalles de venta.");
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error de base de datos al obtener todos los detalles de venta: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
