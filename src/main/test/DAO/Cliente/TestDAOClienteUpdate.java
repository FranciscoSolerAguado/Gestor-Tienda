package DAO.Cliente;

public class TestDAOClienteUpdate {
    public static void main(String[] args) {
        org.fran.gestortienda.DAO.ClienteDAO clienteDAO = new org.fran.gestortienda.DAO.ClienteDAO();

        // ID del cliente a actualizar
        int idClienteAActualizar = 2; // Cambia este ID por el que quieras actualizar

        // Creamos un objeto Cliente con los nuevos datos
        org.fran.gestortienda.model.entity.Cliente clienteActualizado = new org.fran.gestortienda.model.entity.Cliente(
                idClienteAActualizar,
                "Juan Pérez Actualizado",
                "666555444",
                "juan.actualizado@example.com"
        );

        try {
            if (clienteDAO.update(clienteActualizado)) {
                System.out.println("Cliente con ID " + idClienteAActualizar + " actualizado con éxito.");
            } else {
                System.out.println("No se pudo actualizar el cliente con ID " + idClienteAActualizar + ". Puede que no exista.");
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error de base de datos al actualizar el cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
