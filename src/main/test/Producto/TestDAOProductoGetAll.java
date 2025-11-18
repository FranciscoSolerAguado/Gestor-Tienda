package Producto;

import org.fran.gestortienda.DAO.ProductoDAO;
import org.fran.gestortienda.model.entity.Producto;

import java.sql.SQLException;
import java.util.List;

public class TestDAOProductoGetAll {
    public static void main(String[] args) {
        ProductoDAO productoDAO = new ProductoDAO();

        try {
            List<Producto> productos = productoDAO.getAll();

            if (!productos.isEmpty()) {
                System.out.println("Lista de todos los productos:");
                for (Producto p : productos) {
                    System.out.println(p);
                }
            } else {
                System.out.println("No se encontraron productos.");
            }
        } catch (SQLException e) {
            System.err.println("Error de base de datos al obtener todos los productos: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

