package org.example;

import dao.*;
import entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import java.sql.Date;

/**
 * The Populate class is responsible for populating the database with initial data
 * for testing purposes. It creates sample records for Publishers, Books, Users,
 * Copies, and Borrowings entities and saves them to the database using the
 * corresponding DAO classes.
 *
 * This class uses an EntityManager to handle transactions and interact with the
 * database. It also handles the creation and association of entities.
 */
public class Populate {

    // Define the EntityManagerFactory to use the persistence unit defined in persistence.xml
    private static final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("LibraryPersistenceUnit");

    /**
     * Main method that runs the data population process.
     *
     * This method creates several Publisher, Book, User, Copy, and Borrowing records
     * and persists them to the database in a transaction. If any error occurs,
     * the transaction is rolled back.
     *
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        // Create EntityManager and EntityTransaction for database operations
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            // Begin transaction
            transaction.begin();

            // Create Publisher entities
            PublisherDAO publisherDAO = new PublisherDAO(entityManager);
            PublishersEntity publisher1 = new PublishersEntity("wrwe Books", "123 Penerwguin St.", "555-1322423234");
            publisherDAO.create(publisher1);

            PublishersEntity publisher2 = new PublishersEntity("nameee", "456 weww Rd.", "555-324325678");
            publisherDAO.create(publisher2);

            // Create Book entities
            BookDAO bookDAO = new BookDAO(entityManager);
            BooksEntity book1 = new BooksEntity("The Gatsby", "F. esfs Fitzgerald", publisher1, "1444", "978-02234222743273565");
            bookDAO.create(book1);

            BooksEntity book2 = new BooksEntity("191284", "Orwell", publisher2, "1439", "978-34322222242");
            bookDAO.create(book2);

            // Create User entities
            UserDAO userDAO = new UserDAO(entityManager);
            UsersEntity user1 = new UsersEntity("Mat Doe", "mat1@gmail.com", "555-1234", "123 Main St.");
            userDAO.create(user1);

            UsersEntity user2 = new UsersEntity("Kale Smith", "kale1@yahoo.com", "555-5678", "456 Elm St.");
            userDAO.create(user2);

            // Create Copies of books
            CopyDAO copyDAO = new CopyDAO(entityManager);
            CopiesEntity copy1 = new CopiesEntity(book1, 1011001, "Available");
            copyDAO.create(copy1);

            CopiesEntity copy2 = new CopiesEntity(book2, 1001102, "Borrowed");
            copyDAO.create(copy2);

            // Create Borrowing entities
            BorrowingDAO borrowingDAO = new BorrowingDAO(entityManager);
            BorrowingsEntity borrowing1 = new BorrowingsEntity(user1, copy2, Date.valueOf("2003-01-10"), Date.valueOf("2006-01-20"));
            BorrowingsEntity borrowing2 = new BorrowingsEntity(user1, copy2, Date.valueOf("2000-01-10"), Date.valueOf("2003-01-20"));
            BorrowingsEntity borrowing3 = new BorrowingsEntity(user1, copy2, Date.valueOf("2001-01-10"), Date.valueOf("2003-01-20"));
            borrowingDAO.create(borrowing1);
            borrowingDAO.create(borrowing3);
            borrowingDAO.create(borrowing2);

            // Commit transaction if all operations are successful
            transaction.commit();
        } catch (Exception e) {
            // Rollback transaction in case of failure
            transaction.rollback();
            e.printStackTrace();
        } finally {
            // Ensure the EntityManager is closed
            entityManager.close();
        }
    }
}
