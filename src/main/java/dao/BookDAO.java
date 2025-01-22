package dao;

import entity.BooksEntity;
import jakarta.persistence.EntityManager;

import java.util.List;

public class BookDAO implements DAO<BooksEntity> {

    private final EntityManager entityManager;

    // Constructor that accepts EntityManager
    public BookDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void create(BooksEntity book) {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(book);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        }
    }

    @Override
    public BooksEntity getById(int id) {
        return entityManager.find(BooksEntity.class, id);
    }

    @Override
    public List<BooksEntity> getAll() {
        return entityManager.createQuery("SELECT b FROM BooksEntity b", BooksEntity.class).getResultList();
    }

    @Override
    public void update(BooksEntity book) {
        try {
            entityManager.getTransaction().begin();
            entityManager.merge(book);  // This updates the Book in the database
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        }
    }

    @Override
    public void delete(int id) {
        BooksEntity book = entityManager.find(BooksEntity.class, id);
        if (book != null) {
            try {
                entityManager.getTransaction().begin();
                entityManager.remove(book);
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
