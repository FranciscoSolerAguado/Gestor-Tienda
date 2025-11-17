package org.fran.gestortienda.model;

import java.util.Date;
import java.util.Objects;

public class Venta {
    int id_venta;
    Date fecha;
    Double decimal;
    Cliente cliente;

    public Venta() {

    }

    public Venta(int id_venta, Date fecha, Double decimal, Cliente cliente) {
        this.id_venta = id_venta;
        this.fecha = fecha;
        this.decimal = decimal;
        this.cliente = cliente;
    }

    public int getId_venta() {
        return id_venta;
    }

    public void setId_venta(int id_venta) {
        this.id_venta = id_venta;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Double getDecimal() {
        return decimal;
    }

    public void setDecimal(Double decimal) {
        this.decimal = decimal;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    @Override
    public String toString() {
        return "Venta{" +
                "id_venta=" + id_venta +
                ", fecha=" + fecha +
                ", decimal=" + decimal +
                ", cliente=" + cliente +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Venta venta = (Venta) o;
        return id_venta == venta.id_venta;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id_venta);
    }
}
