package dao;

import entity.UsersEntity;
import jakarta.persistence.EntityManager;

import java.util.List;

/**
 * {@code UserDAO} is a Data Access Object (DAO) class responsible for handling CRUD (Create, Read, Update, Delete)
 * operations on the {@link UsersEntity} object. This class interacts with the database through the {@link EntityManager},
 * abstracting the database operations for the {@link UsersEntity}.
 *
 * <p>It provides methods to create, read, update, and delete user records in the database. Each operation is wrapped
 * in a transaction, ensuring data consistency and rollback in case of errors.</p>
 */
public class UserDAO implements DAO<UsersEntity> {

    private final EntityManager entityManager;

    /**
     * Constructs a new {@code UserDAO} with the given {@link EntityManager}.
     *
     * @param entityManager the {@link EntityManager} to be used for database operations.
     */
    public UserDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Creates a new user record in the database.
     * <p>This method begins a transaction, persists the given {@link UsersEntity}, and commits the transaction.
     * If an error occurs, the transaction is rolled back.</p>
     *
     * @param user the {@link UsersEntity} object to be persisted in the database.
     * @throws RuntimeException if the transaction fails or any other error occurs during the operation.
     */
    @Override
    public void create(UsersEntity user) {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(user);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        }
    }

    /**
     * Retrieves a user by its ID.
     * <p>This method fetches a user from the database based on the given ID. If the user is found, it returns
     * the corresponding {@link UsersEntity}; otherwise, it returns {@code null}.</p>
     *
     * @param id the ID of the user to be retrieved.
     * @return the {@link UsersEntity} object with the specified ID, or {@code null} if not found.
     */
    @Override
    public UsersEntity getById(int id) {
        return entityManager.find(UsersEntity.class, id);
    }

    /**
     * Retrieves all users from the database.
     * <p>This method retrieves a list of all users stored in the {@code UsersEntity} table.</p>
     *
     * @return a list of all {@link UsersEntity} objects.
     */
    @Override
    public List<UsersEntity> getAll() {
        return entityManager.createQuery("SELECT u FROM UsersEntity u", UsersEntity.class).getResultList();
    }

    /**
     * Updates an existing user record in the database.
     * <p>This method begins a transaction, merges the given {@link UsersEntity} with the existing record,
     * and commits the transaction. If an error occurs, the transaction is rolled back.</p>
     *
     * @param user the {@link UsersEntity} object containing updated data.
     * @throws RuntimeException if the transaction fails or any other error occurs during the operation.
     */
    @Override
    public void update(UsersEntity user) {
        try {
            entityManager.getTransaction().begin();
            entityManager.merge(user);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        }
    }

    /**
     * Deletes a user record from the database by its ID.
     * <p>This method finds the user by the given ID, begins a transaction, removes the user from the database,
     * and commits the transaction. If the user does not exist, no action is taken. If an error occurs,
     * the transaction is rolled back.</p>
     *
     * @param id the ID of the user to be deleted.
     * @throws RuntimeException if the transaction fails or any other error occurs during the operation.
     */
    @Override
    public void delete(int id) {
        UsersEntity user = entityManager.find(UsersEntity.class, id);
        if (user != null) {
            try {
                entityManager.getTransaction().begin();
                entityManager.remove(user);
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
