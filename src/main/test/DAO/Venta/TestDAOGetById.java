package DAO.Venta;

public class TestDAOGetById {
    public static void main(String[] args) {
        org.fran.gestortienda.DAO.VentaDAO ventaDAO = new org.fran.gestortienda.DAO.VentaDAO();
        int idVentaABuscar = 3; // Cambia este ID por el que quieras buscar

        try {
            org.fran.gestortienda.model.entity.Venta venta = ventaDAO.getById(idVentaABuscar);

            if (venta != null) {
                System.out.println("Venta encontrada:");
                System.out.println(venta);
            } else {
                System.out.println("No se encontró ninguna venta con el ID: " + idVentaABuscar);
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error de base de datos al obtener la venta por ID: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
