package DAO.Cliente;

public class TestDAOClienteRemove {
    public static void main(String[] args) {
        org.fran.gestortienda.DAO.ClienteDAO clienteDAO = new org.fran.gestortienda.DAO.ClienteDAO();

        // ID del cliente a eliminar
        int idClienteAEliminar = 1; // Cambia este ID por el que quieras eliminar

        // Creamos un objeto Cliente con el ID a eliminar
        org.fran.gestortienda.model.entity.Cliente clienteAEliminar = new org.fran.gestortienda.model.entity.Cliente();
        clienteAEliminar.setId_cliente(idClienteAEliminar);

        try {
            if (clienteDAO.delete(clienteAEliminar)) {
                System.out.println("Cliente con ID " + idClienteAEliminar + " eliminado con éxito.");
            } else {
                System.out.println("No se pudo eliminar el cliente con ID " + idClienteAEliminar + ". Puede que no exista.");
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error de base de datos al eliminar el cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
