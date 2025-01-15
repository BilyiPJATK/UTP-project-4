package org.example;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.List;

public class LibraryGUI {
    static Connection connection;
    static Boolean statusAdmin;

    static {
        try {
            Class.forName("org.h2.Driver");
            System.out.println("Driver loaded");
        } catch (ClassNotFoundException e) {
            System.out.println("Driver not found");
        }
    }
    public static void main(String[] args) throws SQLException {

        connection = DriverManager.getConnection("jdbc:h2:./db/database");

        SwingUtilities.invokeLater(LibraryGUI::createPopupWindow);

    }

    private static void createMainWindowAdmin() {
        JFrame dbFrame = new JFrame("Library Management");
        dbFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dbFrame.setSize(1000, 800);
        dbFrame.setLayout(new BorderLayout());

        // Panel to hold the navigation buttons (Add, Edit, Delete)
        JPanel navigationPanel = new JPanel(new GridLayout(3, 3));
        JButton addUserButton = new JButton("Add User");
        JButton addBookButton = new JButton("Add Book");
        JButton addBorrowingButton = new JButton("Add Borrowing");
        JButton editUserButton = new JButton("Edit User");
        JButton editBookButton = new JButton("Edit Book");
        JButton editBorrowingButton = new JButton("Edit Borrowing");
        JButton deleteUserButton = new JButton("Delete User");
        JButton deleteBookButton = new JButton("Delete Book");
        JButton deleteBorrowingButton = new JButton("Delete Borrowing");

        navigationPanel.add(addUserButton);
        navigationPanel.add(addBookButton);
        navigationPanel.add(addBorrowingButton);
        navigationPanel.add(editUserButton);
        navigationPanel.add(editBookButton);
        navigationPanel.add(editBorrowingButton);
        navigationPanel.add(deleteUserButton);
        navigationPanel.add(deleteBookButton);
        navigationPanel.add(deleteBorrowingButton);

        dbFrame.add(navigationPanel, BorderLayout.NORTH);

        // Panel to display tables
        JPanel tablePanel = new JPanel(new GridLayout(1, 3));

        // Create tables for Users, Books, and Borrowings

        // Populate tables with data from the database
        JTable userTable = new JTable(populateUserTable());
        JTable bookTable = new JTable(populateBookTable());
        JTable borrowingTable = new JTable(populateBorrowingTable());


        // Add the tables to the table panel
        JScrollPane userScrollPane = new JScrollPane(userTable);
        JScrollPane bookScrollPane = new JScrollPane(bookTable);
        JScrollPane borrowingScrollPane = new JScrollPane(borrowingTable);

        tablePanel.add(userScrollPane);
        tablePanel.add(bookScrollPane);
        tablePanel.add(borrowingScrollPane);

        // Action listeners for buttons
        addUserButton.addActionListener(e -> {dbFrame.dispose();createUserForm();});
        addBookButton.addActionListener(e -> {dbFrame.dispose();createBookForm();});
        addBorrowingButton.addActionListener(e -> {dbFrame.dispose();createBorrowingForm();});
        deleteUserButton.addActionListener(e -> {dbFrame.dispose();deleteUserForm();});


        dbFrame.add(tablePanel, BorderLayout.CENTER);

        dbFrame.setVisible(true);
    }

    private static void createMainWindowUser(int userID){
        JFrame dbFrame = new JFrame("Library interface");
        dbFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dbFrame.setSize(1000, 800);
        dbFrame.setLayout(new BorderLayout());

        // Panel to display tables
        JPanel tablePanel = new JPanel(new GridLayout(1, 3));


        // Populate tables with data from the database
        JTable bookTable = new JTable(populateBookTable());
        JTable availableTable = new JTable(populateAvailableBooksTable());
        JTable borrowedTable = new JTable(populateBorrowedBooksTable(userID));


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

    // Method to populate the User table
    private static TableModel populateUserTable() {
        String[] columnNames = {"ID", "Name", "Email", "Phone Number", "Address"};
        List<List<Object>> rows = new ArrayList<>();

        try {
            String query = "SELECT id, name, email, phonenumber, address FROM USERS";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                List<Object> row = new ArrayList<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("name"));
                row.add(rs.getString("email"));
                row.add(rs.getString("phonenumber"));
                row.add(rs.getString("address"));
                rows.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    return buildTableModel(rows, columnNames);
    }

    // Method to populate the Book table
    private static TableModel populateBookTable() {
        String[] columnNames;
        if(statusAdmin) columnNames = new String[]{"ID", "title", "Author", "publisher id", "publisher_year", "ISBN"};
        else columnNames = new String[]{"title", "Author", "publisher_year"};
        List<List<Object>> rows = new ArrayList<>();

        try {
            String query = "SELECT id, title, author, publisher, publicationyear, isbn FROM BOOKS";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                List<Object> row = new ArrayList<>();
                if(statusAdmin) row.add(rs.getInt("id"));
                row.add(rs.getString("title"));
                row.add(rs.getString("author"));
                if(statusAdmin) row.add(rs.getString("publisher"));
                row.add(rs.getInt("publicationyear"));
                if(statusAdmin) row.add(rs.getString("isbn"));
                rows.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return buildTableModel(rows, columnNames);
    }

    private static TableModel populateAvailableBooksTable(){
        String[] columnNames = {"title", "Author", "publisher_year"};
        List<List<Object>> rows = new ArrayList<>();

        try {
            String query =  "SELECT DISTINCT b.title, b.author, b.publicationyear\n" +
                            "FROM books b\n" +
                            "JOIN copies c ON b.id = c.bookid\n" +
                            "WHERE c.status = 'Available'";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                List<Object> row = new ArrayList<>();
                row.add(rs.getString("title"));
                row.add(rs.getString("author"));
                row.add(rs.getInt("publicationyear"));
                rows.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return buildTableModel(rows, columnNames);
    }

    // Method to populate the Borrowing table
    private static TableModel populateBorrowingTable() {

        String[] columnNames = {"ID", "User id", "Copy id", "Borrow Date", "Return Date"};
        List<List<Object>> rows = new ArrayList<>();

        try {
            String query = "SELECT id, userid, copyid, borrowdate, returndate FROM BORROWINGS";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                List<Object> row = new ArrayList<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("userid"));
                row.add(rs.getString("copyid"));
                row.add(rs.getDate("borrowdate"));
                row.add(rs.getDate("returndate"));
                rows.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return buildTableModel(rows, columnNames);
    }
    private static TableModel populateBorrowedBooksTable(int userID) {

        String[] columnNames = {"Title", "Borrow Date", "Return Date"};
        List<List<Object>> rows = new ArrayList<>();

        String query =  "SELECT BOOKS.title, BORROWINGS.borrowdate, BORROWINGS.returndate\n" +
                        "FROM BOOKS\n" +
                        "JOIN COPIES ON BOOKS.id = COPIES.bookid\n" +
                        "JOIN BORROWINGS ON COPIES.ID = BORROWINGS.copyID\n" +
                        "WHERE BORROWINGS.userID = ?;";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userID);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    List<Object> row = new ArrayList<>();
                    row.add(resultSet.getString("title"));
                    row.add(resultSet.getDate("borrowdate"));
                    row.add(resultSet.getDate("returndate"));
                    rows.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return buildTableModel(rows, columnNames);
    }

    private static TableModel buildTableModel(List<List<Object>> rows, String[] columnNames) {
        Object[][] dataArray = new Object[rows.size()][columnNames.length];
        for (int i = 0; i < rows.size(); i++) {
            for (int j = 0; j < rows.get(i).size(); j++) {
                dataArray[i][j] = rows.get(i).get(j);
            }
        }

        // Create and return a DefaultTableModel
        return new DefaultTableModel(dataArray, columnNames);
    }




    private static void createUserForm() {

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
            try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO USERS(name, email, phonenumber, address) VALUES (?, ?, ?, ?)")) {
                preparedStatement.setString(1, nameField.getText());
                preparedStatement.setString(2, emailField.getText());
                preparedStatement.setString(3, phoneField.getText());
                preparedStatement.setString(4, addressField.getText());
                preparedStatement.executeUpdate();
                System.out.println("User added");

                registerFrame.dispose();
                createMainWindowAdmin();
            } catch (SQLException exception) {
                exception.printStackTrace();
                System.out.println("Error while adding the user");
            }
        });
        registerFrame.setVisible(true);
    }


    private static void createBookForm() {
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
            try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO BOOKS(title, author, publisher, publisheryear, isbn) VALUES (?, ?, ?, ?, ?)")) {
                preparedStatement.setString(1, titleField.getText());
                preparedStatement.setString(2, authorField.getText());
                preparedStatement.setString(3, publisherField.getText());
                preparedStatement.setString(4, publicationYField.getText());
                preparedStatement.setString(5, isbnField.getText());
                preparedStatement.executeUpdate();
                System.out.println("Book is added");


                registerFrame.dispose();
                createMainWindowAdmin();
            } catch (SQLException exception) {
                exception.printStackTrace();
                System.out.println("Error while adding the book");
            }
        });
        registerFrame.setVisible(true);
    }


    private static void createBorrowingForm() {
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
            try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO BOOKS(userid, copyid, borrowdate, returndate) VALUES (?, ?, ?, ?)")) {
                preparedStatement.setString(1, useridField.getText());
                preparedStatement.setString(2, copyIdField.getText());
                preparedStatement.setString(3, borrowDateField.getText());
                preparedStatement.setString(4, returnDateField.getText());
                preparedStatement.executeUpdate();
                System.out.println("borrow is added");


                registerFrame.dispose();
                createMainWindowAdmin();
            } catch (SQLException exception) {
                exception.printStackTrace();
                System.out.println("Error while adding the borrow");
            }
        });
        registerFrame.setVisible(true);
    }

    private static void deleteUserForm(){
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
            try {
                int userId = Integer.parseInt(userIdField.getText());

                // Delete query with conditions
                String deleteQuery = "DELETE FROM USERS\n" +
                        "WHERE id = ?\n" +
                        "AND NOT EXISTS (SELECT 1 FROM BORROWINGS WHERE userid = ?)\n" +
                        "AND NOT EXISTS (SELECT 1 FROM USERS u left join LIBRARIANS l WHERE u.id = ? and l.userid = ?)";

                try (PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {
                    deleteStmt.setInt(1, userId);
                    deleteStmt.setInt(2, userId);
                    deleteStmt.setInt(3, userId);
                    deleteStmt.setInt(4, userId);

                    int rowsAffected = deleteStmt.executeUpdate();

                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(deleteFrame, "User deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(deleteFrame, "User cannot be deleted (either a librarian or has borrowings).", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(deleteFrame, "Please enter a valid user ID. or User has borrowings/ is a librarian", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        deleteFrame.setVisible(true);
    }


    public static void createPopupWindow() {
        // Popup Window
        JFrame popupFrame = new JFrame("Welcome");
        popupFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        popupFrame.setSize(300, 150);
        popupFrame.setLayout(new FlowLayout());

        // Log In Button
        JButton loginButton = new JButton("Log In");
        loginButton.addActionListener(e -> {
            createLoginForm();
            popupFrame.dispose();
        });

        // Register Button
        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> {
            createRegisterForm();
            popupFrame.dispose();
        });

        popupFrame.add(loginButton);
        popupFrame.add(registerButton);

        popupFrame.setVisible(true);
    }

    private static void createLoginForm() {
        // Log In Form
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
            try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT u.id FROM USERS u WHERE u.email = ?")) {
                // Set the email parameter in the query
                preparedStatement.setString(1, emailField.getText());

                // Execute the query
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int userId = resultSet.getInt("id");


                        try (PreparedStatement librarianQuery = connection.prepareStatement("SELECT position FROM LIBRARIANS WHERE id = ?")) {
                            librarianQuery.setInt(1, userId);
                            try (ResultSet librarianResult = librarianQuery.executeQuery()) {
                                if (librarianResult.next()) {
                                    String position = librarianResult.getString("position");
                                    if (Objects.equals(position, "Librarian")) {
                                        statusAdmin = true;
                                        System.out.println("Librarian logged in!");
                                    } else {
                                        statusAdmin = false;
                                        System.out.println("Non-librarian user logged in!");
                                    }
                                } else {
                                    // User is not a librarian
                                    statusAdmin = false;
                                    System.out.println("Non-librarian user logged in!");
                                }
                            }
                        }

                        if(statusAdmin) createMainWindowAdmin();
                        else createMainWindowUser(userId);
                        loginFrame.dispose();
                    }else{
                        System.out.println("No user found with the provided email.");
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        loginFrame.setVisible(true);
    }

    private static void createRegisterForm() {
        // Register Form
        JFrame registerFrame = new JFrame("Register");
        registerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        registerFrame.setSize(500, 400);
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

        JButton regButton = new JButton("register");
        registerFrame.add(regButton);

        regButton.addActionListener(e ->  {
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO USERS(name, email, phonenumber, address) VALUES ( ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {

                preparedStatement.setString(1, nameField.getText());
                preparedStatement.setString(2, emailField.getText());
                preparedStatement.setString(3, phoneField.getText());
                preparedStatement.setString(4, addressField.getText());
                preparedStatement.executeUpdate();

                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                    int userID = generatedKeys.getInt(1);
                    System.out.println("User added with ID: " + userID);

                    statusAdmin = false;
                    createMainWindowUser(userID);
                    registerFrame.dispose();
                    }
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
                System.out.println("Error while adding the user");
            }
        });
        registerFrame.setVisible(true);
    }
}