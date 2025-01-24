package org.example;

import dao.LibrarianDAO;
import dao.UserDAO;
import entity.LibrariansEntity;
import entity.UsersEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LibrarianTests {

    private EntityManagerFactory emf;
    private EntityManager entityManager;
    private LibrarianDAO librarianDAO;
    private UserDAO userDAO;

    @BeforeAll
    void setUp() {
        emf = Persistence.createEntityManagerFactory("LibraryPersistenceUnit");
        entityManager = emf.createEntityManager();
        librarianDAO = new LibrarianDAO(entityManager);
        userDAO = new UserDAO(entityManager);
    }

    @Test
    void testCreateLibrarian() {
        UsersEntity user = new UsersEntity("username","test@mail.com", "password", "email@test.com");
        userDAO.create(user);

        LibrariansEntity librarian = new LibrariansEntity(user, Date.valueOf("2023-01-01"), "Head Librarian");
        librarianDAO.create(librarian);

        LibrariansEntity fetchedLibrarian = librarianDAO.getById(librarian.getId());

        assertNotNull(fetchedLibrarian);
        assertEquals(librarian.getUserid(), fetchedLibrarian.getUserid());
        assertEquals(librarian.getEmploymentdate(), fetchedLibrarian.getEmploymentdate());
        assertEquals(librarian.getPosition(), fetchedLibrarian.getPosition());
    }

    @Test
    void testGetLibrarianById() {
        UsersEntity user = new UsersEntity("username","test12@mail.com", "password", "email@test.com");
        userDAO.create(user);

        LibrariansEntity librarian = new LibrariansEntity(user, Date.valueOf("2024-02-02"), "Assistant Librarian");
        librarianDAO.create(librarian);

        LibrariansEntity fetchedLibrarian = librarianDAO.getById(librarian.getId());

        assertNotNull(fetchedLibrarian);
        assertEquals(librarian.getId(), fetchedLibrarian.getId());
        assertEquals("Assistant Librarian", fetchedLibrarian.getPosition());
    }

    @Test
    void testGetAllLibrarians() {
        UsersEntity user1 = new UsersEntity("username","test13@mail.com", "password", "email@test.com");
        UsersEntity user2 = new UsersEntity("username","test23@mail.com", "password", "email@test.com");
        userDAO.create(user1);
        userDAO.create(user2);

        LibrariansEntity librarian1 = new LibrariansEntity(user1, Date.valueOf("2020-05-10"), "Librarian");
        LibrariansEntity librarian2 = new LibrariansEntity(user2, Date.valueOf("2021-06-15"), "Senior Librarian");
        librarianDAO.create(librarian1);
        librarianDAO.create(librarian2);

        List<LibrariansEntity> librarians = librarianDAO.getAll();

        assertFalse(librarians.isEmpty());
        assertTrue(librarians.stream().anyMatch(librarian -> librarian.getId() == librarian1.getId()));
        assertTrue(librarians.stream().anyMatch(librarian -> librarian.getId() == librarian2.getId()));
    }

    @Test
    void testUpdateLibrarian() {
        UsersEntity user = new UsersEntity("username","test22@mail.com", "password", "email@test.com");
        userDAO.create(user);

        LibrariansEntity librarian = new LibrariansEntity(user, Date.valueOf("2022-03-03"), "Junior Librarian");
        librarianDAO.create(librarian);

        librarian.setPosition("Library Manager");
        librarianDAO.update(librarian);

        LibrariansEntity updatedLibrarian = librarianDAO.getById(librarian.getId());

        assertNotNull(updatedLibrarian);
        assertEquals("Library Manager", updatedLibrarian.getPosition());
    }

    @Test
    void testDeleteLibrarian() {
        UsersEntity user = new UsersEntity("username","testt@mail.com", "password", "email@test.com");
        userDAO.create(user);

        LibrariansEntity librarian = new LibrariansEntity(user, Date.valueOf("2023-12-12"), "Temporary Librarian");
        librarianDAO.create(librarian);

        librarianDAO.delete(librarian.getId());

        LibrariansEntity deletedLibrarian = librarianDAO.getById(librarian.getId());
        assertNull(deletedLibrarian);
    }
}
