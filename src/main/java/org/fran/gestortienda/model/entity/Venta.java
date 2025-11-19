package org.fran.gestortienda.model.entity;

import java.util.Date;
import java.util.Objects;

public class Venta {
    int id_venta;
    Date fecha;
    Double total;
    Cliente cliente;

    public Venta() {

    }

    public Venta(int id_venta, Date fecha, Double total, Cliente cliente) {
        this.id_venta = id_venta;
        this.fecha = fecha;
        this.total = total;
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

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
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
                ", total=" + total +
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
