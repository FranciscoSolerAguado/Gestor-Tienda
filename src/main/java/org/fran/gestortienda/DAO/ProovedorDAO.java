package org.fran.gestortienda.DAO;

import org.fran.gestortienda.model.entity.Proveedor;

public class ProovedorDAO extends Proveedor {
    public ProovedorDAO(int id_proveedor, String nombre, String telefono, String correo) {
        super(id_proveedor, nombre, telefono, correo);
    }

    public ProovedorDAO() {
        super();
    }

    public ProovedorDAO(Proveedor p) {
        super(p.getId_proveedor(), p.getNombre(), p.getTelefono(), p.getCorreo());
    }

}
