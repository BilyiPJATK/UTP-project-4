package dao;

import entity.UsersEntity;

import jakarta.persistence.EntityManager;

import java.util.List;

public class UserDAO implements DAO<UsersEntity> {

    private final EntityManager entityManager;

    // Constructor that accepts EntityManager
    public UserDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

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

    @Override
    public UsersEntity getById(int id) {
        return entityManager.find(UsersEntity.class, id);
    }

    @Override
    public List<UsersEntity> getAll() {
        return entityManager.createQuery("SELECT u FROM UsersEntity u", UsersEntity.class).getResultList();
    }

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
