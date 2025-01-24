package org.example;

import dao.BookDAO;
import dao.CopyDAO;
import dao.PublisherDAO;
import entity.BooksEntity;
import entity.CopiesEntity;
import entity.PublishersEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CopiesTests {

    private EntityManagerFactory emf;
    private EntityManager entityManager;
    private CopyDAO copiesDAO;
    private PublisherDAO publisherDAO;
    private BookDAO bookDAO;

    @BeforeAll
    void setUp() {
        emf = Persistence.createEntityManagerFactory("LibraryPersistenceUnit");
        entityManager = emf.createEntityManager();
        copiesDAO = new CopyDAO(entityManager);
        publisherDAO = new PublisherDAO(entityManager);
        bookDAO = new BookDAO(entityManager);
    }

    @Test
    void testCreateCopy() {

        BooksEntity book = entityManager.find(BooksEntity.class, 17);

        CopiesEntity copy = new CopiesEntity(book, 1, "available");
        copiesDAO.create(copy);

        CopiesEntity fetchedCopy = entityManager.find(CopiesEntity.class, copy.getId());

        assertNotNull(fetchedCopy);
        assertEquals(copy.getBookid(), fetchedCopy.getBookid());
        assertEquals(copy.getCopynumber(), fetchedCopy.getCopynumber());
        assertEquals(copy.getStatus(), fetchedCopy.getStatus());

    }

    @Test
    void testGetCopyById() {

        PublishersEntity publisher = new PublishersEntity("name","test","test"); // Initialize publisher as needed
        publisherDAO.create(publisher);

        BooksEntity book = new BooksEntity("Delete Me", "John Doe", publisher, "1002", "978-23410");
        bookDAO.create(book);

        CopiesEntity copy = new CopiesEntity(book, 2, "borrowed");
        copiesDAO.create(copy);

        CopiesEntity fetchedCopy = copiesDAO.getById(copy.getId());

        assertNotNull(fetchedCopy);
        assertEquals(copy.getId(), fetchedCopy.getId());
        assertEquals("borrowed", fetchedCopy.getStatus());

    }

    @Test
    void testGetAllCopies() {
        PublishersEntity publisher = new PublishersEntity("name","test","test"); // Initialize publisher as needed
        publisherDAO.create(publisher);

        BooksEntity book = new BooksEntity("Delete Me", "John Doe", publisher, "1002", "978-234510");
        bookDAO.create(book);

        CopiesEntity copy1 = new CopiesEntity(book, 3, "available");
        CopiesEntity copy2 = new CopiesEntity(book, 4, "damaged");

        copiesDAO.create(copy1);
        copiesDAO.create(copy2);

        List<CopiesEntity> copies = copiesDAO.getAll();

        assertFalse(copies.isEmpty());
        assertTrue(copies.stream().anyMatch(copy -> copy.getId() == copy1.getId()));
        assertTrue(copies.stream().anyMatch(copy -> copy.getId() == copy2.getId()));

    }

    @Test
    void testUpdateCopy() {

        PublishersEntity publisher = new PublishersEntity("name","test","test"); // Initialize publisher as needed
        publisherDAO.create(publisher);


        // Create a new book entity for deletion test
        BooksEntity book = new BooksEntity("Delete Me", "John Doe", publisher, "1002", "978-298770");
        bookDAO.create(book);

        CopiesEntity copy = new CopiesEntity(book, 5, "available");
        copiesDAO.create(copy);
        copy.setStatus("borrowed");
        copiesDAO.update(copy);

        CopiesEntity updatedCopy = entityManager.find(CopiesEntity.class, copy.getId());

        assertNotNull(updatedCopy);
        assertEquals("borrowed", updatedCopy.getStatus());

    }

    @Test
    void testDeleteCopy() {

        PublishersEntity publisher = new PublishersEntity("name","test","test"); // Initialize publisher as needed
        publisherDAO.create(publisher);


        // Create a new book entity for deletion test
        BooksEntity book = new BooksEntity("Delete Me", "John Doe", publisher, "1002", "978-200010");
        bookDAO.create(book);

        CopiesEntity copy = new CopiesEntity(book, 6, "available");
        copiesDAO.create(copy);


        copiesDAO.delete(copy.getId());

        CopiesEntity deletedCopy = entityManager.find(CopiesEntity.class, copy.getId());
        assertNull(deletedCopy);

    }
}
