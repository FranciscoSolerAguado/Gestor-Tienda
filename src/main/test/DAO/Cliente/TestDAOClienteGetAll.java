package DAO.Cliente;

public class TestDAOClienteGetAll {
    public static void main(String[] args) {
        org.fran.gestortienda.DAO.ClienteDAO clienteDAO = new org.fran.gestortienda.DAO.ClienteDAO();

        try {
            java.util.List<org.fran.gestortienda.model.entity.Cliente> clientes = clienteDAO.getAll();

            if (!clientes.isEmpty()) {
                System.out.println("Lista de todos los clientes:");
                for (org.fran.gestortienda.model.entity.Cliente cliente : clientes) {
                    System.out.println(cliente);
                }
            } else {
                System.out.println("No se encontraron clientes.");
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error de base de datos al obtener todos los clientes: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
