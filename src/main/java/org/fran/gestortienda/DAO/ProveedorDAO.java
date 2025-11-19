package org.fran.gestortienda.DAO;

import org.fran.gestortienda.Connection.MySQLConnection;
import org.fran.gestortienda.model.CRUD;
import org.fran.gestortienda.model.entity.Proveedor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ProveedorDAO extends Proveedor implements CRUD<Proveedor> {
    private final static String INSERT = "INSERT INTO proveedor(nombre, telefono, correo) VALUES(?, ?, ?)";

    public ProveedorDAO(int id_proveedor, String nombre, String telefono, String correo) {
        super(id_proveedor, nombre, telefono, correo);
    }

    public ProveedorDAO() {
        super();
    }

    public ProveedorDAO(Proveedor p) {
        super(p.getId_proveedor(), p.getNombre(), p.getTelefono(), p.getCorreo());
    }

    @Override
    public boolean save() throws SQLException {
        Connection conn = MySQLConnection.getConnection();
        if (conn != null) {
            try (PreparedStatement ps = conn.prepareStatement(INSERT)) {
                ps.setString(1, getNombre());
                ps.setString(2, getTelefono());
                ps.setString(3, getCorreo());
                return ps.executeUpdate() > 0;
            }
        }
        return false;
    }

    @Override
    public boolean add(Proveedor proveedor) throws SQLException {
        ProveedorDAO proveedorDAO = new ProveedorDAO(proveedor);
        return proveedorDAO.save();
    }

    @Override
    public boolean remove() throws SQLException {
        return false;
    }

    @Override
    public boolean delete(Proveedor proveedor) throws SQLException {
        return false;
    }

    @Override
    public boolean update() throws SQLException {
        return false;
    }

    @Override
    public boolean update(Proveedor proveedor) throws SQLException {
        return false;
    }

    @Override
    public List getAll() throws SQLException {
        return List.of();
    }

    @Override
    public Proveedor getById(int id) throws SQLException {
        return null;
    }
}
