package dao;

import entity.BorrowingsEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.List;

public class BorrowingDAO implements DAO<BorrowingsEntity> {

    private final EntityManager entityManager;

    public BorrowingDAO(EntityManager entityManager) {this.entityManager = entityManager;
    }

    @Override
    public void create(BorrowingsEntity borrowing) {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(borrowing);  // Persist the borrowing entity
            entityManager.getTransaction().commit();
        } finally {
            entityManager.close();
        }
    }

    @Override
    public BorrowingsEntity getById(int id) {
        try {
            return entityManager.find(BorrowingsEntity.class, id);  // Return the Borrowing entity by ID
        } finally {
            entityManager.close();
        }
    }

    @Override
    public List<BorrowingsEntity> getAll() {
        try {
            return entityManager.createQuery("SELECT b FROM BorrowingsEntity b", BorrowingsEntity.class).getResultList();
        } finally {
            entityManager.close();
        }
    }

    @Override
    public void update(BorrowingsEntity borrowing) {
        try {
            entityManager.getTransaction().begin();
            entityManager.merge(borrowing);  // Merge the changes of the borrowing entity
            entityManager.getTransaction().commit();
        } finally {
            entityManager.close();
        }
    }

    @Override
    public void delete(int id) {
        try {
            BorrowingsEntity borrowing = entityManager.find(BorrowingsEntity.class, id);
            if (borrowing != null) {
                entityManager.getTransaction().begin();
                entityManager.remove(borrowing);  // Remove the borrowing entity from the database
                entityManager.getTransaction().commit();
            }
        } finally {
            entityManager.close();
        }
    }
}
