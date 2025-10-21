# Library Management System

A desktop-based library management application built with **Java Swing**, **JDBC**, and **JPA/Hibernate**. The application allows administrators and regular users to manage and interact with books, users, and borrowings efficiently.

## Features

- **Admin Panel**
  - Add, edit, and delete users  
  - Add, edit, and delete books  
  - Add, edit, and delete borrowings  
  - View tables for all users, books, and borrowings  
- **User Panel**
  - Browse all books  
  - View available books  
  - View borrowed books with borrow and return dates  
- **Database Integration**
  - H2 embedded database for persistence  
  - JPA entities for `Book`, `User`, `Copy`, `Publisher`, and `Borrowing`  
  - SQL queries for populating tables dynamically  
- **GUI**
  - Swing-based tables (`JTable`) with dynamic data  
  - Forms for adding/editing/deleting records  
  - Multi-window interface for admin and user roles  

## Technologies Used

- **Frontend:** Java Swing (JFrame, JTable, GridLayout, JScrollPane)  
- **Backend:** Java, JDBC, JPA/Hibernate  
- **Database:** H2 embedded database  
- **Data Modeling:** JPA entities with relationships (One-to-Many, Many-to-One)  

## Setup & Run

1. Ensure you have **Java 17+** installed.  
2. Clone the repository:

```bash
git clone https://github.com/yourusername/library-management.git
cd library-management
