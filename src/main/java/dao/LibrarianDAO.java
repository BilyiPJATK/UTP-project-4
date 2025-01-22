package dao;

import entity.LibrariansEntity;
import entity.LibrariansEntity;
import jakarta.persistence.EntityManager;

import java.util.List;

public class LibrarianDAO implements DAO<LibrariansEntity> {

    private final EntityManager entityManager;

    // Constructor that accepts EntityManager
    public LibrarianDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void create(LibrariansEntity librarian) {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(librarian);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        }
    }

    @Override
    public LibrariansEntity getById(int id) {
        return entityManager.find(LibrariansEntity.class, id);
    }

    @Override
    public List<LibrariansEntity> getAll() {
        return entityManager.createQuery("SELECT l FROM LibrariansEntity l", LibrariansEntity.class).getResultList();
    }

    @Override
    public void update(LibrariansEntity librarian) {
        try {
            entityManager.getTransaction().begin();
            entityManager.merge(librarian);
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
        LibrariansEntity librarian = entityManager.find(LibrariansEntity.class, id);
        if (librarian != null) {
            try {
                entityManager.getTransaction().begin();
                entityManager.remove(librarian);
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
