package dao;

import java.util.List;

/**
 * Generic DAO interface for performing CRUD operations on entities.
 *
 * @param <T> the type of the entity
 */
public interface DAO<T> {

    /**
     * Creates a new entity in the database.
     *
     * @param entity the entity to be created
     */
    void create(T entity);

    /**
     * Retrieves an entity by its ID.
     *
     * @param id the ID of the entity
     * @return the entity with the specified ID, or null if not found
     */
    T getById(int id);

    /**
     * Retrieves all entities of type T.
     *
     * @return a list of all entities of type T
     */
    List<T> getAll();

    /**
     * Updates an existing entity in the database.
     *
     * @param entity the entity to be updated
     */
    void update(T entity);

    /**
     * Deletes an entity by its ID.
     *
     * @param id the ID of the entity to be deleted
     */
    void delete(int id);
}
