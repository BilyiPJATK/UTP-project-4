package dao;

import java.util.List;

public interface DAO<T> {

    void create(T entity);  // Create an entity

    T getById(int id);      // Get an entity by ID

    List<T> getAll();       // Get all entities of type T

    void update(T entity);  // Update an entity

    void delete(int id);    // Delete an entity by ID
}
