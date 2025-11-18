package org.fran.gestortienda.model.entity;

import java.util.Objects;

public class Proveedor {
    int id_proveedor;
    String nombre;
    String telefono;
    String correo;

    public Proveedor(){

    }

    public Proveedor(int id_proveedor, String nombre, String telefono, String correo) {
        this.id_proveedor = id_proveedor;
        this.nombre = nombre;
        this.telefono = telefono;
        this.correo = correo;
    }

    public int getId_proveedor() {
        return id_proveedor;
    }

    public void setId_proveedor(int id_proveedor) {
        this.id_proveedor = id_proveedor;
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

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    @Override
    public String toString() {
        return "Proveedor{" +
                "id_proveedor=" + id_proveedor +
                ", nombre='" + nombre + '\'' +
                ", telefono=" + telefono +
                ", correo='" + correo + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Proveedor proveedor = (Proveedor) o;
        return id_proveedor == proveedor.id_proveedor;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id_proveedor);
    }
}
