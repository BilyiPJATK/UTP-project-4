package dao;

import entity.PublishersEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;

/**
 * {@code PublisherDAO} is a Data Access Object (DAO) class responsible for handling CRUD operations related to
 * the {@link PublishersEntity} object. It interacts with the database through the {@link EntityManager}, providing
 * methods for performing Create, Read, Update, and Delete operations on the {@link PublishersEntity} table.
 *
 * <p>This class uses transactions to ensure data consistency when performing database operations, such as persisting,
 * updating, or deleting a publisher.</p>
 */
public class PublisherDAO {

    private EntityManager entityManager;

    /**
     * Constructs a new {@code PublisherDAO} with the specified {@link EntityManager}.
     *
     * @param entityManager the {@link EntityManager} used to interact with the database.
     */
    public PublisherDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Creates a new publisher record in the database.
     * <p>This method begins a transaction, persists the given {@link PublishersEntity} object, and commits the
     * transaction after the publisher is successfully persisted. If an error occurs, the transaction is rolled back.</p>
     *
     * @param publisher the {@link PublishersEntity} object to be persisted in the database.
     * @throws RuntimeException if an error occurs during the persistence process, or if the transaction fails.
     */
    public void create(PublishersEntity publisher) {
        EntityTransaction transaction = entityManager.getTransaction();

        if (!transaction.isActive()) {
            transaction.begin();
        }
        try {
            entityManager.persist(publisher);
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    /**
     * Retrieves a publisher by its ID.
     * <p>This method fetches a publisher from the database based on the given ID. If the publisher is found, it returns
     * the corresponding {@link PublishersEntity}; otherwise, it returns {@code null}.</p>
     *
     * @param id the ID of the publisher to be retrieved.
     * @return the {@link PublishersEntity} object with the specified ID, or {@code null} if not found.
     */
    public PublishersEntity getById(int id) {
        return entityManager.find(PublishersEntity.class, id);
    }

    /**
     * Updates an existing publisher record in the database.
     * <p>This method begins a transaction, merges the given {@link PublishersEntity} with the existing record,
     * and commits the transaction. If an error occurs, the transaction is rolled back.</p>
     *
     * @param publisher the {@link PublishersEntity} object containing updated data.
     * @throws RuntimeException if the transaction fails or any other error occurs during the operation.
     */
    public void update(PublishersEntity publisher) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.merge(publisher);
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    /**
     * Deletes a publisher record from the database by its ID.
     * <p>This method finds the publisher by the given ID, begins a transaction, removes the publisher from the database,
     * and commits the transaction. If the publisher does not exist, no action is taken. If an error occurs,
     * the transaction is rolled back.</p>
     *
     * @param id the ID of the publisher to be deleted.
     * @throws RuntimeException if the transaction fails or any other error occurs during the operation.
     */
    public void delete(int id) {
        PublishersEntity publisher = entityManager.find(PublishersEntity.class, id);
        if (publisher != null) {
            EntityTransaction transaction = entityManager.getTransaction();
            try {
                transaction.begin();
                entityManager.remove(publisher);
                transaction.commit();
            } catch (RuntimeException e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                throw e;
            }
        }
    }
}
