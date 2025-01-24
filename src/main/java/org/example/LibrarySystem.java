package org.example;

import dao.BookDAO;
import dao.BorrowingDAO;
import dao.UserDAO;
import entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The {@code LibrarySystem} class provides functionality to manage the operations related to users, books,
 * borrowings, and publishers in a library system. It supports creating, editing, deleting, and validating
 * user and book records, as well as managing borrowings. This class interacts with the underlying database
 * to perform these operations using {@link EntityManager}.
 *
 * <p>The operations include adding, editing, and deleting users, books, borrowings, and publishers, along
 * with checking the uniqueness of email addresses for users. Additionally, it provides forms for user interaction
 * through a graphical user interface (GUI) built with Swing components.</p>
 *
 * <p>This class assumes the existence of an {@link EntityManagerFactory} and other related entities such as
 * {@link UsersEntity}, {@link BooksEntity}, {@link BorrowingsEntity}, and {@link PublishersEntity}.</p>
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

    /**
     * Database connection used for establishing a direct connection to the H2 database.
     */
    private static Connection connection;

    /**
     * EntityManagerFactory instance used for managing database interactions in the library system.
     * This factory is configured using the "LibraryPersistenceUnit" persistence unit.
     */
    private static final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("LibraryPersistenceUnit");

    /**
     * Represents the currently logged-in user in the library system.
     */
    private static UsersEntity currentUser;

    /**
     * The main entry point for the LibrarySystem application.
     * <p>
     * This method initializes the database connection, sets up the environment, and displays
     * the initial popup window for user interaction.
     * </p>
     *
     * @param args command-line arguments (not used in this application).
     */
    public static void main(String[] args) {

        try {
            // Initialize database connection (example connection)
            connection = DriverManager.getConnection("jdbc:h2:./db/database");

            createPopupWindow();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Creates and displays the popup window that serves as the welcome screen for the library system.
     * <p>
     * This window contains two buttons: "Log In" and "Register." Clicking on the "Log In" button
     * opens the login form, while clicking on the "Register" button opens the registration form.
     * Once either button is clicked, the popup window is closed.
     * </p>
     *
     * The popup window is created using a {@link JFrame} with a {@link FlowLayout}.
     */
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


    /**
     * Creates and displays the registration form for new users of the library system.
     * <p>
     * The form includes fields for entering a user's name, email, phone number, and address.
     * Upon clicking the "Register" button, the form validates the input, ensures the email
     * is unique, and registers the user in the system by saving their details to the database.
     * </p>
     *
     * <ul>
     *     <li>Validates user input to ensure all fields are filled correctly.</li>
     *     <li>Checks the uniqueness of the email address to avoid duplicates.</li>
     *     <li>Registers the user using the {@link UserDAO#create(UsersEntity)} method and
     *     saves the details in the database.</li>
     *     <li>Displays success or error messages depending on the operation outcome.</li>
     *     <li>On successful registration, the main user window is displayed.</li>
     * </ul>
     */
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

    /**
     * Creates and displays the login form for existing users of the library system.
     * <p>
     * The form includes a field for entering the user's email and a "Log In" button.
     * When the user clicks the "Log In" button, the system checks the database for a matching email
     * and retrieves the corresponding user information.
     * </p>
     *
     * <ul>
     *     <li>Validates the email entered by the user and queries the database to find a match.</li>
     *     <li>If a user with the provided email is found, the system determines the user's role
     *     (e.g., librarian or regular user) and redirects them to the appropriate main window.</li>
     *     <li>Updates the `currentUser` field with the logged-in user's information.</li>
     *     <li>Displays an error message if any issues occur during the login process.</li>
     * </ul>
     *
     * <strong>Note:</strong> This method uses both JPA (`EntityManager`) and raw SQL
     * (`PreparedStatement`) to fetch and process user data.
     */
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

    /**
     * Creates and displays the main administrative window for the library management system.
     * <p>
     * This window is designed for librarians or administrators, providing full control over
     * managing users, books, and borrowings. It includes functionality for adding, editing,
     * and deleting records, as well as displaying the existing data in tables.
     * </p>
     *
     * <h3>Features:</h3>
     * <ul>
     *     <li>Navigation buttons for adding, editing, and deleting users, books, and borrowings.</li>
     *     <li>Three separate tables for displaying data: Users, Books, and Borrowings.</li>
     *     <li>Interactive buttons that open specific forms for performing actions on selected records.</li>
     * </ul>
     *
     * <h3>Implementation Details:</h3>
     * <ul>
     *     <li>The navigation panel contains buttons for actions (Add, Edit, Delete) on each entity.</li>
     *     <li>Each table is populated using helper methods such as {@code buildUsersTableModel()},
     *     {@code buildBooksTableModel()}, and {@code buildBorrowingsTableModel()}.</li>
     *     <li>Action listeners are attached to each button to dispose of the current frame and open
     *     the appropriate form for performing the selected operation.</li>
     * </ul>
     *
     * <h3>Table Layout:</h3>
     * <ul>
     *     <li>Users table: Displays registered users in the system.</li>
     *     <li>Books table: Displays books available in the library.</li>
     *     <li>Borrowings table: Displays active and completed borrowings.</li>
     * </ul>
     *
     * <strong>Note:</strong> This method uses {@code JFrame}, {@code JPanel}, {@code JTable}, and
     * {@code JScrollPane} to build the user interface.
     */
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
        editUserButton.addActionListener(e -> {dbFrame.dispose();addEditUserForm();});
        editBookButton.addActionListener(e -> {dbFrame.dispose();addEditBookForm();});
        editBorrowingButton.addActionListener(e -> {dbFrame.dispose();addEditBorrowingForm();});
        deleteUserButton.addActionListener(e -> {dbFrame.dispose();addDeleteUserForm();});
        deleteBookButton.addActionListener(e -> {dbFrame.dispose();addDeleteBookForm();});
        deleteBorrowingButton.addActionListener(e -> {dbFrame.dispose();addDeleteBorrowingForm();});


        // Add the table panel to the frame
        dbFrame.add(tablePanel, BorderLayout.CENTER);

        dbFrame.setVisible(true);
    }

    /**
     * Creates and displays the main user interface for library users.
     * <p>
     * This window is designed for regular users (non-administrators) to view information
     * about books in the library. The interface includes three tables that provide the
     * following details:
     * </p>
     *
     * <ul>
     *     <li><strong>Books:</strong> Displays all books available in the library.</li>
     *     <li><strong>Available Books:</strong> Displays books that are currently available for borrowing.</li>
     *     <li><strong>Borrowed Books:</strong> Displays books currently borrowed by the logged-in user.</li>
     * </ul>
     *
     * <h3>Implementation Details:</h3>
     * <ul>
     *     <li>The interface is created using {@code JFrame} and is divided into two main panels:
     *         <ul>
     *             <li><strong>Text Panel:</strong> Displays titles for each table.</li>
     *             <li><strong>Table Panel:</strong> Displays data in three separate tables, each populated
     *             with data fetched from the database.</li>
     *         </ul>
     *     </li>
     *     <li>Each table is constructed using helper methods:
     *         <ul>
     *             <li>{@code buildBooksTableModel()} - Populates the Books table.</li>
     *             <li>{@code buildAvialableBooksTableModel()} - Populates the Available Books table.</li>
     *             <li>{@code buildBorrowedTableModel()} - Populates the Borrowed Books table.</li>
     *         </ul>
     *     </li>
     * </ul>
     *
     * <h3>Note:</h3>
     * This method focuses on creating a read-only interface for regular users and does not
     * include features for editing or deleting data. The layout uses {@code BorderLayout}
     * for dividing sections and {@code GridLayout} for aligning the tables and labels.
     */
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


    /**
     * Converts a list of lists into a two-dimensional array, suitable for use in a table model.
     * <p>
     * This method takes a {@code List<List<Object>>} representing the data and an array of column names
     * to create a 2D {@code Object[][]} array. Each inner list in the input represents a row, and each
     * element in the inner list represents a cell in the corresponding row.
     * </p>
     *
     * @param lists A list of lists, where each inner list contains the values for a row of the table.
     * @param columns An array of column names, used to define the structure of the resulting 2D array.
     * @return A two-dimensional {@code Object[][]} array representing the table data.
     */
    public static Object[][] buildArr(List<List<Object>> lists, Object[] columns){
        Object[][] dataArray = new Object[lists.size()][columns.length];
        for (int i = 0; i < lists.size(); i++) {
            for (int j = 0; j < lists.get(i).size(); j++) {
                dataArray[i][j] = lists.get(i).get(j);
            }
        }
        return dataArray;
    }

    /**
     * Builds a {@code TableModel} for displaying a list of books in a table.
     * <p>
     * This method retrieves all the books from the database using the {@code BookDAO} and converts
     * the book entities into a table format suitable for use in a {@code JTable}. The method fetches
     * the book information, such as ID, title, author, publisher ID, publication year, and ISBN,
     * and constructs the corresponding table data.
     * </p>
     *
     * @return A {@code TableModel} containing the book data to be displayed in a {@code JTable}.
     */
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

    /**
     * Builds a {@code TableModel} for displaying a list of available books in a table.
     * <p>
     * This method retrieves all books and their copies from the database using the {@code BookDAO} and {@code CopiesEntity}.
     * It checks the status of each copy, and only the books with available copies are included in the table model.
     * The method retrieves book information such as ID, title, author, publisher, publication year, and ISBN,
     * and prepares the data for display in a {@code JTable}.
     * </p>
     *
     * @return A {@code TableModel} containing the data of available books to be displayed in a {@code JTable}.
     */
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

    /**
     * Builds a {@code TableModel} for displaying a list of users in a table.
     * <p>
     * This method retrieves all users from the database using the {@code UserDAO}.
     * It gathers the user's information, such as ID, name, email, phone number, and address,
     * and prepares the data for display in a {@code JTable}.
     * </p>
     *
     * @return A {@code TableModel} containing the data of users to be displayed in a {@code JTable}.
     */
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

    /**
     * Builds a {@code TableModel} for displaying a list of borrowings in a table.
     * <p>
     * This method retrieves all borrowings from the database using the {@code BorrowingDAO}.
     * It gathers the borrowing details, such as the borrowing ID, user ID, copy ID, borrow date,
     * and return date, and prepares the data for display in a {@code JTable}.
     * </p>
     *
     * @return A {@code TableModel} containing the data of borrowings to be displayed in a {@code JTable}.
     */
    public static TableModel buildBorrowingsTableModel() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        BorrowingDAO borrowingDAO = new BorrowingDAO(entityManager);
        Object[] columns = {"id","user id", "copy id", "borrow date", "return date"};
        List<List<Object>> lists = new ArrayList<>();
        for(BorrowingsEntity borrowing : borrowingDAO.getAll())
            lists.add(borrowing.returnAll());
        System.out.println(lists);

        return new DefaultTableModel(buildArr(lists,columns), columns);
    }

    /**
     * Builds a {@code TableModel} for displaying a list of books borrowed by the current user.
     * <p>
     * This method retrieves all borrowings associated with the current user from the database
     * using the {@code BorrowingDAO}. It gathers the borrowing details such as the book's title,
     * author, publisher, publication year, and ISBN, and prepares the data for display in a {@code JTable}.
     * </p>
     *
     * @return A {@code TableModel} containing the data of borrowed books to be displayed in a {@code JTable}.
     */
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


    /**
     * Creates a user interface for adding a new user to the system.
     * <p>
     * This method creates a form where the admin can input details for a new user, such as name, email,
     * phone number, and address. After the user provides the necessary information and clicks the "Add User" button,
     * the system validates the input and checks if the email is unique. If validation passes, the user is added to
     * the system's database. After successful addition, the form closes and the main admin window is reopened.
     * </p>
     *
     * @see #validateUserInput(JTextField, JTextField, JTextField, JTextField)
     * @see #isEmailUnique(String)
     */
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

    /**
     * Creates a user interface for adding a new book to the system.
     * <p>
     * This method creates a form where the admin can input details for a new book, such as title, author,
     * publisher ID, publication year, and ISBN. After the user provides the necessary information and clicks the "Add Book" button,
     * the system validates the publisher's ID, creates a new `BooksEntity` object, and adds it to the database. After successful addition,
     * the form closes and the main admin window is reopened.
     * </p>
     *
     * @see #createMainWindowAdmin()
     */
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
                BooksEntity book = new BooksEntity(titleField.getText(), authorField.getText(), publisher, publicationYField.getText(), isbnField.getText());
                bookDAO.create(book);

                JOptionPane.showMessageDialog(registerFrame, "add successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                registerFrame.dispose();



            } finally {
                entityManager.close();
            }
        });
        registerFrame.setVisible(true);
    }


    /**
     * Creates a user interface for adding a new borrowing record to the system.
     * <p>
     * This method creates a form where the admin can input details for a new borrowing record, such as user ID, copy ID,
     * borrow date, and return date. After the user provides the necessary information and clicks the "Add Borrowing" button,
     * the system validates the copy ID and user ID, parses the provided dates, and creates a new `BorrowingsEntity` object.
     * The new borrowing record is then added to the database. After successful addition, the form closes and the main admin window is reopened.
     * </p>
     *
     * @see #createMainWindowAdmin()
     */
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
                if (!Objects.equals(copy.getStatus(), "Available")){
                    JOptionPane.showMessageDialog(registerFrame, "Copy is already borrowed.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                copy.setStatus("Borrowed");
                entityManager.getTransaction().begin();
                entityManager.persist(copy);
                entityManager.getTransaction().commit();

                UsersEntity user = entityManager.find(UsersEntity.class, useridField.getText());

                // Parsing the date input from the text fields
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date borrowDate = null;
                Date returnDate = null;

                try {
                    java.util.Date utilBorrowDate = dateFormat.parse(borrowDateField.getText());
                    java.util.Date utilReturnDate;
                    if(!Objects.equals(returnDateField.getText(), "null")) {
                        utilReturnDate = dateFormat.parse(returnDateField.getText());
                        returnDate = new java.sql.Date(utilReturnDate.getTime());
                    }

                    // Convert java.util.Date to java.sql.Date
                    borrowDate = new java.sql.Date(utilBorrowDate.getTime());
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


    /**
     * Creates a user interface for editing an existing user record in the system.
     * <p>
     * This method presents a form that allows the admin to input the ID of the user they want to edit, as well as new
     * information for that user's name, email, phone number, and address. After validating the input and ensuring the
     * email is unique, the system retrieves the corresponding user from the database, updates the user's details, and saves
     * the changes. Upon successful editing, a success message is shown and the form closes.
     * </p>
     *
     * @see #createMainWindowAdmin()
     */
    private static void addEditUserForm() {

        JFrame registerFrame = new JFrame("Edit user");
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

        JLabel idLabel = new JLabel("Enter an id of a user to be edited:");
        JTextField idField = new JTextField();

        JLabel nameLabel = new JLabel("Enter a name:");
        JTextField nameField = new JTextField();

        JLabel emailLabel = new JLabel("Enter an email:");
        JTextField emailField = new JTextField();

        JLabel phoneLabel = new JLabel("Enter a phone number:");
        JTextField phoneField = new JTextField();

        JLabel addressLabel = new JLabel("Enter an address:");
        JTextField addressField = new JTextField();

        registerFrame.add(idLabel);
        registerFrame.add(idField);
        registerFrame.add(nameLabel);
        registerFrame.add(nameField);
        registerFrame.add(emailLabel);
        registerFrame.add(emailField);
        registerFrame.add(phoneLabel);
        registerFrame.add(phoneField);
        registerFrame.add(addressLabel);
        registerFrame.add(addressField);

        JButton regButton = new JButton("edit user");
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
                    UsersEntity user = entityManager.find(UsersEntity.class, idField.getText()); // Retrieve user with ID 1
                    user.setName(nameField.getText());
                    user.setEmail(emailField.getText());
                    user.setAddress(addressField.getText());
                    user.setPhonenumber(phoneField.getText());
                    userDAO.update(user);
                    currentUser = user;

                    JOptionPane.showMessageDialog(registerFrame, "edited successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    registerFrame.dispose();



                } finally {
                    entityManager.close();
                }
            }
        });
        registerFrame.setVisible(true);
    }


    /**
     * Creates a user interface for editing an existing book record in the system.
     * <p>
     * This method presents a form that allows the admin to input the ID of the book they want to edit, as well as new
     * information for that book's title, author, publisher, publication year, and ISBN. After validating the input and
     * ensuring the publisher exists, the system retrieves the corresponding book from the database, updates the book's
     * details, and saves the changes. Upon successful editing, a success message is shown and the form closes.
     * </p>
     *
     * @see #createMainWindowAdmin()
     */
    private static void addEditBookForm() {
        JFrame registerFrame = new JFrame("Edit book");
        registerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        registerFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                // Open the main window after the registerFrame is closed
                createMainWindowAdmin();
            }
        });

        registerFrame.setSize(500, 400);
        registerFrame.setLayout(new GridLayout(8, 1, 10, 10));

        JLabel idLabel = new JLabel("Enter an id of book to be edited:");
        JTextField idField = new JTextField();

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

        registerFrame.add(idLabel);
        registerFrame.add(idField);
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

        JButton regButton = new JButton("edit a book");
        registerFrame.add(regButton);

        regButton.addActionListener(e ->  {
            EntityManager entityManager = entityManagerFactory.createEntityManager();
            try {

                PublishersEntity publisher = entityManager.find(PublishersEntity.class, publisherField.getText());

                BookDAO bookDAO = new BookDAO(entityManager);
                BooksEntity book = entityManager.find(BooksEntity.class, idField.getText());
                book.setAuthor(authorField.getText());
                book.setPublisher(publisher);
                book.setTitle(titleField.getText());
                book.setPublicationyear(publicationYField.getText());
                book.setIsbn(isbnField.getText());
                bookDAO.update(book);

                JOptionPane.showMessageDialog(registerFrame, "edited successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                registerFrame.dispose();



            } finally {
                entityManager.close();
            }
        });
        registerFrame.setVisible(true);
    }

    /**
     * Creates a user interface for editing an existing borrowing record in the system.
     * <p>
     * This method presents a form that allows the admin to input the ID of the borrowing record they want to edit, as well
     * as new information for the user ID, copy ID, borrow date, and return date. The system retrieves the corresponding borrowing
     * record from the database and updates it with the new details. If any invalid data is entered, an error message will be shown.
     * Upon successful editing, a success message is displayed, and the form closes.
     * </p>
     *
     * @see #createMainWindowAdmin()
     */

    private static void addEditBorrowingForm() {
        JFrame registerFrame = new JFrame("Edit Borrowing");
        registerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        registerFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                // Open the main window after the registerFrame is closed
                createMainWindowAdmin();
            }
        });

        registerFrame.setSize(500, 400);
        registerFrame.setLayout(new GridLayout(8, 1, 10, 10));

        JLabel borrowingIdLabel = new JLabel("Enter a borrowing id:");
        JTextField borrowingIdField = new JTextField();

        JLabel useridLabel = new JLabel("Enter a user id:");
        JTextField useridField = new JTextField();

        JLabel copyIdLabel = new JLabel("Enter copy id:");
        JTextField copyIdField = new JTextField();

        JLabel borrowDateLabel = new JLabel("Enter borrow date:");
        JTextField borrowDateField = new JTextField();

        JLabel returnDateLabel = new JLabel("Enter return date:");
        JTextField returnDateField = new JTextField();

        registerFrame.add(borrowingIdLabel);
        registerFrame.add(borrowingIdField);
        registerFrame.add(useridLabel);
        registerFrame.add(useridField);
        registerFrame.add(copyIdLabel);
        registerFrame.add(copyIdField);
        registerFrame.add(borrowDateLabel);
        registerFrame.add(borrowDateField);
        registerFrame.add(returnDateLabel);
        registerFrame.add(returnDateField);

        JButton regButton = new JButton("edit borrowing");
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
                BorrowingsEntity borrowing = entityManager.find(BorrowingsEntity.class, borrowingIdField.getText());
                borrowing.setBorrowdate(borrowDate);
                borrowing.setReturndate(returnDate);
                borrowing.setCopyid(copy);
                borrowing.setUserid(user);
                borrowingDAO.update(borrowing);

                JOptionPane.showMessageDialog(registerFrame, "edited successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                registerFrame.dispose();



            } finally {
                entityManager.close();
            }
        });
        registerFrame.setVisible(true);
    }

    /**
     * Creates a user interface for deleting an existing user from the system.
     * <p>
     * This method presents a form that allows the admin to input the user ID of the user they want to delete. Upon clicking the
     * "Delete User" button, the system attempts to locate the user by the given ID. If the user exists, it is removed from the
     * database. In case of invalid input or an error, an appropriate message is displayed.
     * </p>
     * <p>
     * The form validates that a user ID is entered and confirms whether the user exists in the database. If the user is deleted
     * successfully, a success message is shown, and the form closes.
     * </p>
     *
     * @see #createMainWindowAdmin()
     */
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

    /**
     * Creates a user interface for deleting a book from the system.
     * <p>
     * This method presents a form that allows the admin to input the book ID of the book they want to delete. Upon clicking the
     * "Delete Book" button, the system attempts to locate the book by the given ID. If the book exists and is not currently borrowed,
     * it is removed from the database. If the book is being borrowed, deletion is prevented, and an error message is shown.
     * </p>
     * <p>
     * The form validates that a book ID is entered and confirms whether the book exists in the database. If the book is being borrowed,
     * an error message is shown. If the book is deleted successfully, a success message is displayed, and the form is closed.
     * </p>
     *
     * @see #createMainWindowAdmin()
     */
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



                CopiesEntity copyEntity = null;
                try {
                    copyEntity = entityManager.createQuery("SELECT c FROM CopiesEntity c WHERE c.bookid = :book", CopiesEntity.class)
                            .setParameter("book", book)
                            .getSingleResult(); // Will throw NoResultException if no result is found
                } catch (Exception ignored) {
                }

                // Now, query for active borrowings using the CopiesEntity
                String query = "SELECT b FROM BorrowingsEntity b WHERE b.copyid = :copyEntity AND b.returndate IS NULL";
                List<BorrowingsEntity> activeBorrowings = entityManager.createQuery(query, BorrowingsEntity.class)
                        .setParameter("copyEntity", copyEntity)
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


    /**
     * Creates a user interface for deleting a borrowing record from the system.
     * <p>
     * This method presents a form that allows the admin to input the borrowing ID of the borrowing record they want to delete. Upon clicking the
     * "Delete Borrowing" button, the system attempts to locate the borrowing record by the given ID. If the borrowing exists and is not active
     * (i.e., it has a return date), it is removed from the database. If the borrowing is still active, deletion is prevented, and an error message
     * is shown.
     * </p>
     * <p>
     * The form validates that a borrowing ID is entered and confirms whether the borrowing record exists in the database. If the borrowing is
     * still active, an error message is shown. If the borrowing is deleted successfully, a success message is displayed, and the form is closed.
     * </p>
     *
     * @see #createMainWindowAdmin()
     */
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

                CopiesEntity copy = entityManager.find(CopiesEntity.class, borrowing.getCopyid().getId());
                copy.setStatus("Available");

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

    /**
     * Validates the user input in the provided text fields.
     * <p>
     * This method checks if all required fields (name, email, phone, and address) are filled out. It also performs basic validation for the email
     * address, ensuring it contains the "@" symbol. If any field is empty or if the email is invalid, an error message is displayed, and the
     * method returns {@code false}. If all fields are valid, the method returns {@code true}.
     * </p>
     *
     * @param nameField The text field for the user's name.
     * @param emailField The text field for the user's email address.
     * @param phoneField The text field for the user's phone number.
     * @param addressField The text field for the user's address.
     * @return {@code true} if all fields are valid, {@code false} otherwise.
     */
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

    /**
     * Checks if the provided email address is unique in the database.
     * <p>
     * This method queries the database to count how many users already have the specified email address.
     * If the count is 0, meaning the email is not in use, it returns {@code true}. If the email already exists,
     * it returns {@code false}.
     * </p>
     *
     * @param email The email address to be checked for uniqueness.
     * @return {@code true} if the email address is unique (not found in the database),
     *         {@code false} if the email address is already associated with an existing user.
     */
    public static boolean isEmailUnique(String email) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        if(email == null) return false;
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



