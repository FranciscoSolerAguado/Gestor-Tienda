package DAO.Proveedor;

public class TestDAOProveedorRemove {
    public static void main(String[] args) {
        org.fran.gestortienda.DAO.ProveedorDAO proveedorDAO = new org.fran.gestortienda.DAO.ProveedorDAO();

        // ID del proveedor a eliminar
        int idProveedorAEliminar = 6; // Cambia este ID por el que quieras eliminar

        // Creamos un objeto Proveedor con el ID a eliminar
        org.fran.gestortienda.model.entity.Proveedor proveedorAEliminar = new org.fran.gestortienda.model.entity.Proveedor();
        proveedorAEliminar.setId_proveedor(idProveedorAEliminar);

        try {
            if (proveedorDAO.delete(proveedorAEliminar)) {
                System.out.println("Proveedor con ID " + idProveedorAEliminar + " eliminado con éxito.");
            } else {
                System.out.println("No se pudo eliminar el proveedor con ID " + idProveedorAEliminar + ". Puede que no exista.");
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error de base de datos al eliminar el proveedor: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
