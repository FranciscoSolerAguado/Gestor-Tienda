package org.fran.gestortienda.DAO;

import org.fran.gestortienda.Connection.MySQLConnection;
import org.fran.gestortienda.model.CRUD;
import org.fran.gestortienda.model.entity.Detalle_Venta;
import org.fran.gestortienda.model.entity.Producto;
import org.fran.gestortienda.model.entity.Venta;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class Detalle_VentaDAO extends Detalle_Venta implements CRUD<Detalle_Venta> {
    private final static String INSERT = "INSERT INTO detalle_venta(id_venta, id_producto, cantidad, descuento, precio_unitario, iva, subtotal) VALUES(?, ?, ?, ?, ?, ?, ?)";

    public Detalle_VentaDAO(int id_detalle, Venta venta, Producto producto, int cantidad, double descuento, double precio_unitario, double iva, double subtotal){
        super(id_detalle, venta, producto, cantidad, descuento, precio_unitario, iva, subtotal);
    }

    public Detalle_VentaDAO(){
        super();
    }

    public Detalle_VentaDAO(Detalle_Venta dv){
        super(dv.getId_detalle(), dv.getVenta(), dv.getProducto(), dv.getCantidad(), dv.getDescuento(), dv.getPrecio_unitario(), dv.getIva(), dv.getSubtotal());
    }


    /**
     * No funciona
     */
    @Override
    public boolean save() throws SQLException {
        Connection conn = MySQLConnection.getConnection();
        if (conn != null) {
            try (java.sql.PreparedStatement ps = conn.prepareStatement(INSERT)) {
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
    public boolean add(Detalle_Venta detalle_venta) throws SQLException {
        Detalle_VentaDAO detalleVentaDAO = new Detalle_VentaDAO(detalle_venta);
        return detalleVentaDAO.save();
    }

    @Override
    public boolean remove() throws SQLException {
        return false;
    }

    @Override
    public boolean delete(Detalle_Venta objeto) throws SQLException {
        return false;
    }

    @Override
    public boolean update() throws SQLException {
        return false;
    }

    @Override
    public boolean update(Detalle_Venta objeto) throws SQLException {
        return false;
    }

    @Override
    public List<Detalle_Venta> getAll() throws SQLException {
        return List.of();
    }

    @Override
    public Detalle_Venta getById(int id) throws SQLException {
        return null;
    }
}
