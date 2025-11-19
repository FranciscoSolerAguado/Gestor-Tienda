package DAO.Venta;

public class TestDAOVentaUpdate {
    public static void main(String[] args) {
        org.fran.gestortienda.DAO.VentaDAO ventaDAO = new org.fran.gestortienda.DAO.VentaDAO();

        // ID de la venta a actualizar
        int idVentaAActualizar = 3; // Cambia este ID por el que quieras actualizar

        // Asumimos que ya existe un Cliente con id_cliente = 1
        org.fran.gestortienda.model.entity.Cliente clienteExistente = new org.fran.gestortienda.model.entity.Cliente();
        clienteExistente.setId_cliente(2);

        // Creamos un objeto Venta con los nuevos datos
        org.fran.gestortienda.model.entity.Venta ventaActualizada = new org.fran.gestortienda.model.entity.Venta(
                idVentaAActualizar,
                new java.util.Date(), // Nueva fecha (o la misma si no cambia)
                200.50, // Nuevo total
                clienteExistente
        );

        try {
            if (ventaDAO.update(ventaActualizada)) {
                System.out.println("Venta con ID " + idVentaAActualizar + " actualizada con éxito.");
            } else {
                System.out.println("No se pudo actualizar la venta con ID " + idVentaAActualizar + ". Puede que no exista.");
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error de base de datos al actualizar la venta: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
