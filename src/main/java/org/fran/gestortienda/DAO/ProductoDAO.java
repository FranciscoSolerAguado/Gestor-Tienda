package org.fran.gestortienda.DAO;

import org.fran.gestortienda.model.entity.Producto;

public class ProductoDAO extends Producto {
    public ProductoDAO(int id_producto, String nombre, org.fran.gestortienda.model.Categoria categoria, double precio, int stock, org.fran.gestortienda.model.entity.Proveedor proveedor, String imagen) {
        super(id_producto, nombre, categoria, precio, stock, proveedor, imagen);
    }

    public ProductoDAO() {
        super();
    }

    public ProductoDAO(Producto p) {
        super(p.getId_producto(), p.getNombre(), p.getCategoria(), p.getPrecio(), p.getStock(), p.getProveedor(), p.getImagen());
    }

}
