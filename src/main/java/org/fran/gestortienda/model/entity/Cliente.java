package org.fran.gestortienda.model.entity;

import java.util.Objects;

public class Cliente {
    int id_cliente;
    String nombre;
    String telefono;
    String direccion;

    public Cliente() {

    }

    public Cliente(int id_cliente, String nombre, String telefono, String direccion) {
        this.id_cliente = id_cliente;
        this.nombre = nombre;
        this.telefono = telefono;
        this.direccion = direccion;
    }

    public int getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(int id_cliente) {
        this.id_cliente = id_cliente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "id_cliente=" + id_cliente +
                ", nombre='" + nombre + '\'' +
                ", telefono='" + telefono + '\'' +
                ", correo='" + direccion + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        return id_cliente == cliente.id_cliente;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id_cliente);
    }
}
