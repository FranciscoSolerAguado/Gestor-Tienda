package org.fran.gestortienda.model.entity;

import org.fran.gestortienda.model.Categoria;

import java.util.Objects;

public class Producto {
    int id_producto;
    String nombre;
    Categoria categoria;
    double precio;
    int stock;
    Proveedor proveedor;

    public Producto() {

    }

    public Producto(int id_producto, String nombre, Categoria categoria, double precio, int stock, Proveedor proveedor) {
        this.id_producto = id_producto;
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
        this.stock = stock;
        this.proveedor = proveedor;
    }

    public int getId_producto() {
        return id_producto;
    }

    public void setId_producto(int id_producto) {
        this.id_producto = id_producto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    @Override
    public String toString() {
        return "Producto{" +
                "id_producto=" + id_producto +
                ", nombre='" + nombre + '\'' +
                ", categoria=" + categoria +
                ", precio=" + precio +
                ", stock=" + stock +
                ", proveedor=" + proveedor +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Producto producto = (Producto) o;
        return id_producto == producto.id_producto;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id_producto);
    }
}
