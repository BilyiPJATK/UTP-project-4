package entity;

import jakarta.persistence.*;

@Entity
@Table(name = "COPIES")
public class Copy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "bookId", nullable = false)
    private Book book;

    @Column(name = "copyNumber", unique = true)
    private int copyNumber;

    @Column(name = "status")
    private String status;

    // Constructors
    public Copy() {
    }

    public Copy(Book book, int copyNumber, String status) {
        this.book = book;
        this.copyNumber = copyNumber;
        this.status = status;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public int getCopyNumber() {
        return copyNumber;
    }

    public void setCopyNumber(int copyNumber) {
        this.copyNumber = copyNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Copy{" +
                "id=" + id +
                ", book=" + book +
                ", copyNumber=" + copyNumber +
                ", status='" + status + '\'' +
                '}';
    }
}
