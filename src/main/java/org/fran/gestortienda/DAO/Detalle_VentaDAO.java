package org.fran.gestortienda.DAO;

import org.fran.gestortienda.Connection.ConnectionFactory;
import org.fran.gestortienda.model.CRUD;
import org.fran.gestortienda.model.entity.Detalle_Venta;
import org.fran.gestortienda.model.entity.Producto;
import org.fran.gestortienda.model.entity.Venta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Detalle_VentaDAO extends Detalle_Venta implements CRUD<Detalle_Venta> {

    // --- QUERIES ---
    private final static String INSERT = "INSERT INTO detalle_venta(id_venta, id_producto, cantidad, descuento, precio_unitario, iva, subtotal) VALUES(?, ?, ?, ?, ?, ?, ?)";
    private final static String UPDATE = "UPDATE detalle_venta SET id_venta = ?, id_producto = ?, cantidad = ?, descuento = ?, precio_unitario = ?, iva = ?, subtotal = ? WHERE id_detalle = ?";
    private final static String DELETE = "DELETE FROM detalle_venta WHERE id_detalle = ?";
    private final static String GET_ALL = "SELECT id_detalle, id_venta, id_producto, cantidad, descuento, precio_unitario, iva, subtotal FROM detalle_venta";
    private final static String GET_BY_ID = "SELECT id_detalle, id_venta, id_producto, cantidad, descuento, precio_unitario, iva, subtotal FROM detalle_venta WHERE id_detalle = ?";
    private final static String GET_BY_VENTA = """
       SELECT 
           dv.id_detalle, dv.id_venta, dv.cantidad, dv.descuento, dv.precio_unitario, dv.iva, dv.subtotal,
           p.id_producto, p.nombre, p.categoria, p.precio, p.stock, p.imagen, p.id_proveedor
       FROM 
           detalle_venta dv
       JOIN 
           producto p ON dv.id_producto = p.id_producto
       WHERE 
           dv.id_venta = ?
   """;

    private final static String DELETE_BY_VENTA_ID = "DELETE FROM detalle_venta WHERE id_venta = ?";


    // --- CONSTRUCTORES ---
    public Detalle_VentaDAO(int id_detalle, Venta venta, Producto producto, int cantidad, double descuento, double precio_unitario, double iva, double subtotal) {
        super(id_detalle, venta, producto, cantidad, descuento, precio_unitario, iva, subtotal);
    }

    public Detalle_VentaDAO() {
        super();
    }

    public Detalle_VentaDAO(Detalle_Venta dv) {
        super(dv.getId_detalle(), dv.getVenta(), dv.getProducto(), dv.getCantidad(), dv.getDescuento(), dv.getPrecio_unitario(), dv.getIva(), dv.getSubtotal());
    }

    // --- MÉTODOS DE INSTANCIA (Acceden a la BD) ---

    @Override
    public boolean save() throws SQLException {
        Connection conn = ConnectionFactory.getConnection();
        if (conn != null) {
            try (PreparedStatement ps = conn.prepareStatement(INSERT)) {
                // Se asume que getVenta() y getProducto() devuelven objetos con IDs válidos
                ps.setInt(1, getVenta().getId_venta());
                ps.setInt(2, getProducto().getId_producto());
                ps.setInt(3, getCantidad());
                ps.setDouble(4, getDescuento());
                ps.setDouble(5, getPrecio_unitario());
                ps.setDouble(6, getIva());
                ps.setDouble(7, getSubtotal());
                return ps.executeUpdate() > 0;
            }
        }
        return false;
    }

    @Override
    public boolean remove() throws SQLException {
        Connection conn = ConnectionFactory.getConnection();
        if (conn != null) {
            try (PreparedStatement ps = conn.prepareStatement(DELETE)) {
                ps.setInt(1, getId_detalle());
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
                ps.setInt(1, getVenta().getId_venta());
                ps.setInt(2, getProducto().getId_producto());
                ps.setInt(3, getCantidad());
                ps.setDouble(4, getDescuento());
                ps.setDouble(5, getPrecio_unitario());
                ps.setDouble(6, getIva());
                ps.setDouble(7, getSubtotal());
                ps.setInt(8, getId_detalle()); // Cláusula WHERE
                return ps.executeUpdate() > 0;
            }
        }
        return false;
    }

    // --- MÉTODOS DE INTERFAZ (Delegan en los de instancia) ---

    @Override
    public boolean add(Detalle_Venta detalle_venta) throws SQLException {
        Detalle_VentaDAO detalleVentaDAO = new Detalle_VentaDAO(detalle_venta);
        return detalleVentaDAO.save();
    }

    @Override
    public boolean delete(Detalle_Venta detalle_venta) throws SQLException {
        if (detalle_venta == null || detalle_venta.getId_detalle() == 0) {
            return false;
        }
        Detalle_VentaDAO detalleVentaDAO = new Detalle_VentaDAO(detalle_venta);
        return detalleVentaDAO.remove();
    }

    @Override
    public boolean update(Detalle_Venta detalle_venta) throws SQLException {
        if (detalle_venta == null || detalle_venta.getId_detalle() == 0) {
            return false;
        }
        Detalle_VentaDAO detalleVentaDAO = new Detalle_VentaDAO(detalle_venta);
        return detalleVentaDAO.update();
    }

    @Override
    public List<Detalle_Venta> getAll() throws SQLException {
        List<Detalle_Venta> detalles = new ArrayList<>();
        Connection conn = ConnectionFactory.getConnection();
        if (conn != null) {
            try (PreparedStatement ps = conn.prepareStatement(GET_ALL);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Se crean objetos Venta y Producto solo con sus IDs para lazy loading
                    Venta v = new Venta();
                    v.setId_venta(rs.getInt("id_venta"));
                    Producto p = new Producto();
                    p.setId_producto(rs.getInt("id_producto"));

                    Detalle_Venta dv = new Detalle_Venta(
                            rs.getInt("id_detalle"),
                            v, // Venta solo con ID
                            p, // Producto solo con ID
                            rs.getInt("cantidad"),
                            rs.getDouble("descuento"),
                            rs.getDouble("precio_unitario"),
                            rs.getDouble("iva"),
                            rs.getDouble("subtotal")
                    );
                    detalles.add(dv);
                }
            }
        }
        return detalles;
    }

    @Override
    public Detalle_Venta getById(int id) throws SQLException {
        Detalle_Venta dv = null;
        Connection conn = ConnectionFactory.getConnection();
        if (conn != null) {
            try (PreparedStatement ps = conn.prepareStatement(GET_BY_ID)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        // Se crean objetos Venta y Producto solo con sus IDs para lazy loading
                        Venta v = new Venta();
                        v.setId_venta(rs.getInt("id_venta"));
                        Producto p = new Producto();
                        p.setId_producto(rs.getInt("id_producto"));

                        dv = new Detalle_Venta(
                                rs.getInt("id_detalle"),
                                v, // Venta solo con ID
                                p, // Producto solo con ID
                                rs.getInt("cantidad"),
                                rs.getDouble("descuento"),
                                rs.getDouble("precio_unitario"),
                                rs.getDouble("iva"),
                                rs.getDouble("subtotal")
                        );
                    }
                }
            }
        }
        return dv;
    }

    public List<Detalle_Venta> getByVenta(int idVenta) throws SQLException {
        List<Detalle_Venta> lista = new ArrayList<>();
        Connection conn = ConnectionFactory.getConnection();
        if (conn != null) {
            try (PreparedStatement ps = conn.prepareStatement(GET_BY_VENTA)) {
                ps.setInt(1, idVenta);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        // 2. CREAMOS UN OBJETO PRODUCTO COMPLETO ("HIDRATADO")
                        Producto productoCompleto = new Producto();
                        productoCompleto.setId_producto(rs.getInt("id_producto"));
                        productoCompleto.setNombre(rs.getString("nombre"));
                        // ... (puedes añadir más campos del producto si los necesitas)

                        // 3. CREAMOS EL DETALLE DE VENTA CON EL PRODUCTO COMPLETO
                        Detalle_Venta dv = new Detalle_Venta();
                        dv.setId_detalle(rs.getInt("id_detalle"));
                        dv.setCantidad(rs.getInt("cantidad"));
                        dv.setDescuento(rs.getDouble("descuento"));
                        dv.setPrecio_unitario(rs.getDouble("precio_unitario"));
                        dv.setIva(rs.getDouble("iva"));
                        dv.setSubtotal(rs.getDouble("subtotal"));
                        dv.setProducto(productoCompleto); // <-- Asignamos el objeto completo

                        lista.add(dv);
                    }
                }
            }
        }
        return lista;
    }

    /**
     * Borra todos los detalles de una venta específica.
     * @param idVenta El ID de la venta cuyos detalles se van a borrar.
     * @return true si se borraron filas, false en caso contrario.
     * @throws SQLException Si ocurre un error de SQL.
     */
    public boolean deleteByVentaId(int idVenta) throws SQLException {
        Connection conn = ConnectionFactory.getConnection();
        if (conn != null) {
            try (PreparedStatement ps = conn.prepareStatement(DELETE_BY_VENTA_ID)) {
                ps.setInt(1, idVenta);
                return ps.executeUpdate() > 0;
            }
        }
        return false;
    }


}
