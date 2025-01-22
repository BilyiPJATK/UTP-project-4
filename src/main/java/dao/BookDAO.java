package dao;

import entity.BooksEntity;
import jakarta.persistence.EntityManager;

import java.util.List;

/**
 * Data Access Object (DAO) for the Books entity. Provides CRUD operations for books in the database.
 */
public class BookDAO implements DAO<BooksEntity> {

    private final EntityManager entityManager;

    /**
     * Constructor that accepts an EntityManager.
     *
     * @param entityManager the EntityManager used to interact with the database
     */
    public BookDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Creates a new book entity in the database.
     *
     * @param book the book entity to be created
     */
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

    /**
     * Retrieves a book by its ID.
     *
     * @param id the ID of the book to retrieve
     * @return the book with the specified ID, or null if not found
     */
    @Override
    public BooksEntity getById(int id) {
        return entityManager.find(BooksEntity.class, id);
    }

    /**
     * Retrieves all books in the database.
     *
     * @return a list of all books
     */
    @Override
    public List<BooksEntity> getAll() {
        return entityManager.createQuery("SELECT b FROM BooksEntity b", BooksEntity.class).getResultList();
    }

    /**
     * Updates an existing book entity in the database.
     *
     * @param book the book entity to update
     */
    @Override
    public void update(BooksEntity book) {
        try {
            entityManager.getTransaction().begin();
            entityManager.merge(book);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        }
    }

    /**
     * Deletes a book entity by its ID.
     *
     * @param id the ID of the book to delete
     */
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
