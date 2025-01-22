package dao;

import entity.PublishersEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

/**
 * {@code PublisherDAO} is a Data Access Object (DAO) class responsible for handling operations related to the
 * {@link PublishersEntity} object. It interacts with the database through the {@link EntityManager}, providing methods
 * for CRUD operations on the {@link PublishersEntity} table.
 *
 * <p>This class uses transactions to ensure data consistency when performing database operations, such as persisting
 * a publisher.</p>
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
     * <p>This method begins a transaction, persists the given {@link PublishersEntity} object, and commits the transaction
     * after the publisher is successfully persisted. If an error occurs, the transaction is rolled back.</p>
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
}
