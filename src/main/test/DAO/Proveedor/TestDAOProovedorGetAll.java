package DAO.Proveedor;

public class TestDAOProovedorGetAll {
    public static void main(String[] args) {
        org.fran.gestortienda.DAO.ProveedorDAO proveedorDAO = new org.fran.gestortienda.DAO.ProveedorDAO();

        try {
            java.util.List<org.fran.gestortienda.model.entity.Proveedor> proveedores = proveedorDAO.getAll();

            if (!proveedores.isEmpty()) {
                System.out.println("Lista de todos los proveedores:");
                for (org.fran.gestortienda.model.entity.Proveedor proveedor : proveedores) {
                    System.out.println(proveedor);
                }
            } else {
                System.out.println("No se encontraron proveedores.");
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error de base de datos al obtener todos los proveedores: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
