package dao;

import entity.LibrariansEntity;
import jakarta.persistence.EntityManager;

import java.util.List;

/**
 * {@code LibrarianDAO} is a Data Access Object (DAO) class responsible for handling CRUD (Create, Read, Update, Delete)
 * operations on the {@link LibrariansEntity} object. This class interacts with the database through the {@link EntityManager},
 * abstracting the database operations for the {@link LibrariansEntity}.
 *
 * <p>It provides methods to create, read, update, and delete librarian records in the database. Each operation is wrapped
 * in a transaction, ensuring data consistency and rollback in case of errors.</p>
 */
public class LibrarianDAO implements DAO<LibrariansEntity> {

    private final EntityManager entityManager;

    /**
     * Constructs a new {@code LibrarianDAO} with the given {@link EntityManager}.
     *
     * @param entityManager the {@link EntityManager} to be used for database operations.
     */
    public LibrarianDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Creates a new librarian record in the database.
     * <p>This method begins a transaction, persists the given {@link LibrariansEntity} object,
     * and commits the transaction. If an error occurs, the transaction is rolled back.</p>
     *
     * @param librarian the {@link LibrariansEntity} object to be persisted in the database.
     * @throws RuntimeException if the transaction fails or any other error occurs during the operation.
     */
    @Override
    public void create(LibrariansEntity librarian) {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(librarian);  // Persist the librarian entity
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        }
    }

    /**
     * Retrieves a librarian record by its ID.
     * <p>This method fetches a librarian from the database based on the given ID. If the librarian is found,
     * it returns the corresponding {@link LibrariansEntity}; otherwise, it returns {@code null}.</p>
     *
     * @param id the ID of the librarian record to be retrieved.
     * @return the {@link LibrariansEntity} object with the specified ID, or {@code null} if not found.
     */
    @Override
    public LibrariansEntity getById(int id) {
        return entityManager.find(LibrariansEntity.class, id);
    }

    /**
     * Retrieves all librarian records from the database.
     * <p>This method retrieves a list of all librarians stored in the {@code LibrariansEntity} table.</p>
     *
     * @return a list of all {@link LibrariansEntity} objects.
     */
    @Override
    public List<LibrariansEntity> getAll() {
        return entityManager.createQuery("SELECT l FROM LibrariansEntity l", LibrariansEntity.class).getResultList();
    }

    /**
     * Updates an existing librarian record in the database.
     * <p>This method begins a transaction, merges the given {@link LibrariansEntity} with the existing record,
     * and commits the transaction. If an error occurs, the transaction is rolled back.</p>
     *
     * @param librarian the {@link LibrariansEntity} object containing updated data.
     * @throws RuntimeException if the transaction fails or any other error occurs during the operation.
     */
    @Override
    public void update(LibrariansEntity librarian) {
        try {
            entityManager.getTransaction().begin();
            entityManager.merge(librarian);  // Merge the changes of the librarian entity
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        }
    }

    /**
     * Deletes a librarian record from the database by its ID.
     * <p>This method finds the librarian record by the given ID, begins a transaction, removes the librarian
     * from the database, and commits the transaction. If the librarian does not exist, no action is taken.
     * If an error occurs, the transaction is rolled back.</p>
     *
     * @param id the ID of the librarian record to be deleted.
     * @throws RuntimeException if the transaction fails or any other error occurs during the operation.
     */
    @Override
    public void delete(int id) {
        LibrariansEntity librarian = entityManager.find(LibrariansEntity.class, id);
        if (librarian != null) {
            try {
                entityManager.getTransaction().begin();
                entityManager.remove(librarian);  // Remove the librarian entity from the database
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
