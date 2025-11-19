package DAO.Venta;

import org.fran.gestortienda.DAO.VentaDAO;
import org.fran.gestortienda.model.entity.Cliente;
import org.fran.gestortienda.model.entity.Venta;

public class TestDAOVentaAdd {
    public static void main(String[] args) {
        VentaDAO ventaDAO = new VentaDAO();

        Cliente clienteExistente = new Cliente();
        clienteExistente.setId_cliente(2); // <-- ¡Este es el paso clave!

        Venta nuevaVenta = new Venta(
                0, // El ID se autoincrementará
                new java.util.Date(), // Fecha actual
                150.75, // Total de la venta
                clienteExistente
        );

        try {
            if (ventaDAO.add(nuevaVenta)) {
                System.out.println("Venta añadida con éxito para el cliente: " + nuevaVenta.getCliente().getNombre());
            } else {
                System.out.println("No se pudo añadir la venta.");
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error de base de datos al añadir la venta: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
