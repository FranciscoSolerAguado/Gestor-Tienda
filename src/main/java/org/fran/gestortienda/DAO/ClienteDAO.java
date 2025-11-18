package org.fran.gestortienda.DAO;

import org.fran.gestortienda.model.entity.Cliente;

public class ClienteDAO extends Cliente {

    public ClienteDAO(int id_cliente, String nombre, String telefono, String correo) {
        super(id_cliente, nombre, telefono, correo);
    }

    public ClienteDAO() {
        super();
    }

    public ClienteDAO(Cliente c) {
        super(c.getId_cliente(), c.getNombre(), c.getTelefono(), c.getCorreo());
    }
}
