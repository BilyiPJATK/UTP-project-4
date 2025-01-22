package dao;

import entity.CopiesEntity;
import jakarta.persistence.EntityManager;

import java.util.List;

public class CopyDAO implements DAO<CopiesEntity> {

    private final EntityManager entityManager;

    // Constructor that accepts EntityManager
    public CopyDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void create(CopiesEntity copy) {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(copy);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        }
    }

    @Override
    public CopiesEntity getById(int id) {
        return entityManager.find(CopiesEntity.class, id);
    }

    @Override
    public List<CopiesEntity> getAll() {
        return entityManager.createQuery("SELECT c FROM CopiesEntity c", CopiesEntity.class).getResultList();
    }

    @Override
    public void update(CopiesEntity copy) {
        try {
            entityManager.getTransaction().begin();
            entityManager.merge(copy);
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
        CopiesEntity copy = entityManager.find(CopiesEntity.class, id);
        if (copy != null) {
            try {
                entityManager.getTransaction().begin();
                entityManager.remove(copy);
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
