package DAO.Proveedor;

public class TestDAOProovedorGetById {
    public static void main(String[] args) {
        org.fran.gestortienda.DAO.ProveedorDAO proveedorDAO = new org.fran.gestortienda.DAO.ProveedorDAO();
        int idProveedorABuscar = 3; // Cambia este ID por el que quieras buscar

        try {
            org.fran.gestortienda.model.entity.Proveedor proveedor = proveedorDAO.getById(idProveedorABuscar);

            if (proveedor != null) {
                System.out.println("Proveedor encontrado:");
                System.out.println(proveedor);
            } else {
                System.out.println("No se encontró ningún proveedor con el ID: " + idProveedorABuscar);
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error de base de datos al obtener el proveedor por ID: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
