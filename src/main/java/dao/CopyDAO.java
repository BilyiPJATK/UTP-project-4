package dao;

import entity.CopiesEntity;
import jakarta.persistence.EntityManager;

import java.util.List;

/**
 * {@code CopyDAO} is a Data Access Object (DAO) class responsible for handling CRUD (Create, Read, Update, Delete)
 * operations on the {@link CopiesEntity} object. This class interacts with the database through the {@link EntityManager},
 * abstracting the database operations for the {@link CopiesEntity}.
 *
 * <p>It provides methods to create, read, update, and delete copy records in the database. Each operation is wrapped
 * in a transaction, ensuring data consistency and rollback in case of errors.</p>
 */
public class CopyDAO implements DAO<CopiesEntity> {

    private final EntityManager entityManager;

    /**
     * Constructs a new {@code CopyDAO} with the given {@link EntityManager}.
     *
     * @param entityManager the {@link EntityManager} to be used for database operations.
     */
    public CopyDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Creates a new copy record in the database.
     * <p>This method begins a transaction, persists the given {@link CopiesEntity} object,
     * and commits the transaction. If an error occurs, the transaction is rolled back.</p>
     *
     * @param copy the {@link CopiesEntity} object to be persisted in the database.
     * @throws RuntimeException if the transaction fails or any other error occurs during the operation.
     */
    @Override
    public void create(CopiesEntity copy) {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(copy);  // Persist the copy entity
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        }
    }

    /**
     * Retrieves a copy record by its ID.
     * <p>This method fetches a copy from the database based on the given ID. If the copy is found,
     * it returns the corresponding {@link CopiesEntity}; otherwise, it returns {@code null}.</p>
     *
     * @param id the ID of the copy record to be retrieved.
     * @return the {@link CopiesEntity} object with the specified ID, or {@code null} if not found.
     */
    @Override
    public CopiesEntity getById(int id) {
        return entityManager.find(CopiesEntity.class, id);
    }

    /**
     * Retrieves all copy records from the database.
     * <p>This method retrieves a list of all copies stored in the {@code CopiesEntity} table.</p>
     *
     * @return a list of all {@link CopiesEntity} objects.
     */
    @Override
    public List<CopiesEntity> getAll() {
        return entityManager.createQuery("SELECT c FROM CopiesEntity c", CopiesEntity.class).getResultList();
    }

    /**
     * Updates an existing copy record in the database.
     * <p>This method begins a transaction, merges the given {@link CopiesEntity} with the existing record,
     * and commits the transaction. If an error occurs, the transaction is rolled back.</p>
     *
     * @param copy the {@link CopiesEntity} object containing updated data.
     * @throws RuntimeException if the transaction fails or any other error occurs during the operation.
     */
    @Override
    public void update(CopiesEntity copy) {
        try {
            entityManager.getTransaction().begin();
            entityManager.merge(copy);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        }
    }

    /**
     * Deletes a copy record from the database by its ID.
     * <p>This method finds the copy record by the given ID, begins a transaction, removes the copy
     * from the database, and commits the transaction. If the copy does not exist, no action is taken.
     * If an error occurs, the transaction is rolled back.</p>
     *
     * @param id the ID of the copy record to be deleted.
     * @throws RuntimeException if the transaction fails or any other error occurs during the operation.
     */
    @Override
    public void delete(int id) {
        CopiesEntity copy = entityManager.find(CopiesEntity.class, id);
        if (copy != null) {
            try {
                entityManager.getTransaction().begin();
                entityManager.remove(copy);
                entityManager.getTransaction().commit();
            } catch (Exception e) {
                if (entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().rollback();
                }
                throw e;
            }
        }
    }
}
