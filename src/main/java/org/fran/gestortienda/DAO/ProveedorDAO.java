package org.fran.gestortienda.DAO;

import org.fran.gestortienda.Connection.ConnectionFactory;
import org.fran.gestortienda.model.CRUD;
import org.fran.gestortienda.model.entity.Proveedor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProveedorDAO extends Proveedor implements CRUD<Proveedor> {
    private final static String INSERT = "INSERT INTO proveedor(nombre, telefono, correo) VALUES(?, ?, ?)";
    private final static String UPDATE = "UPDATE proveedor SET nombre = ?, telefono = ?, correo = ? WHERE id_proveedor = ?";
    private final static String DELETE = "DELETE FROM proveedor WHERE id_proveedor = ?";
    private final static String GET_ALL = "SELECT id_proveedor, nombre, telefono, correo FROM proveedor";
    private final static String GET_BY_ID = "SELECT id_proveedor, nombre, telefono, correo FROM proveedor WHERE id_proveedor = ?";
    private final static String GET_BY_NOMBRE = "SELECT id_proveedor, nombre, telefono, correo FROM proveedor WHERE nombre LIKE ?";
    private final static String GET_BY_CORREO = "SELECT id_proveedor, nombre, telefono, correo FROM proveedor WHERE correo LIKE ?";


    /**
     * Constructor con parametros
     */
    public ProveedorDAO(int id_proveedor, String nombre, String telefono, String correo) {
        super(id_proveedor, nombre, telefono, correo);
    }

    /**
     * Constructor vacio
     */
    public ProveedorDAO() {
        super();
    }

    /**
     * Constructor con proveedor
     * @param p el proveedor
     */
    public ProveedorDAO(Proveedor p) {
        super(p.getId_proveedor(), p.getNombre(), p.getTelefono(), p.getCorreo());
    }


    /**
     * Metodo que guarda un proveedor en la base de datos.
     * @throws SQLException
     */
    @Override
    public boolean save() throws SQLException {
        Connection conn = ConnectionFactory.getConnection();
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

    /**
     * Metodo que elimina un proveedor de la base de datos.
     * @throws SQLException
     */
    @Override
    public boolean remove() throws SQLException {
        Connection conn = ConnectionFactory.getConnection();
        if (conn != null) {
            try (PreparedStatement ps = conn.prepareStatement(DELETE)) {
                ps.setInt(1, getId_proveedor());
                return ps.executeUpdate() > 0;
            }
        }
        return false;
    }

    /**
     * Metodo que actualiza un proveedor en la base de datos.
     * @throws SQLException
     */
    @Override
    public boolean update() throws SQLException {
        Connection conn = ConnectionFactory.getConnection();
        if (conn != null) {
            try (PreparedStatement ps = conn.prepareStatement(UPDATE)) {
                ps.setString(1, getNombre());
                ps.setString(2, getTelefono());
                ps.setString(3, getCorreo());
                ps.setInt(4, getId_proveedor()); // Cláusula WHERE
                return ps.executeUpdate() > 0;
            }
        }
        return false;
    }

    /**
     * Guarda un proveedor en la base de datos.
     * @param proveedor el proveedor a guardar
     * @throws SQLException
     */
    @Override
    public boolean add(Proveedor proveedor) throws SQLException {
        ProveedorDAO proveedorDAO = new ProveedorDAO(proveedor);
        return proveedorDAO.save();
    }

    /**
     * Elimina un proveedor de la base de datos.
     * @param proveedor el proveedor a eliminar
     * @throws SQLException
     */
    @Override
    public boolean delete(Proveedor proveedor) throws SQLException {
        if (proveedor == null || proveedor.getId_proveedor() == 0) {
            return false;
        }
        ProveedorDAO proveedorDAO = new ProveedorDAO(proveedor);
        return proveedorDAO.remove();
    }

    /**
     * Actualiza un proveedor en la base de datos.
     * @param proveedor el proveedor a actualizar
     * @throws SQLException
     */
    @Override
    public boolean update(Proveedor proveedor) throws SQLException {
        if (proveedor == null || proveedor.getId_proveedor() == 0) {
            return false;
        }
        ProveedorDAO proveedorDAO = new ProveedorDAO(proveedor);
        return proveedorDAO.update();
    }

    /**
     * Obtiene todos los proveedores de la base de datos.
     * @throws SQLException
     */
    @Override
    public List<Proveedor> getAll() throws SQLException {
        List<Proveedor> proveedores = new ArrayList<>();
        Connection conn = ConnectionFactory.getConnection();
        if (conn != null) {
            try (PreparedStatement ps = conn.prepareStatement(GET_ALL);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Proveedor proveedor = new Proveedor(
                            rs.getInt("id_proveedor"),
                            rs.getString("nombre"),
                            rs.getString("telefono"),
                            rs.getString("correo")
                    );
                    proveedores.add(proveedor);
                }
            }
        }
        return proveedores;
    }

    /**
     * Obtiene un proveedor por su ID.
     * @param id el ID del proveedor
     * @throws SQLException
     */
    @Override
    public Proveedor getById(int id) throws SQLException {
        Proveedor proveedor = null;
        Connection conn = ConnectionFactory.getConnection();
        if (conn != null) {
            try (PreparedStatement ps = conn.prepareStatement(GET_BY_ID)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        proveedor = new Proveedor(
                                rs.getInt("id_proveedor"),
                                rs.getString("nombre"),
                                rs.getString("telefono"),
                                rs.getString("correo")
                        );
                    }
                }
            }
        }
        return proveedor;
    }

    /**
     * Busca proveedores cuyo nombre contenga el texto proporcionado.
     * @param nombre El texto a buscar en el nombre.
     * @throws SQLException
     */
    public List<Proveedor> findByNombre(String nombre) throws SQLException {
        List<Proveedor> proveedores = new ArrayList<>();
        Connection conn = ConnectionFactory.getConnection();
        if (conn != null) {
            try (PreparedStatement ps = conn.prepareStatement(GET_BY_NOMBRE)) {
                ps.setString(1, "%" + nombre + "%");
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        proveedores.add(new Proveedor(
                                rs.getInt("id_proveedor"),
                                rs.getString("nombre"),
                                rs.getString("telefono"),
                                rs.getString("correo")
                        ));
                    }
                }
            }
        }
        return proveedores;
    }

    /**
     * Busca proveedores cuyo correo contenga el texto proporcionado.
     * @param correo El texto a buscar en el correo.
     * @throws SQLException
     */
    public List<Proveedor> findByCorreo(String correo) throws SQLException {
        List<Proveedor> proveedores = new ArrayList<>();
        Connection conn = ConnectionFactory.getConnection();
        if (conn != null) {
            try (PreparedStatement ps = conn.prepareStatement(GET_BY_CORREO)) {
                ps.setString(1, "%" + correo + "%");
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        proveedores.add(new Proveedor(
                                rs.getInt("id_proveedor"),
                                rs.getString("nombre"),
                                rs.getString("telefono"),
                                rs.getString("correo")
                        ));
                    }
                }
            }
        }
        return proveedores;
    }
}
