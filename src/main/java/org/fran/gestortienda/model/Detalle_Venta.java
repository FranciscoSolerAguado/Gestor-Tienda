package org.fran.gestortienda.model;

import java.util.Objects;

public class Detalle_Venta {
    int id_detalle;
    Venta venta;
    Producto producto;
    int cantidad;
    double descuento;
    double precio_unitario;
    double iva;
    double subtotal;

    public Detalle_Venta() {

    }

    public Detalle_Venta(int id_detalle, Venta venta, Producto producto, int cantidad, double descuento, double precio_unitario, double iva, double subtotal) {
        this.id_detalle = id_detalle;
        this.venta = venta;
        this.producto = producto;
        this.cantidad = cantidad;
        this.descuento = descuento;
        this.precio_unitario = precio_unitario;
        this.iva = iva;
        this.subtotal = subtotal;
    }

    public int getId_detalle() {
        return id_detalle;
    }

    public void setId_detalle(int id_detalle) {
        this.id_detalle = id_detalle;
    }

    public Venta getVenta() {
        return venta;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public double getPrecio_unitario() {
        return precio_unitario;
    }

    public void setPrecio_unitario(double precio_unitario) {
        this.precio_unitario = precio_unitario;
    }

    public double getIva() {
        return iva;
    }

    public void setIva(double iva) {
        this.iva = iva;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    @Override
    public String toString() {
        return "Detalle_Venta{" +
                "id_detalle=" + id_detalle +
                ", venta=" + venta +
                ", producto=" + producto +
                ", cantidad=" + cantidad +
                ", descuento=" + descuento +
                ", precio_unitario=" + precio_unitario +
                ", iva=" + iva +
                ", subtotal=" + subtotal +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Detalle_Venta that = (Detalle_Venta) o;
        return id_detalle == that.id_detalle;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id_detalle);
    }
}
