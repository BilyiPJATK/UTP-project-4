package org.example;

import dao.BookDAO;
import dao.BorrowingDAO;
import dao.CopyDAO;
import dao.UserDAO;
import entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.w3c.dom.Entity;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Field;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * The LibrarySystem class is responsible for handling the main user interface
 * and user interactions in a library management system. This includes user
 * registration, login, and creating different views for normal users and
 * administrators (librarians).
 * <p>
 * The system interacts with a database to manage users, books, and borrowings.
 * The UI is built using Swing components, and all operations are executed
 * in a transactional context using the EntityManager.
 */

public class LibrarySystem {

    static {
        try {
            Class.forName("org.h2.Driver");
            System.out.println("Driver loaded");
        } catch (ClassNotFoundException e) {
            System.out.println("Driver not found");
        }
    }

    private static Connection connection;
    private static final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("LibraryPersistenceUnit");
    private static UsersEntity currentUser;

    public static void main(String[] args) {

        try {
            // Initialize database connection (example connection)
            connection = DriverManager.getConnection("jdbc:h2:./db/database");

            createPopupWindow();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    private static void createPopupWindow() {
        JFrame popupFrame = new JFrame("Welcome");
        popupFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        popupFrame.setSize(300, 150);
        popupFrame.setLayout(new FlowLayout());

        JButton loginButton = new JButton("Log In");
        loginButton.addActionListener(e -> {
            createLoginForm();
            popupFrame.dispose();
        });

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> {
            createRegisterForm();
            popupFrame.dispose();
        });

        popupFrame.add(loginButton);
        popupFrame.add(registerButton);

        popupFrame.setVisible(true);
    }


    private static void createRegisterForm() {
        JFrame registerFrame = new JFrame("Register");
        registerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        registerFrame.setSize(500, 500);
        registerFrame.setLayout(new GridLayout(6, 1, 10, 10));

        JLabel nameLabel = new JLabel("Enter your name:");
        JTextField nameField = new JTextField();

        JLabel emailLabel = new JLabel("Enter your email:");
        JTextField emailField = new JTextField();

        JLabel phoneLabel = new JLabel("Enter your phone number:");
        JTextField phoneField = new JTextField();

        JLabel addressLabel = new JLabel("Enter your address:");
        JTextField addressField = new JTextField();

        registerFrame.add(nameLabel);
        registerFrame.add(nameField);
        registerFrame.add(emailLabel);
        registerFrame.add(emailField);
        registerFrame.add(phoneLabel);
        registerFrame.add(phoneField);
        registerFrame.add(addressLabel);
        registerFrame.add(addressField);

        JButton regButton = new JButton("Register");
        registerFrame.add(regButton);

        regButton.addActionListener(e -> {
            if (validateUserInput(nameField, emailField, phoneField, addressField)) {
                if (!isEmailUnique(emailField.getText())) {
                    JOptionPane.showMessageDialog(null, "Email is already in use. Please choose another one.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                EntityManager entityManager = entityManagerFactory.createEntityManager();
                try {

                    UserDAO userDAO = new UserDAO(entityManager);
                    UsersEntity user = new UsersEntity(nameField.getText(), emailField.getText(), phoneField.getText(), addressField.getText());
                    userDAO.create(user);
                    currentUser = user;

                    JOptionPane.showMessageDialog(registerFrame, "Registration successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    registerFrame.dispose();

                    createMainWindowUser();


                } finally {
                    entityManager.close();
                }
            }
        });

        registerFrame.setVisible(true);
    }

    private static void createLoginForm() {
        JFrame loginFrame = new JFrame("Log In");
        loginFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        loginFrame.setSize(500, 400);
        loginFrame.setLayout(new GridLayout(3, 2, 10, 10));

        JLabel emailLabel = new JLabel("Enter your email:");
        JTextField emailField = new JTextField();

        loginFrame.add(emailLabel);
        loginFrame.add(emailField);

        JButton loginButton = new JButton("log in");
        loginFrame.add(loginButton);

        loginButton.addActionListener(e -> {
            EntityManager entityManager = entityManagerFactory.createEntityManager();

            try {
                try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT u.id FROM USERS u WHERE u.email = ?")) {
                    preparedStatement.setString(1, emailField.getText());
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {

                            UsersEntity user = entityManager.find(UsersEntity.class, resultSet.getInt("id"));
                            currentUser = user;
                            if (user.getLibrarian() != null) {
                                createMainWindowAdmin();
                            } else {
                                createMainWindowUser();
                            }

                            loginFrame.dispose();
                        }
                    }
                }
            } catch( Exception ex) {
                ex.printStackTrace();
            } finally {
                entityManager.close();
            }
        });

        loginFrame.setVisible(true);
    }

    private static void createMainWindowAdmin() {
        JFrame dbFrame = new JFrame("Library Management");
        dbFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dbFrame.setSize(1000, 800);
        dbFrame.setLayout(new BorderLayout());

        // Panel to hold the navigation buttons (Add, Edit, Delete)
        JPanel navigationPanel = new JPanel(new GridLayout(1, 3));
        JButton addUserButton = new JButton("Add User");
        JButton addBookButton = new JButton("Add Book");
        JButton addBorrowingButton = new JButton("Add Borrowing");
        JButton editUserButton = new JButton("Edit User");
        JButton editBookButton = new JButton("Edit Book");
        JButton editBorrowingButton = new JButton("Edit Borrowing");
        JButton deleteUserButton = new JButton("Delete User");
        JButton deleteBookButton = new JButton("Delete Book");
        JButton deleteBorrowingButton = new JButton("Delete Borrowing");

        // Add buttons to the panel
        navigationPanel.add(addUserButton);
        navigationPanel.add(addBookButton);
        navigationPanel.add(addBorrowingButton);
        navigationPanel.add(editUserButton);
        navigationPanel.add(editBookButton);
        navigationPanel.add(editBorrowingButton);
        navigationPanel.add(deleteUserButton);
        navigationPanel.add(deleteBookButton);
        navigationPanel.add(deleteBorrowingButton);

        // Add navigation panel to the frame
        dbFrame.add(navigationPanel, BorderLayout.NORTH);

        // Panel to display tables
        JPanel tablePanel = new JPanel(new GridLayout(1, 3));

        // Create tables for Users, Books, and Borrowings
        JTable userTable = new JTable(buildUsersTableModel());
        JTable bookTable = new JTable(buildBooksTableModel());
        JTable borrowingTable = new JTable(buildBorrowingsTableModel());

        // Add the tables to the table panel
        JScrollPane userScrollPane = new JScrollPane(userTable);
        JScrollPane bookScrollPane = new JScrollPane(bookTable);
        JScrollPane borrowingScrollPane = new JScrollPane(borrowingTable);

        tablePanel.add(userScrollPane);
        tablePanel.add(bookScrollPane);
        tablePanel.add(borrowingScrollPane);

        // Add action listeners for buttons (for "Add", "Edit", "Delete" actions)
        addUserButton.addActionListener(e -> {dbFrame.dispose(); addUserForm();});
        addBookButton.addActionListener(e -> {dbFrame.dispose();addBookForm();});
        addBorrowingButton.addActionListener(e -> {dbFrame.dispose();addBorrowingForm();});
//        editUserButton.addActionListener();
//        editBookButton.addActionListener();
//        editBorrowingButton.addActionListener();
        deleteUserButton.addActionListener(e -> {dbFrame.dispose();addDeleteUserForm();});
        deleteBookButton.addActionListener(e -> {dbFrame.dispose();addDeleteBookForm();});
        deleteBorrowingButton.addActionListener(e -> {dbFrame.dispose();addDeleteBorrowingForm();});


        // Add the table panel to the frame
        dbFrame.add(tablePanel, BorderLayout.CENTER);

        dbFrame.setVisible(true);
    }
    private static void createMainWindowUser() {
        JFrame dbFrame = new JFrame("Library interface");
        dbFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dbFrame.setSize(1000, 800);
        dbFrame.setLayout(new BorderLayout());

        // Panel to display tables
        JPanel tablePanel = new JPanel(new GridLayout(1, 3));


        // Populate tables with data from the database
        JTable bookTable = new JTable(buildBooksTableModel());
        JTable availableTable = new JTable(buildAvialableBooksTableModel());
        JTable borrowedTable = new JTable(buildBorrowedTableModel());


        JPanel textPanel = new JPanel(new GridLayout(1, 3));
        JLabel bookTitleLabel = new JLabel("Books");
        JLabel availableTitleLabel = new JLabel("Available Books");
        JLabel borrowedTitleLabel = new JLabel("Borrowed Books");
        textPanel.add(bookTitleLabel);
        textPanel.add(availableTitleLabel);
        textPanel.add(borrowedTitleLabel);


        // Add the tables to the table panel
        JScrollPane bookScrollPane = new JScrollPane(bookTable);
        JScrollPane availableScrollPane = new JScrollPane(availableTable);
        JScrollPane borrowedScrollPane = new JScrollPane(borrowedTable);

        tablePanel.add(bookScrollPane);
        tablePanel.add(availableScrollPane);
        tablePanel.add(borrowedScrollPane);

        dbFrame.add(textPanel, BorderLayout.NORTH);
        dbFrame.add(tablePanel, BorderLayout.CENTER);

        dbFrame.setVisible(true);
    }



    public static Object[][] buildArr(List<List<Object>> lists, Object[] columns){
        Object[][] dataArray = new Object[lists.size()][columns.length];
        for (int i = 0; i < lists.size(); i++) {
            for (int j = 0; j < lists.get(i).size(); j++) {
                dataArray[i][j] = lists.get(i).get(j);
            }
        }
        return dataArray;
    }
    public static TableModel buildBooksTableModel() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        BookDAO bookDAO = new BookDAO(entityManager);
        Object[] columns = {"id", "title", "author", "publisher id", "publication year", "isbn"};
        List<List<Object>> lists = new ArrayList<>();
        for(BooksEntity entity : bookDAO.getAll())
            lists.add(entity.returnAll());
        System.out.println(lists);

        return new DefaultTableModel(buildArr(lists,columns), columns);
    }
    public static TableModel buildAvialableBooksTableModel() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        BookDAO copyDAO = new BookDAO(entityManager);
        Object[] columns = {"id", "title", "author", "publisher", "publication year", "isbn"};
        List<List<Object>> lists = new ArrayList<>();
        for(BooksEntity book : copyDAO.getAll()) {
             for(CopiesEntity copy : book.returnCopy()){
                if (copy.getStatus().equals("Available"))
                    lists.add(book.returnAll());
             }
        }
        System.out.println(lists);

        return new DefaultTableModel(buildArr(lists,columns), columns);
    }
    public static TableModel buildUsersTableModel() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        UserDAO userDAO = new UserDAO(entityManager);
        Object[] columns = {"id", "name", "email", "phone number", "address"};
        List<List<Object>> lists = new ArrayList<>();
        for(UsersEntity entity : userDAO.getAll())
            lists.add(entity.returnAll());
        System.out.println(lists);

        return new DefaultTableModel(buildArr(lists,columns), columns);
    }
    public static TableModel buildBorrowingsTableModel() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        BorrowingDAO borrowingDAO = new BorrowingDAO(entityManager);
        Object[] columns = {"id", "copy id", "author id", "publisher", "publication year"};
        List<List<Object>> lists = new ArrayList<>();
        for(BorrowingsEntity borrowing : borrowingDAO.getAll())
            lists.add(borrowing.returnAll());
        System.out.println(lists);

        return new DefaultTableModel(buildArr(lists,columns), columns);
    }
    public static TableModel buildBorrowedTableModel() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        BorrowingDAO borrowingDAO = new BorrowingDAO(entityManager);
        Object[] columns = {"id", "title", "author", "publisher", "publication year", "isbn"};
        List<List<Object>> lists = new ArrayList<>();
        UsersEntity user = entityManager.find(UsersEntity.class, currentUser.getId());
            for (BorrowingsEntity borrowing : user.returnBorriwings())
                lists.add(borrowing.returnAll());
        System.out.println(lists);

        return new DefaultTableModel(buildArr(lists,columns), columns);
    }

    private static void addUserForm() {

        JFrame registerFrame = new JFrame("Add user");
        registerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        registerFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                // Open the main window after the registerFrame is closed
                createMainWindowAdmin();
            }
        });
        registerFrame.setSize(500, 400);
        registerFrame.setLayout(new GridLayout(6, 1, 10, 10));

        JLabel nameLabel = new JLabel("Enter a name:");
        JTextField nameField = new JTextField();

        JLabel emailLabel = new JLabel("Enter an email:");
        JTextField emailField = new JTextField();

        JLabel phoneLabel = new JLabel("Enter a phone number:");
        JTextField phoneField = new JTextField();

        JLabel addressLabel = new JLabel("Enter an address:");
        JTextField addressField = new JTextField();

        registerFrame.add(nameLabel);
        registerFrame.add(nameField);
        registerFrame.add(emailLabel);
        registerFrame.add(emailField);
        registerFrame.add(phoneLabel);
        registerFrame.add(phoneField);
        registerFrame.add(addressLabel);
        registerFrame.add(addressField);

        JButton regButton = new JButton("add user");
        registerFrame.add(regButton);

        regButton.addActionListener(e ->  {
            if (validateUserInput(nameField, emailField, phoneField, addressField)) {
                if (!isEmailUnique(emailField.getText())) {
                    JOptionPane.showMessageDialog(null, "Email is already in use. Please choose another one.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                EntityManager entityManager = entityManagerFactory.createEntityManager();
                try {

                    UserDAO userDAO = new UserDAO(entityManager);
                    UsersEntity user = new UsersEntity(nameField.getText(), emailField.getText(), phoneField.getText(), addressField.getText());
                    userDAO.create(user);
                    currentUser = user;

                    JOptionPane.showMessageDialog(registerFrame, "add successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    registerFrame.dispose();




                } finally {
                    entityManager.close();
                }
            }
        });
        registerFrame.setVisible(true);
    }

    private static void addBookForm() {
        JFrame registerFrame = new JFrame("Add book");
        registerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        registerFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                // Open the main window after the registerFrame is closed
                createMainWindowAdmin();
            }
        });

        registerFrame.setSize(500, 400);
        registerFrame.setLayout(new GridLayout(7, 1, 10, 10));

        JLabel titleLabel = new JLabel("Enter a title:");
        JTextField titleField = new JTextField();

        JLabel authorLabel = new JLabel("Enter author's name:");
        JTextField authorField = new JTextField();

        JLabel publisherLabel = new JLabel("Enter publisher's id:");
        JTextField publisherField = new JTextField();

        JLabel publicationYLabel = new JLabel("Enter publication year:");
        JTextField publicationYField = new JTextField();

        JLabel isbnLabel = new JLabel("Enter isbn:");
        JTextField isbnField = new JTextField();

        registerFrame.add(titleLabel);
        registerFrame.add(titleField);
        registerFrame.add(authorLabel);
        registerFrame.add(authorField);
        registerFrame.add(publisherLabel);
        registerFrame.add(publisherField);
        registerFrame.add(publicationYLabel);
        registerFrame.add(publicationYField);
        registerFrame.add(isbnLabel);
        registerFrame.add(isbnField);

        JButton regButton = new JButton("add a book");
        registerFrame.add(regButton);

        regButton.addActionListener(e ->  {
            EntityManager entityManager = entityManagerFactory.createEntityManager();
            try {

                PublishersEntity publisher = entityManager.find(PublishersEntity.class, publisherField.getText());

                BookDAO bookDAO = new BookDAO(entityManager);
                BooksEntity user = new BooksEntity(titleField.getText(), authorField.getText(), publisher, publicationYField.getText(), isbnField.getText());
                bookDAO.create(user);

                JOptionPane.showMessageDialog(registerFrame, "add successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                registerFrame.dispose();




            } finally {
                entityManager.close();
            }
        });
        registerFrame.setVisible(true);
    }

    private static void addBorrowingForm() {
        JFrame registerFrame = new JFrame("Add Borrowing");
        registerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        registerFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                // Open the main window after the registerFrame is closed
                createMainWindowAdmin();
            }
        });

        registerFrame.setSize(500, 400);
        registerFrame.setLayout(new GridLayout(7, 1, 10, 10));

        JLabel useridLabel = new JLabel("Enter a user id:");
        JTextField useridField = new JTextField();

        JLabel copyIdLabel = new JLabel("Enter copy id:");
        JTextField copyIdField = new JTextField();

        JLabel borrowDateLabel = new JLabel("Enter borrow date:");
        JTextField borrowDateField = new JTextField();

        JLabel returnDateLabel = new JLabel("Enter return date:");
        JTextField returnDateField = new JTextField();

        registerFrame.add(useridLabel);
        registerFrame.add(useridField);
        registerFrame.add(copyIdLabel);
        registerFrame.add(copyIdField);
        registerFrame.add(borrowDateLabel);
        registerFrame.add(borrowDateField);
        registerFrame.add(returnDateLabel);
        registerFrame.add(returnDateField);

        JButton regButton = new JButton("add borrowing");
        registerFrame.add(regButton);

        regButton.addActionListener(e ->  {
            EntityManager entityManager = entityManagerFactory.createEntityManager();
            try {
                CopiesEntity copy = entityManager.find(CopiesEntity.class, copyIdField.getText());

                if (copy == null) {
                    JOptionPane.showMessageDialog(registerFrame, "Copy ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                UsersEntity user = entityManager.find(UsersEntity.class, useridField.getText());

                // Parsing the date input from the text fields
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date borrowDate = null;
                Date returnDate = null;

                try {
                    java.util.Date utilBorrowDate = dateFormat.parse(borrowDateField.getText());
                    java.util.Date utilReturnDate = dateFormat.parse(returnDateField.getText());

                    // Convert java.util.Date to java.sql.Date
                    borrowDate = new java.sql.Date(utilBorrowDate.getTime());
                    returnDate = new java.sql.Date(utilReturnDate.getTime());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(registerFrame, "Invalid date format. Please use yyyy-MM-dd.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                BorrowingDAO borrowingDAO = new BorrowingDAO(entityManager);
                BorrowingsEntity borrowings = new BorrowingsEntity(user, copy, borrowDate, returnDate);
                borrowingDAO.create(borrowings);

                JOptionPane.showMessageDialog(registerFrame, "add successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                registerFrame.dispose();




            } finally {
                entityManager.close();
            }
        });
        registerFrame.setVisible(true);
    }
    private static void addDeleteUserForm(){
        JFrame deleteFrame = new JFrame("Delete User");
        deleteFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        deleteFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                // Open the main window after the registerFrame is closed
                createMainWindowAdmin();
            }
        });
        deleteFrame.setSize(400, 200);
        deleteFrame.setLayout(new GridLayout(3, 1, 10, 10));

        // Input field for user ID
        JLabel userIdLabel = new JLabel("Enter User ID:");
        JTextField userIdField = new JTextField();

        // Button to delete the user
        JButton deleteButton = new JButton("Delete User");

        // Add components to the frame
        deleteFrame.add(userIdLabel);
        deleteFrame.add(userIdField);
        deleteFrame.add(deleteButton);

            deleteButton.addActionListener(e -> {
                String userId = userIdField.getText().trim();

                // Check if the user ID is empty
                if (userId.isEmpty()) {
                    JOptionPane.showMessageDialog(deleteFrame, "Please enter a valid user ID.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Create the entity manager and begin the transaction
                EntityManager entityManager = entityManagerFactory.createEntityManager();
                try {
                    entityManager.getTransaction().begin();

                    // Find the user by ID
                    UsersEntity user = entityManager.find(UsersEntity.class, userId);

                    // If the user doesn't exist
                    if (user == null) {
                        JOptionPane.showMessageDialog(deleteFrame, "User not found.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Remove the user from the database
                    entityManager.remove(user);

                    // Commit the transaction
                    entityManager.getTransaction().commit();

                    // Show success message
                    JOptionPane.showMessageDialog(deleteFrame, "User deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

                    // Close the delete frame
                    deleteFrame.dispose();




                } catch (Exception ex) {
                    // Rollback in case of an error
                    entityManager.getTransaction().rollback();
                    JOptionPane.showMessageDialog(deleteFrame, "Error deleting user: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    // Close the entity manager
                    entityManager.close();
                }
            });
        deleteFrame.setVisible(true);
    }
    private static void addDeleteBookForm() {
        JFrame deleteFrame = new JFrame("Delete Book");
        deleteFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        deleteFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                // Open the main window after the deleteFrame is closed
                createMainWindowAdmin();
            }
        });
        deleteFrame.setSize(400, 200);
        deleteFrame.setLayout(new GridLayout(3, 1, 10, 10));

        // Input field for book ID
        JLabel bookIdLabel = new JLabel("Enter Book ID:");
        JTextField bookIdField = new JTextField();

        // Button to delete the book
        JButton deleteButton = new JButton("Delete Book");

        // Add components to the frame
        deleteFrame.add(bookIdLabel);
        deleteFrame.add(bookIdField);
        deleteFrame.add(deleteButton);

        deleteButton.addActionListener(e -> {
            String bookId = bookIdField.getText().trim();

            // Check if the book ID is empty
            if (bookId.isEmpty()) {
                JOptionPane.showMessageDialog(deleteFrame, "Please enter a valid book ID.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create the entity manager and begin the transaction
            EntityManager entityManager = entityManagerFactory.createEntityManager();
            try {
                entityManager.getTransaction().begin();

                // Find the book by ID
                BooksEntity book = entityManager.find(BooksEntity.class, bookId);

                // If the book doesn't exist
                if (book == null) {
                    JOptionPane.showMessageDialog(deleteFrame, "Book not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Check if the book is currently borrowed
                String query = "SELECT b FROM BorrowingsEntity b WHERE b.copyid = :book AND b.returndate IS NULL";
                List<BorrowingsEntity> activeBorrowings = entityManager.createQuery(query, BorrowingsEntity.class)
                        .setParameter("book", book)
                        .getResultList();

                if (!activeBorrowings.isEmpty()) {
                    // If the book is currently borrowed, prevent deletion
                    JOptionPane.showMessageDialog(deleteFrame, "Cannot delete the book because it is currently borrowed.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Remove the book from the database
                entityManager.remove(book);

                // Commit the transaction
                entityManager.getTransaction().commit();

                // Show success message
                JOptionPane.showMessageDialog(deleteFrame, "Book deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

                // Close the delete frame
                deleteFrame.dispose();




            } catch (Exception ex) {
                // Rollback in case of an error
                entityManager.getTransaction().rollback();
                JOptionPane.showMessageDialog(deleteFrame, "Error deleting book: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                // Close the entity manager
                entityManager.close();
            }
        });

        deleteFrame.setVisible(true);
    }

    private static void addDeleteBorrowingForm() {
        JFrame deleteFrame = new JFrame("Delete Borrowing");
        deleteFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        deleteFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                // Open the main window after the deleteFrame is closed
                createMainWindowAdmin();
            }
        });
        deleteFrame.setSize(400, 200);
        deleteFrame.setLayout(new GridLayout(3, 1, 10, 10));

        // Input field for borrowing ID
        JLabel borrowingIdLabel = new JLabel("Enter Borrowing ID:");
        JTextField borrowingIdField = new JTextField();

        // Button to delete the borrowing
        JButton deleteButton = new JButton("Delete Borrowing");

        // Add components to the frame
        deleteFrame.add(borrowingIdLabel);
        deleteFrame.add(borrowingIdField);
        deleteFrame.add(deleteButton);

        deleteButton.addActionListener(e -> {
            String borrowingId = borrowingIdField.getText().trim();

            // Check if the borrowing ID is empty
            if (borrowingId.isEmpty()) {
                JOptionPane.showMessageDialog(deleteFrame, "Please enter a valid borrowing ID.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create the entity manager and begin the transaction
            EntityManager entityManager = entityManagerFactory.createEntityManager();
            try {
                entityManager.getTransaction().begin();

                // Find the borrowing by ID
                BorrowingsEntity borrowing = entityManager.find(BorrowingsEntity.class, borrowingId);

                // If the borrowing doesn't exist
                if (borrowing == null) {
                    JOptionPane.showMessageDialog(deleteFrame, "Borrowing not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Check if the borrowing is still active (this is optional, depending on your requirements)
                if (borrowing.getReturndate() == null) {
                    JOptionPane.showMessageDialog(deleteFrame, "Cannot delete an active borrowing (not returned yet).", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Remove the borrowing from the database
                entityManager.remove(borrowing);

                // Commit the transaction
                entityManager.getTransaction().commit();

                // Show success message
                JOptionPane.showMessageDialog(deleteFrame, "Borrowing deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

                // Close the delete frame
                deleteFrame.dispose();




            } catch (Exception ex) {
                // Rollback in case of an error
                entityManager.getTransaction().rollback();
                JOptionPane.showMessageDialog(deleteFrame, "Error deleting borrowing: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                // Close the entity manager
                entityManager.close();
            }
        });

        deleteFrame.setVisible(true);
    }


    private static boolean validateUserInput(JTextField nameField, JTextField emailField, JTextField phoneField, JTextField addressField) {
        if (nameField.getText().trim().isEmpty() || emailField.getText().trim().isEmpty() || phoneField.getText().trim().isEmpty() || addressField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "All fields must be filled out.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        // Simple email validation
        if (!emailField.getText().contains("@")) {
            JOptionPane.showMessageDialog(null, "Invalid email address.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
    public static boolean isEmailUnique(String email) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            // Query the database to check if the email already exists
            Long count = entityManager.createQuery(
                            "SELECT COUNT(u) FROM UsersEntity u WHERE u.email = :email", Long.class)
                    .setParameter("email", email)
                    .getSingleResult();

            // Return true if no record is found with the given email
            return count == 0;
        } finally {
            entityManager.close();
        }
    }
}



