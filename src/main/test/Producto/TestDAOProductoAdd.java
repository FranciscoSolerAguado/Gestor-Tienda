package Producto;

import org.fran.gestortienda.DAO.ProductoDAO;
import org.fran.gestortienda.model.Categoria;
import org.fran.gestortienda.model.entity.Producto;
import org.fran.gestortienda.model.entity.Proveedor;

import java.sql.SQLException;

public class TestDAOProductoAdd {
    public static void main(String[] args) {
        // 1. Crea un objeto Proveedor para representar a uno que YA EXISTE en la BBDD.
        // Asumimos que tienes un proveedor con ID=1 en tu tabla 'proveedor'.
        Proveedor proveedorExistente = new Proveedor();
        proveedorExistente.setId_proveedor(1); // <-- ¡Este es el paso clave!

        // 2. Ahora crea el Producto usando el objeto Proveedor que acabas de configurar.
        Producto producto = new Producto("Fanta limon", Categoria.BEBIDAS, 1.99, 5,
                proveedorExistente, "org/fran/gestortienda/img/fanta.jpg");

        ProductoDAO productoDAO = new ProductoDAO();

        try {
            if (productoDAO.add(producto)) {
                System.out.println("Producto guardado con éxito.");
            } else {
                System.out.println("Error al guardar el producto.");
            }
        } catch (SQLException e) {
            System.err.println("Error de base de datos al guardar el producto: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
