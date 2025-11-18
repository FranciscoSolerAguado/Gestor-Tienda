package org.fran.gestortienda.model;

import java.sql.SQLException;
import java.util.List;

/**
 * Interfaz genérica para operaciones CRUD (Crear, Leer, Actualizar, Borrar).
 *
 * @param <T> el tipo de la entidad a manejar.
 */
public interface CRUD<T> {


    boolean save() throws SQLException;

    boolean add(T objeto) throws SQLException;

    boolean remove() throws SQLException;

    boolean delete(T objeto) throws SQLException;

    boolean update() throws SQLException;

    boolean update(T objeto) throws SQLException;

    List<T> getAll() throws  SQLException;

    T getById(int id) throws SQLException;
}
