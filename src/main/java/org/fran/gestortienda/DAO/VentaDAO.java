package org.fran.gestortienda.DAO;

import org.fran.gestortienda.Connection.ConnectionFactory;
import org.fran.gestortienda.model.CRUD;
import org.fran.gestortienda.model.entity.Venta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VentaDAO extends Venta implements CRUD<Venta> {


    private final static String INSERT = "INSERT INTO venta(fecha, total, id_cliente) VALUES(?, ?, ?)";
    private final static String UPDATE = "UPDATE venta SET fecha = ?, total = ?, id_cliente = ? WHERE id_venta = ?";
    private final static String DELETE = "DELETE FROM venta WHERE id_venta = ?";
    private final static String GET_ALL = "SELECT id_venta, fecha, total, id_cliente FROM venta";
    private final static String GET_BY_ID = "SELECT id_venta, fecha, total, id_cliente FROM venta WHERE id_venta = ?";
    private final static String GET_BY_FECHA = "SELECT id_venta, fecha, total, id_cliente FROM venta WHERE fecha = ?";
    private static final String GET_BY_CLIENTE = "SELECT id_venta, fecha, total, id_cliente FROM venta WHERE id_cliente = ?";
    private static final String GET_BY_TOTAL = "SELECT id_venta, fecha, total, id_cliente FROM venta WHERE total = ?";
    private final static String GET_LAST_BY_CLIENTE = "SELECT * FROM venta WHERE id_cliente = ? ORDER BY id_venta DESC LIMIT 1";

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
        Connection conn = ConnectionFactory.getConnection();
        if (conn != null) {
            try (PreparedStatement ps = conn.prepareStatement(INSERT)) {
                ps.setDate(1, new java.sql.Date(getFecha().getTime()));
                ps.setDouble(2, getTotal());

                // Manejo de cliente nulo (lazy loading)
                if (getCliente() != null && getCliente().getId_cliente() > 0) {
                    ps.setInt(3, getCliente().getId_cliente());
                } else {
                    ps.setNull(3, Types.INTEGER);
                }

                return ps.executeUpdate() > 0;
            }
        }
        return false;
    }

    public Venta addVenta(Venta venta) throws SQLException {
        Connection conn = ConnectionFactory.getConnection();
        if (conn != null) {
            try (PreparedStatement ps = conn.prepareStatement(INSERT, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                ps.setObject(1, venta.getFecha());
                ps.setDouble(2, venta.getTotal());
                ps.setInt(3, venta.getCliente().getId_cliente());

                int affectedRows = ps.executeUpdate();

                if (affectedRows > 0) {
                    try (java.sql.ResultSet generatedKeys = ps.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            venta.setId_venta(generatedKeys.getInt(1));
                            return venta;
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public boolean remove() throws SQLException {
        Connection conn = ConnectionFactory.getConnection();
        if (conn != null) {
            try (PreparedStatement ps = conn.prepareStatement(DELETE)) {
                ps.setInt(1, getId_venta());
                return ps.executeUpdate() > 0;
            }
        }
        return false;
    }

    @Override
    public boolean update() throws SQLException {
        Connection conn = ConnectionFactory.getConnection();
        if (conn != null) {
            try (PreparedStatement ps = conn.prepareStatement(UPDATE)) {
                ps.setDate(1, new java.sql.Date(getFecha().getTime()));
                ps.setDouble(2, getTotal());

                if (getCliente() != null && getCliente().getId_cliente() > 0) {
                    ps.setInt(3, getCliente().getId_cliente());
                } else {
                    ps.setNull(3, Types.INTEGER);
                }

                ps.setInt(4, getId_venta()); // Cláusula WHERE
                return ps.executeUpdate() > 0;
            }
        }
        return false;
    }



    @Override
    public boolean add(Venta venta) throws SQLException {
        VentaDAO ventaDAO = new VentaDAO(venta);
        return ventaDAO.save();
    }

    @Override
    public boolean delete(Venta venta) throws SQLException {
        if (venta == null || venta.getId_venta() == 0) {
            return false;
        }
        VentaDAO ventaDAO = new VentaDAO(venta);
        return ventaDAO.remove();
    }

    @Override
    public boolean update(Venta venta) throws SQLException {
        if (venta == null || venta.getId_venta() == 0) {
            return false;
        }
        VentaDAO ventaDAO = new VentaDAO(venta);
        return ventaDAO.update();
    }

    @Override
    public List<Venta> getAll() throws SQLException {
        List<Venta> ventas = new ArrayList<>();
        Connection conn = ConnectionFactory.getConnection();
        if (conn != null) {
            try (PreparedStatement ps = conn.prepareStatement(GET_ALL);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Venta venta = new Venta(
                            rs.getInt("id_venta"),
                            rs.getDate("fecha"),
                            rs.getDouble("total"),
                            null
                    );
                    ventas.add(venta);
                }
            }
        }
        return ventas;
    }

    @Override
    public Venta getById(int id) throws SQLException {
        Venta venta = null;
        Connection conn = ConnectionFactory.getConnection();
        if (conn != null) {
            try (PreparedStatement ps = conn.prepareStatement(GET_BY_ID)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        venta = new Venta(
                                rs.getInt("id_venta"),
                                rs.getDate("fecha"),
                                rs.getDouble("total"),
                                null
                        );
                    }
                }
            }
        }
        return venta;
    }

    public List<Venta> buscarPorID(int id) throws SQLException {
        List<Venta> ventas = new ArrayList<>();

        String sql = "SELECT id_venta, fecha, total, id_cliente FROM venta WHERE id_venta = ?";

        Connection conn = ConnectionFactory.getConnection();
        if (conn != null) {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        ventas.add(new Venta(
                                rs.getInt("id_venta"),
                                rs.getDate("fecha"),
                                rs.getDouble("total"),
                                null
                        ));
                    }
                }
            }
        }
        return ventas;
    }


    public List<Venta> findByFecha(Date fecha) throws SQLException {
        List<Venta> ventas = new ArrayList<>();

        Connection conn = ConnectionFactory.getConnection();

        if (conn != null) {
            try (PreparedStatement ps = conn.prepareStatement(GET_BY_FECHA)) {
                ps.setDate(1, new java.sql.Date(fecha.getTime()));

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        ventas.add(new Venta(
                                rs.getInt("id_venta"),
                                rs.getDate("fecha"),
                                rs.getDouble("total"),
                                null
                        ));
                    }
                }
            }
        }

        return ventas;
    }

    public List<Venta> findByCliente(int idCliente) throws SQLException {
        List<Venta> ventas = new ArrayList<>();

        Connection conn = ConnectionFactory.getConnection();
        if (conn != null) {
            try (PreparedStatement ps = conn.prepareStatement(GET_BY_CLIENTE)) {
                ps.setInt(1, idCliente);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        ventas.add(new Venta(
                                rs.getInt("id_venta"),
                                rs.getDate("fecha"),
                                rs.getDouble("total"),
                                null
                        ));
                    }
                }
            }
        }
        return ventas;
    }

    public List<Venta> findByTotal(double total) throws SQLException {
        List<Venta> lista = new ArrayList<>();
        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(GET_BY_TOTAL)) {
            ps.setDouble(1, total);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(new Venta(
                        rs.getInt("id_venta"),
                        rs.getDate("fecha"),
                        rs.getDouble("total"),
                        null
                ));
            }
        }

        return lista;
    }

    public Venta getLastByCliente(int idCliente) throws SQLException {
        Venta venta = null;
        Connection conn = ConnectionFactory.getConnection();
        if (conn != null) {
            try (PreparedStatement ps = conn.prepareStatement(GET_LAST_BY_CLIENTE)) {
                ps.setInt(1, idCliente);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        venta = new Venta(
                                rs.getInt("id_venta"),
                                rs.getDate("fecha"),
                                rs.getDouble("total"),
                                null
                        );
                    }
                }
            }
        }
        return venta;
    }
}
