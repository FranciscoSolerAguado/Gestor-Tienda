package org.fran.gestortienda.DAO;

import org.fran.gestortienda.Connection.MySQLConnection;
import org.fran.gestortienda.model.CRUD;
import org.fran.gestortienda.model.entity.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO extends Cliente implements CRUD<Cliente> {
    private final static String INSERT = "INSERT INTO cliente(nombre, telefono, direccion) VALUES(?, ?, ?)";
    private final static String UPDATE = "UPDATE cliente SET nombre=?, telefono=?, direccion=? WHERE id_cliente=?";
    private final static String DELETE = "DELETE FROM cliente WHERE id_cliente = ?";
    private final static String GET_ALL = "SELECT id_cliente, nombre, telefono, direccion FROM cliente";
    private final static String GET_BY_ID = "SELECT id_cliente, nombre, telefono, direccion FROM cliente WHERE id_cliente = ?";

    public ClienteDAO(int id_cliente, String nombre, String telefono, String correo) {
        super(id_cliente, nombre, telefono, correo);
    }

    public ClienteDAO() {
        super();
    }

    public ClienteDAO(Cliente c) {
        super(c.getId_cliente(), c.getNombre(), c.getTelefono(), c.getCorreo());
    }


    @Override
    public boolean save() throws SQLException {
        Connection conn = MySQLConnection.getConnection();
        if (conn != null) {
            try (java.sql.PreparedStatement ps = conn.prepareStatement(INSERT)) {
                ps.setString(1, getNombre());
                ps.setString(2, getTelefono());
                ps.setString(3, getCorreo());
                return ps.executeUpdate() > 0;
            }
        }
        return false;
    }


    @Override
    public boolean remove() throws SQLException {
        Connection conn = MySQLConnection.getConnection();
        if (conn != null) {
            try (PreparedStatement ps = conn.prepareStatement(DELETE)) {
                ps.setInt(1, getId_cliente());
                return ps.executeUpdate() > 0;
            }
        }
        return false;
    }

    @Override
    public boolean update() throws SQLException {
        Connection conn = MySQLConnection.getConnection();
        if (conn != null) {
            try (PreparedStatement ps = conn.prepareStatement(UPDATE)) {
                ps.setString(1, getNombre());
                ps.setString(2, getTelefono());
                ps.setString(3, getCorreo());
                ps.setInt(4, getId_cliente()); // Cláusula WHERE
                return ps.executeUpdate() > 0;
            }
        }
        return false;
    }

    // --- MÉTODOS DE INTERFAZ (Delegan en los de instancia) ---

    @Override
    public boolean add(Cliente cliente) throws SQLException {
        ClienteDAO clienteDAO = new ClienteDAO(cliente);
        return clienteDAO.save();
    }

    @Override
    public boolean delete(Cliente cliente) throws SQLException {
        // Asegurarse de que el cliente tiene un ID válido
        if (cliente == null || cliente.getId_cliente() == 0) {
            return false;
        }
        ClienteDAO clienteDAO = new ClienteDAO(cliente);
        return clienteDAO.remove();
    }

    @Override
    public boolean update(Cliente cliente) throws SQLException {
        // Asegurarse de que el cliente tiene un ID válido
        if (cliente == null || cliente.getId_cliente() == 0) {
            return false;
        }
        ClienteDAO clienteDAO = new ClienteDAO(cliente);
        return clienteDAO.update();
    }

    @Override
    public List<Cliente> getAll() throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        Connection conn = MySQLConnection.getConnection();
        if (conn != null) {
            try (PreparedStatement ps = conn.prepareStatement(GET_ALL);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Cliente cliente = new Cliente(
                            rs.getInt("id_cliente"),
                            rs.getString("nombre"),
                            rs.getString("telefono"),
                            rs.getString("direccion")
                    );
                    clientes.add(cliente);
                }
            }
        }
        return clientes;
    }

    @Override
    public Cliente getById(int id) throws SQLException {
        Cliente cliente = null;
        Connection conn = MySQLConnection.getConnection();
        if (conn != null) {
            try (PreparedStatement ps = conn.prepareStatement(GET_BY_ID)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        cliente = new Cliente(
                                rs.getInt("id_cliente"),
                                rs.getString("nombre"),
                                rs.getString("telefono"),
                                rs.getString("direccion")
                        );
                    }
                }
            }
        }
        return cliente;
    }
}
