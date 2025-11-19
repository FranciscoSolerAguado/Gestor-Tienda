package DAO.Venta;

public class TestDAOVentaGetAll {
    public static void main(String[] args) {
        org.fran.gestortienda.DAO.VentaDAO ventaDAO = new org.fran.gestortienda.DAO.VentaDAO();

        try {
            java.util.List<org.fran.gestortienda.model.entity.Venta> ventas = ventaDAO.getAll();

            if (!ventas.isEmpty()) {
                System.out.println("Lista de todas las ventas:");
                for (org.fran.gestortienda.model.entity.Venta venta : ventas) {
                    System.out.println(venta);
                }
            } else {
                System.out.println("No se encontraron ventas.");
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error de base de datos al obtener todas las ventas: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
