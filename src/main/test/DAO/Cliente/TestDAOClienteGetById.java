package DAO.Cliente;

public class TestDAOClienteGetById {
    public static void main(String[] args) {
        org.fran.gestortienda.DAO.ClienteDAO clienteDAO = new org.fran.gestortienda.DAO.ClienteDAO();
        int idClienteABuscar = 2; // Cambia este ID por el que quieras buscar

        try {
            org.fran.gestortienda.model.entity.Cliente cliente = clienteDAO.getById(idClienteABuscar);

            if (cliente != null) {
                System.out.println("Cliente encontrado:");
                System.out.println(cliente);
            } else {
                System.out.println("No se encontró ningún cliente con el ID: " + idClienteABuscar);
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error de base de datos al obtener el cliente por ID: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
