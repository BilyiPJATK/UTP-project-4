package org.example;

import dao.UserDAO;
import entity.BooksEntity;
import entity.BorrowingsEntity;
import entity.CopiesEntity;
import entity.UsersEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import java.sql.Date;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserTests {

    private EntityManagerFactory emf;
    private EntityManager entityManager;
    private UserDAO usersDAO;

    @BeforeAll
    void setUp() {
        emf = Persistence.createEntityManagerFactory("LibraryPersistenceUnit");
        entityManager = emf.createEntityManager();
        usersDAO = new UserDAO(entityManager);
    }

    @Test
    void testCreateUser() {
        UsersEntity user = new UsersEntity("John Doe", "john@example.com", "1234567890", "123 Main St");
        usersDAO.create(user);

        UsersEntity fetchedUser = entityManager.find(UsersEntity.class, user.getId());

        assertNotNull(fetchedUser);
        assertEquals(user.getName(), fetchedUser.getName());
        assertEquals(user.getEmail(), fetchedUser.getEmail());
        assertEquals(user.getPhonenumber(), fetchedUser.getPhonenumber());
        assertEquals(user.getAddress(), fetchedUser.getAddress());
    }

    @Test
    void testGetUserById() {
        UsersEntity user = new UsersEntity("Jane Doe", "jane@example.com", "0987654321", "456 Elm St");
        usersDAO.create(user);

        UsersEntity fetchedUser = usersDAO.getById(user.getId());

        assertNotNull(fetchedUser);
        assertEquals(user.getId(), fetchedUser.getId());
        assertEquals("Jane Doe", fetchedUser.getName());
    }

    @Test
    void testUpdateUser() {
        UsersEntity user = new UsersEntity("Old User", "old@example.com", "111222333", "Old Address");
        usersDAO.create(user);

        user.setName("Updated User");
        user.setEmail("updated@example.com");
        user.setPhonenumber("444555666");
        user.setAddress("Updated Address");
        usersDAO.update(user);

        UsersEntity updatedUser = entityManager.find(UsersEntity.class, user.getId());

        assertNotNull(updatedUser);
        assertEquals("Updated User", updatedUser.getName());
        assertEquals("updated@example.com", updatedUser.getEmail());
        assertEquals("444555666", updatedUser.getPhonenumber());
        assertEquals("Updated Address", updatedUser.getAddress());
    }

    @Test
    void testDeleteUser() {
        UsersEntity user = new UsersEntity("Delete Me User", "delete@example.com", "777888999", "Delete Me Address");
        usersDAO.create(user);

        usersDAO.delete(user.getId());

        UsersEntity deletedUser = entityManager.find(UsersEntity.class, user.getId());
        assertNull(deletedUser);
    }

    @Test
    void testEmailFormatValidation() {
//        UsersEntity user = new UsersEntity("name","used@gmail.com","32231","test");
//        usersDAO.create(user);
        assertFalse(LibrarySystem.isEmailUnique("used@gmail.com"));
    }

    @Test
    void testNoEmailEx() {
        assertFalse(LibrarySystem.isEmailUnique(null));
    }

    @Test
    void testAddBorrowings(){
        UsersEntity user = new UsersEntity("user", "testadd@example.com", "777888999", "Delete Me Address");
        usersDAO.create(user);

        BooksEntity book = entityManager.find(BooksEntity.class, 10);

        CopiesEntity copy = new CopiesEntity(book,432,"Available");
        CopiesEntity copy2 = new CopiesEntity(book,432,"Available");

        BorrowingsEntity borrowing = new BorrowingsEntity(user, copy, Date.valueOf(LocalDate.now()), null);
        borrowingDAO.create(borrowing);

        assertNotNull(user);
    }
}
