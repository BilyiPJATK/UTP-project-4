package dao;

import entity.BorrowingsEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.List;

/**
 * {@code BorrowingDAO} is a Data Access Object (DAO) class responsible for handling CRUD (Create, Read, Update, Delete)
 * operations on the {@link BorrowingsEntity} object. This class interacts with the database through the {@link EntityManager},
 * abstracting the database operations for the {@link BorrowingsEntity}.
 *
 * <p>It provides methods to create, read, update, and delete borrowing records in the database. Each operation is wrapped
 * in a transaction, ensuring data consistency and rollback in case of errors.</p>
 */
public class BorrowingDAO implements DAO<BorrowingsEntity> {

    private final EntityManager entityManager;

    /**
     * Constructs a new {@code BorrowingDAO} with the given {@link EntityManager}.
     *
     * @param entityManager the {@link EntityManager} to be used for database operations.
     */
    public BorrowingDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Creates a new borrowing record in the database.
     * <p>This method begins a transaction, persists the given {@link BorrowingsEntity} object,
     * and commits the transaction. If an error occurs, the transaction is rolled back.</p>
     *
     * @param borrowing the {@link BorrowingsEntity} object to be persisted in the database.
     * @throws RuntimeException if the transaction fails or any other error occurs during the operation.
     */
    @Override
    public void create(BorrowingsEntity borrowing) {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(borrowing);  // Persist the borrowing entity
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        }
    }

    /**
     * Retrieves a borrowing record by its ID.
     * <p>This method fetches a borrowing from the database based on the given ID. If the borrowing is found,
     * it returns the corresponding {@link BorrowingsEntity}; otherwise, it returns {@code null}.</p>
     *
     * @param id the ID of the borrowing record to be retrieved.
     * @return the {@link BorrowingsEntity} object with the specified ID, or {@code null} if not found.
     */
    @Override
    public BorrowingsEntity getById(int id) {
        try {
            return entityManager.find(BorrowingsEntity.class, id);  // Return the Borrowing entity by ID
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        }
    }

    /**
     * Retrieves all borrowing records from the database.
     * <p>This method retrieves a list of all borrowings stored in the {@code BorrowingsEntity} table.</p>
     *
     * @return a list of all {@link BorrowingsEntity} objects.
     */
    @Override
    public List<BorrowingsEntity> getAll() {
        try {
            return entityManager.createQuery("SELECT b FROM BorrowingsEntity b", BorrowingsEntity.class).getResultList();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        }
    }

    /**
     * Updates an existing borrowing record in the database.
     * <p>This method begins a transaction, merges the given {@link BorrowingsEntity} with the existing record,
     * and commits the transaction. If an error occurs, the transaction is rolled back.</p>
     *
     * @param borrowing the {@link BorrowingsEntity} object containing updated data.
     * @throws RuntimeException if the transaction fails or any other error occurs during the operation.
     */
    @Override
    public void update(BorrowingsEntity borrowing) {
        try {
            entityManager.getTransaction().begin();
            entityManager.merge(borrowing);  // Merge the changes of the borrowing entity
            entityManager.getTransaction().commit();
        }catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        }
    }

    /**
     * Deletes a borrowing record from the database by its ID.
     * <p>This method finds the borrowing record by the given ID, begins a transaction, removes the borrowing
     * from the database, and commits the transaction. If the borrowing does not exist, no action is taken.
     * If an error occurs, the transaction is rolled back.</p>
     *
     * @param id the ID of the borrowing record to be deleted.
     * @throws RuntimeException if the transaction fails or any other error occurs during the operation.
     */
    @Override
    public void delete(int id) {
        try {
            BorrowingsEntity borrowing = entityManager.find(BorrowingsEntity.class, id);
            if (borrowing != null) {
                entityManager.getTransaction().begin();
                entityManager.remove(borrowing);  // Remove the borrowing entity from the database
                entityManager.getTransaction().commit();
            }
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        }
    }
}
