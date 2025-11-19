package Cliente;

import org.fran.gestortienda.DAO.ClienteDAO;
import org.fran.gestortienda.model.entity.Cliente;

public class TestDAOClienteAdd {
    public static void main(String[] args) {
        ClienteDAO clienteDAO = new ClienteDAO();

Cliente nuevoCliente = new org.fran.gestortienda.model.entity.Cliente(
                0, // El ID se autoincrementará en la base de datos
                "Juan Pérez",
                "654321987",
                "juan.perez@example.com"
        );

        try {
            if (clienteDAO.add(nuevoCliente)) {
                System.out.println("Cliente añadido con éxito: " + nuevoCliente.getNombre());
            } else {
                System.out.println("No se pudo añadir el cliente.");
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error de base de datos al añadir el cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
