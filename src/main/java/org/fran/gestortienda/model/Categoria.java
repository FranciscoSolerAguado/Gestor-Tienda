package org.fran.gestortienda.model;

public enum Categoria {
    BEBIDAS("Bebidas"),
    SNACKS("Snacks"),
    ELECTRONICA("Electrónica"),
    LIMPIEZA("Limpieza"),
    LACTEOS("Lácteos"),
    FRUTAS_Y_VERDURAS("Frutas y Verduras"),
    CARNICERIA("Carnicería"),
    PESCADERIA("Pescadería"),
    PANADERIA("Panadería y Repostería"),
    CONGELADOS("Congelados"),
    CONSERVAS("Conservas"),
    CUIDADO_PERSONAL("Cuidado Personal"),
    MASCOTAS("Mascotas"),
    HOGAR("Hogar y Bricolaje");

    private final String nombreParaMostrar;

    Categoria(String nombreParaMostrar) {
        this.nombreParaMostrar = nombreParaMostrar;
    }

    public String getNombreParaMostrar() {
        return nombreParaMostrar;
    }

    @Override
    public String toString() {
        return this.nombreParaMostrar;
    }
    }
