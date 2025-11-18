package Producto;

public class TestDAOProductoGetById {
    public static void main(String[] args) {
        org.fran.gestortienda.DAO.ProductoDAO productoDAO = new org.fran.gestortienda.DAO.ProductoDAO();
        int idProductoABuscar = 14; // Cambia este ID por el que quieras buscar

        try {
            org.fran.gestortienda.model.entity.Producto producto = productoDAO.getById(idProductoABuscar);

            if (producto != null) {
                System.out.println("Producto encontrado:");
                System.out.println(producto);
            } else {
                System.out.println("No se encontró ningún producto con el ID: " + idProductoABuscar);
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error de base de datos al obtener el producto por ID: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
