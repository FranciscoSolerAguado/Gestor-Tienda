package org.fran.gestortienda.DAO;

import org.fran.gestortienda.Connection.ConnectionFactory;
import org.fran.gestortienda.model.CRUD;
import org.fran.gestortienda.model.Categoria;
import org.fran.gestortienda.model.entity.Producto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO extends Producto implements CRUD<Producto> {
    private final static String INSERT = "INSERT INTO producto(nombre, categoria, precio, stock, id_proveedor, imagen) VALUES(?, ?, ?, ?, ?, ?)";
    private final static String DELETE = "DELETE FROM producto WHERE id_producto = ?";
    private final static String UPDATE = "UPDATE producto SET nombre=?, categoria=?, precio=?, stock=?, id_proveedor=?, imagen=? WHERE id_producto=?";
    private final static String GET_ALL = "SELECT * FROM producto";
    private final static String GET_BY_ID = "SELECT * FROM producto WHERE id_producto = ?";


    public ProductoDAO(int id_producto, String nombre, org.fran.gestortienda.model.Categoria categoria, double precio, int stock, org.fran.gestortienda.model.entity.Proveedor proveedor, String imagen) {
        super(id_producto, nombre, categoria, precio, stock, proveedor, imagen);
    }

    public ProductoDAO() {
        super();
    }

    public ProductoDAO(Producto p) {
        super(p.getId_producto(), p.getNombre(), p.getCategoria(), p.getPrecio(), p.getStock(), p.getProveedor(), p.getImagen());
    }

    public boolean save() throws SQLException {
        Connection conn = ConnectionFactory.getConnection();
        // Corregido: La condición debe ser conn != null
        if (conn != null) {
            try (PreparedStatement ps = conn.prepareStatement(INSERT)) {
                ps.setString(1, getNombre());
                ps.setString(2, getCategoria().name());
                ps.setDouble(3, getPrecio());
                ps.setInt(4, getStock());
                ps.setInt(5, getProveedor().getId_proveedor());
                ps.setString(6, getImagen());
                return ps.executeUpdate() > 0;

            }
        } else {
            return false;
        }
    }

    @Override
    public boolean remove() throws SQLException {
        Connection conn = ConnectionFactory.getConnection();
        if (conn != null) {
            try (PreparedStatement ps = conn.prepareStatement(DELETE)) {
                ps.setInt(1, getId_producto());
                //el boolean es esto, si devuelve un numero mayor que cero comprueba que lo ha borrado
                return ps.executeUpdate() > 0;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean update() throws SQLException {
        boolean actualizado = false;
        Connection conn = ConnectionFactory.getConnection();
        if (conn != null) {
            try (PreparedStatement ps = conn.prepareStatement(UPDATE)) {
                // Establecer los valores para los campos a actualizar
                ps.setString(1, getNombre());
                ps.setString(2, getCategoria().name());
                ps.setDouble(3, getPrecio());
                ps.setInt(4, getStock());
                ps.setInt(5, getProveedor().getId_proveedor());
                ps.setString(6, getImagen());

                // Establecer el ID para la cláusula WHERE
                ps.setInt(7, getId_producto());

                if (ps.executeUpdate() > 0) {
                    actualizado = true;
                }
            }
        }
        return actualizado;
    }

    @Override
    public List<Producto> getAll() throws SQLException {
        List<Producto> productos = new ArrayList<>();
        Connection conn = ConnectionFactory.getConnection();
        if (conn != null) {
            try (PreparedStatement ps = conn.prepareStatement(GET_ALL);
                 java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // 1. Obtén el nombre de la categoría como un String.
                    String categoriaString = rs.getString("categoria");

                    // 2. Convierte el String al enum Categoria.
                    Categoria categoria = Categoria.valueOf(categoriaString.toUpperCase());


                    // 4. Crea el Producto usando los objetos correctos (categoria y proveedor).
                    Producto producto = new Producto(
                            rs.getInt("id_producto"),
                            rs.getString("nombre"),
                            categoria,
                            rs.getDouble("precio"),
                            rs.getInt("stock"),
                            null,
                            rs.getString("imagen"));

                    productos.add(producto);
                }
            }
        }
        return productos;
    }

    @Override
    public Producto getById(int id) throws SQLException {
        Connection conn = ConnectionFactory.getConnection();
        Producto producto = null;
        if (conn != null) {
            try (PreparedStatement ps = conn.prepareStatement(GET_BY_ID)) {
                
                ps.setInt(1, id); // Asignar el parámetro ANTES de ejecutar

                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) { // Si hay un resultado, lo procesamos
                        producto = new Producto(
                                rs.getInt("id_producto"),
                                rs.getString("nombre"),
                                Categoria.valueOf(rs.getString("categoria").toUpperCase()),
                                rs.getDouble("precio"),
                                rs.getInt("stock"),
                                null,
                                rs.getString("imagen"));
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return producto;
    }


    @Override
    public boolean update(Producto producto) throws SQLException {
        // Se necesita el ID para actualizar, así que nos aseguramos de que no sea 0
        if (producto.getId_producto() == 0) {
            return false;
        }
        ProductoDAO productoDAO = new ProductoDAO(producto);
        return productoDAO.update();
    }


    @Override
    public boolean add(Producto producto) throws SQLException {
        ProductoDAO productoDAO = new ProductoDAO(producto);
        return productoDAO.save();
    }

    @Override
    public boolean delete(Producto producto) throws SQLException {
        ProductoDAO productoDAO = new ProductoDAO(producto);
        return productoDAO.remove();
    }
}
