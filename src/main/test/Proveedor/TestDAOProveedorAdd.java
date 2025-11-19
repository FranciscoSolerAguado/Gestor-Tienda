package Proveedor;

import org.fran.gestortienda.DAO.ProveedorDAO;
import org.fran.gestortienda.model.entity.Proveedor;

public class TestDAOProveedorAdd {
    public static void main(String[] args) {
        ProveedorDAO proveedorDAO = new org.fran.gestortienda.DAO.ProveedorDAO();

        Proveedor nuevoProveedor = new org.fran.gestortienda.model.entity.Proveedor(
                0, // El ID se autoincrementará en la base de datos
                "Proveedor Ejemplo S.A.",
                "987654321",
                "contacto@ejemplo.com"
        );

        try {
            if (proveedorDAO.add(nuevoProveedor)) {
                System.out.println("Proveedor añadido con éxito: " + nuevoProveedor.getNombre());
            } else {
                System.out.println("No se pudo añadir el proveedor.");
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error de base de datos al añadir el proveedor: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
