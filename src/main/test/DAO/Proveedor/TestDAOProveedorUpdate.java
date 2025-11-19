package DAO.Proveedor;

public class TestDAOProveedorUpdate {
    public static void main(String[] args) {
        org.fran.gestortienda.DAO.ProveedorDAO proveedorDAO = new org.fran.gestortienda.DAO.ProveedorDAO();

        // ID del proveedor a actualizar
        int idProveedorAActualizar = 6; // Cambia este ID por el que quieras actualizar

        // Creamos un objeto Proveedor con los nuevos datos
        org.fran.gestortienda.model.entity.Proveedor proveedorActualizado = new org.fran.gestortienda.model.entity.Proveedor(
                idProveedorAActualizar,
                "Proveedor Actualizado S.L.",
                "987654321",
                "contacto_actualizado@ejemplo.com"
        );

        try {
            if (proveedorDAO.update(proveedorActualizado)) {
                System.out.println("Proveedor con ID " + idProveedorAActualizar + " actualizado con éxito.");
            } else {
                System.out.println("No se pudo actualizar el proveedor con ID " + idProveedorAActualizar + ". Puede que no exista.");
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error de base de datos al actualizar el proveedor: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
