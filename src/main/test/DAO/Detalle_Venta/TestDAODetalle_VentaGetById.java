package DAO.Detalle_Venta;

public class TestDAODetalle_VentaGetById {
    public static void main(String[] args) {
        org.fran.gestortienda.DAO.Detalle_VentaDAO detalleVentaDAO = new org.fran.gestortienda.DAO.Detalle_VentaDAO();
        int idDetalleABuscar = 2; // Cambia este ID por el que quieras buscar

        try {
            org.fran.gestortienda.model.entity.Detalle_Venta detalle = detalleVentaDAO.getById(idDetalleABuscar);

            if (detalle != null) {
                System.out.println("Detalle de venta encontrado:");
                System.out.println(detalle);
            } else {
                System.out.println("No se encontró ningún detalle de venta con el ID: " + idDetalleABuscar);
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error de base de datos al obtener el detalle de venta por ID: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
