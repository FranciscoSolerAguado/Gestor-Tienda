package org.fran.gestortienda.DAO;

import org.fran.gestortienda.Connection.MySQLConnection;
import org.fran.gestortienda.model.CRUD;
import org.fran.gestortienda.model.entity.Cliente;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ClienteDAO extends Cliente implements CRUD<Cliente> {
    private final static String INSERT = "INSERT INTO cliente(nombre, telefono, direccion) VALUES(?, ?, ?)";

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
    public boolean add(Cliente cliente) throws SQLException {
        ClienteDAO clienteDAO = new ClienteDAO(cliente);
        return clienteDAO.save();
    }

    @Override
    public boolean remove() throws SQLException {
        return false;
    }

    @Override
    public boolean delete(Cliente objeto) throws SQLException {
        return false;
    }

    @Override
    public boolean update() throws SQLException {
        return false;
    }

    @Override
    public boolean update(Cliente objeto) throws SQLException {
        return false;
    }

    @Override
    public List<Cliente> getAll() throws SQLException {
        return List.of();
    }

    @Override
    public Cliente getById(int id) throws SQLException {
        return null;
    }
}
