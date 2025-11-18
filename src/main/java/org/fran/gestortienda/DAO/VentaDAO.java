package org.fran.gestortienda.DAO;

import org.fran.gestortienda.model.entity.Venta;

public class VentaDAO extends Venta {
    public VentaDAO(int id_venta, java.util.Date fecha, Double decimal, org.fran.gestortienda.model.entity.Cliente cliente) {
        super(id_venta, fecha, decimal, cliente);
    }

    public VentaDAO() {
        super();
    }

    public VentaDAO(Venta v) {
        super(v.getId_venta(), v.getFecha(), v.getDecimal(), v.getCliente());
    }

}
