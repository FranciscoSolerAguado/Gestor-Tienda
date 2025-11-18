package Producto;

import org.fran.gestortienda.DAO.ProductoDAO;

public class TestDAOProductoDelete {
    public static void main(String[] args) {
        ProductoDAO pdao = new ProductoDAO();
        pdao.setId_producto(15);

        try {
            if (pdao.remove()) {
                System.out.println("Producto eliminado con éxito.");
            } else {
                System.out.println("Error al eliminar el producto.");
            }
        } catch (Exception e) {

        }
    }
}
