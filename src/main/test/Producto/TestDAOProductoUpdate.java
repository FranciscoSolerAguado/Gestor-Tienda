package Producto;

import org.fran.gestortienda.DAO.ProductoDAO;
import org.fran.gestortienda.model.Categoria;
import org.fran.gestortienda.model.entity.Producto;
import org.fran.gestortienda.model.entity.Proveedor;

import java.sql.SQLException;

public class TestDAOProductoUpdate {
    public static void main(String[] args) {
        // 1. Crea un objeto Proveedor para representar a uno que YA EXISTE en la BBDD.
        Proveedor proveedorExistente = new Proveedor();
        proveedorExistente.setId_proveedor(1); // Asegúrate de que este ID exista en tu base de datos

        // 2. Crea un objeto Producto con los datos actualizados, incluyendo el ID del producto a modificar.
        Producto productoAActualizar = new Producto(
                14, // ID del producto que quieres actualizar
                "Fanta Naranja (Actualizado)",
                Categoria.BEBIDAS,
                2.50,
                100,
                proveedorExistente,
                "org/fran/gestortienda/img/fanta_naranja_updated.jpg"
        );

        ProductoDAO productoDAO = new ProductoDAO();

        try {
            if (productoDAO.update(productoAActualizar)) {
                System.out.println("Producto actualizado con éxito.");
            } else {
                System.out.println("Error al actualizar el producto. Asegúrate de que el ID del producto exista.");
            }
        } catch (SQLException e) {
            System.err.println("Error de base de datos al actualizar el producto: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
