package org.fran.gestortienda.DAO;

import org.fran.gestortienda.Connection.MySQLConnection;
import org.fran.gestortienda.model.CRUD;
import org.fran.gestortienda.model.entity.Venta;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

public class VentaDAO extends Venta implements CRUD<Venta> {
    private final static String INSERT = "INSERT INTO venta(fecha, total, id_cliente) VALUES(?, ?, ?)";

    public VentaDAO(int id_venta, java.util.Date fecha, Double total, org.fran.gestortienda.model.entity.Cliente cliente) {
        super(id_venta, fecha, total, cliente);
    }

    public VentaDAO() {
        super();
    }

    public VentaDAO(Venta v) {
        super(v.getId_venta(), v.getFecha(), v.getTotal(), v.getCliente());
    }

    @Override
    public boolean save() throws SQLException {
        Connection conn = MySQLConnection.getConnection();
        if (conn != null) {
            try (java.sql.PreparedStatement ps = conn.prepareStatement(INSERT)) {
                ps.setDate(1, new java.sql.Date(getFecha().getTime()));
                ps.setDouble(2, getTotal());
                ps.setInt(3, getCliente().getId_cliente());

                return ps.executeUpdate() > 0;
            }
        }
        return false;
    }

    @Override
    public boolean add(Venta venta) throws SQLException {
        VentaDAO ventaDAO = new VentaDAO(venta);
        return  ventaDAO.save();
    }

    @Override
    public boolean remove() throws SQLException {
        return false;
    }

    @Override
    public boolean delete(Venta objeto) throws SQLException {
        return false;
    }

    @Override
    public boolean update() throws SQLException {
        return false;
    }

    @Override
    public boolean update(Venta objeto) throws SQLException {
        return false;
    }

    @Override
    public List<Venta> getAll() throws SQLException {
        return List.of();
    }

    @Override
    public Venta getById(int id) throws SQLException {
        return null;
    }
}
