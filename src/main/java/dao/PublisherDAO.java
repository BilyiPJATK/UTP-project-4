package dao;

import entity.PublishersEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class PublisherDAO {

    private EntityManager entityManager;

    public PublisherDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void create(PublishersEntity publisher) {
        EntityTransaction transaction = entityManager.getTransaction();

        if (!transaction.isActive()) {
            transaction.begin();
        }
        try {
            entityManager.persist(publisher);
            transaction.commit(); // commit the transaction after persisting
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback(); // rollback the transaction in case of error
            }
            throw e; // rethrow the exception to ensure proper handling
        }
    }
}
