package org.example;

import dao.BorrowingDAO;
import entity.BorrowingsEntity;
import entity.CopiesEntity;
import entity.UsersEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BorrowingTests {

    private static EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private BorrowingDAO borrowingDAO;

    @BeforeEach
    public void setUp() {
        entityManagerFactory = Persistence.createEntityManagerFactory("LibraryPersistenceUnit");
        entityManager = entityManagerFactory.createEntityManager();
        borrowingDAO = new BorrowingDAO(entityManager);
    }

    @Test
    public void testCreateBorrowing() {
        UsersEntity user = entityManager.find(UsersEntity.class, 7);

        CopiesEntity copy = entityManager.find(CopiesEntity.class,3);

        BorrowingsEntity borrowing = new BorrowingsEntity(user, copy, Date.valueOf(LocalDate.now()), Date.valueOf(LocalDate.now().plusDays(14)));
        borrowingDAO.create(borrowing);

        BorrowingsEntity savedBorrowing = entityManager.find(BorrowingsEntity.class, borrowing.getId());

        assertNotNull(savedBorrowing);
        assertEquals(user.getId(), savedBorrowing.getUserid().getId());
        assertEquals(copy.getId(), savedBorrowing.getCopyid().getId());
    }

    @Test
    public void testReadBorrowing() {
        UsersEntity user = entityManager.find(UsersEntity.class, 7);

        CopiesEntity copy = entityManager.find(CopiesEntity.class,3);

        BorrowingsEntity borrowing = new BorrowingsEntity(user, copy, Date.valueOf(LocalDate.now()), Date.valueOf(LocalDate.now().plusDays(14)));
        borrowingDAO.create(borrowing);

        BorrowingsEntity retrievedBorrowing = borrowingDAO.getById(borrowing.getId());
        assertNotNull(retrievedBorrowing);
        assertEquals(borrowing.getId(), retrievedBorrowing.getId());
    }

    @Test
    public void testUpdateBorrowing() {
        UsersEntity user = entityManager.find(UsersEntity.class, 7);

        CopiesEntity copy = entityManager.find(CopiesEntity.class,3);

        BorrowingsEntity borrowing = new BorrowingsEntity(user, copy, Date.valueOf(LocalDate.now()), Date.valueOf(LocalDate.now().plusDays(14)));
        borrowingDAO.create(borrowing);

        LocalDate newReturnDate = LocalDate.now().plusDays(21);
        borrowing.setReturndate(Date.valueOf(newReturnDate));
        borrowingDAO.update(borrowing);

        BorrowingsEntity updatedBorrowing = borrowingDAO.getById(borrowing.getId());
        assertNotNull(updatedBorrowing);
        assertEquals(Date.valueOf(newReturnDate), updatedBorrowing.getReturndate());
    }

    @Test
    public void testDeleteBorrowing() {
        UsersEntity user = entityManager.find(UsersEntity.class, 7);

        CopiesEntity copy = entityManager.find(CopiesEntity.class,3);

        BorrowingsEntity borrowing = new BorrowingsEntity(user, copy, Date.valueOf(LocalDate.now()), Date.valueOf(LocalDate.now().plusDays(14)));
        borrowingDAO.create(borrowing);

        BorrowingsEntity savedBorrowing = borrowingDAO.getById(borrowing.getId());
        assertNotNull(savedBorrowing);

        borrowingDAO.delete(savedBorrowing.getId());
        BorrowingsEntity deletedBorrowing = borrowingDAO.getById(borrowing.getId());
        assertNull(deletedBorrowing);
    }

    @Test
    public void testGetAllBorrowings() {
        UsersEntity user = entityManager.find(UsersEntity.class, 7);

        CopiesEntity copy = entityManager.find(CopiesEntity.class,3);

        BorrowingsEntity borrowing1 = new BorrowingsEntity(user, copy, Date.valueOf(LocalDate.now()), Date.valueOf(LocalDate.now().plusDays(14)));
        borrowingDAO.create(borrowing1);


        BorrowingsEntity borrowing2 = new BorrowingsEntity(user, copy, Date.valueOf(LocalDate.now()), Date.valueOf(LocalDate.now().plusDays(14)));
        borrowingDAO.create(borrowing2);

        entityManager.persist(borrowing1);
        entityManager.persist(borrowing2);

        List<BorrowingsEntity> borrowings = borrowingDAO.getAll();
        assertNotNull(borrowings);
        assertTrue(borrowings.size() >= 2); // At least 2 borrowings should exist
    }

    @Test
    void testAddBorrowingNoReturnDate() {
        UsersEntity user = entityManager.find(UsersEntity.class, 7);

        CopiesEntity copy = entityManager.find(CopiesEntity.class,3);

        BorrowingsEntity borrowing = new BorrowingsEntity(user, copy, Date.valueOf(LocalDate.now()), null);
        borrowingDAO.create(borrowing);

        assertNotNull(borrowing);
    }
}
