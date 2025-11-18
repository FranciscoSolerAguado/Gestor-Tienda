package org.fran.gestortienda.DAO;

import org.fran.gestortienda.model.entity.Detalle_Venta;
import org.fran.gestortienda.model.entity.Producto;
import org.fran.gestortienda.model.entity.Venta;

public class Detalle_VentaDAO extends Detalle_Venta {
    public Detalle_VentaDAO(int id_detalle, Venta venta, Producto producto, int cantidad, double descuento, double precio_unitario, double iva, double subtotal){
        super(id_detalle, venta, producto, cantidad, descuento, precio_unitario, iva, subtotal);
    }

    public Detalle_VentaDAO(){
        super();
    }

    public Detalle_VentaDAO(Detalle_Venta dv){
        super(dv.getId_detalle(), dv.getVenta(), dv.getProducto(), dv.getCantidad(), dv.getDescuento(), dv.getPrecio_unitario(), dv.getIva(), dv.getSubtotal());
    }
}
