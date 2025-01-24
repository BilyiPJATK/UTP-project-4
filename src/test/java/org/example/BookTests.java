package org.example;

import dao.BookDAO;
import dao.CopyDAO;
import dao.PublisherDAO;
import dao.UserDAO;
import entity.BooksEntity;
import entity.CopiesEntity;
import entity.PublishersEntity;
import entity.UsersEntity;
import jakarta.persistence.*;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BookTests {

    private static EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private BookDAO bookDAO;
    private PublisherDAO publisherDAO;
    private CopyDAO copyDAO;

    @BeforeEach
    public void setUp() {
        entityManagerFactory = Persistence.createEntityManagerFactory("LibraryPersistenceUnit");
        entityManager = entityManagerFactory.createEntityManager();
        bookDAO = new BookDAO(entityManager);
        publisherDAO = new PublisherDAO(entityManager);
        copyDAO = new CopyDAO(entityManager);
    }

    @Test
    public void testCreateBook() {
        // Create a new book entity
        PublishersEntity publisher = new PublishersEntity("Publisher 1", "Address 1", "123-456-7890");
        publisherDAO.create(publisher);

        BooksEntity book = new BooksEntity("The Great Book", "John Doe", publisher, "1001", "978-36490");
        bookDAO.create(book);

        // Ensure the book is correctly saved by fetching it back
        BooksEntity retrievedBook = entityManager.find(BooksEntity.class, book.getId());
        assertNotNull(retrievedBook);
        assertEquals(book.getTitle(), retrievedBook.getTitle());
        assertEquals(book.getAuthor(), retrievedBook.getAuthor());
        assertEquals(book.getIsbn(), retrievedBook.getIsbn());
    }

    @Test
    public void testReadBook() {
        // Ensure a book exists in the database and test reading it
        BooksEntity book = entityManager.find(BooksEntity.class, 10);
        assertNotNull(book, "Book should be found in the database.");
        assertEquals("The Great Gatsby", book.getTitle(), "The title should match.");
    }

    @Test
    public void testUpdateBook() {
        // Retrieve an existing book from the database
        BooksEntity book = entityManager.find(BooksEntity.class, 17);
        String newTitle = "Updated Book Title";
        book.setTitle(newTitle);

        // Begin transaction and update the book
        bookDAO.update(book);

        // Retrieve the updated book and verify the changes
        BooksEntity updatedBook = entityManager.find(BooksEntity.class, 17);
        assertNotNull(updatedBook, "Updated book should be found in the database.");
        assertEquals(newTitle, updatedBook.getTitle(), "The title should be updated.");
    }

    @Test
    public void testDeleteBook() {

        PublishersEntity publisher = new PublishersEntity("name","test","test"); // Initialize publisher as needed
        publisherDAO.create(publisher);


        // Create a new book entity for deletion test
        BooksEntity book = new BooksEntity("Delete Me", "John Doe", publisher, "1002", "978-23410");

        // Begin transaction and persist the book
        bookDAO.create(book);

        // Verify the book is in the database
        BooksEntity savedBook = entityManager.find(BooksEntity.class, book.getId());
        assertNotNull(savedBook);

        // Begin transaction and delete the book
        bookDAO.delete(savedBook.getId());

        // Verify the book is deleted
        BooksEntity deletedBook = entityManager.find(BooksEntity.class, book.getId());
        assertNull(deletedBook);
    }

    @Test
    public void testDeleteBookWithRelationships() {

        BooksEntity book = entityManager.find(BooksEntity.class, 11);

        if (book == null) {
            fail("Book with id 11 not found. Ensure the test data exists.");
        }

        // Try to delete the book
        try {
            // Attempt to delete the book
            bookDAO.delete(book.getId());

            // Check if the book still exists after the delete attempt
            BooksEntity deletedBook = entityManager.find(BooksEntity.class, 11);

            assertNotEquals(deletedBook,null);
        } catch (PersistenceException e) {
            System.out.println(e);
        }
    }



}
