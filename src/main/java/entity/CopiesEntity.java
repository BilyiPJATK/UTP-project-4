package entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entity representing a copy of a book in the system. Maps to the "COPIES" table in the database.
 */
@Entity
@Table(name = "COPIES", schema = "PUBLIC", catalog = "DATABASE")
public class CopiesEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "ID")
    private int id;

    @ManyToOne
    @JoinColumn(name = "BOOKID")
    private BooksEntity bookid;

    @Basic
    @Column(name = "COPYNUMBER")
    private int copynumber;

    @Basic
    @Column(name = "STATUS")
    private String status;

    /**
     * Default constructor.
     */
    public CopiesEntity() {
    }

    /**
     * Constructor that initializes all fields of the copy entity.
     *
     * @param book      the book associated with this copy
     * @param copyNumber the number of the copy
     * @param status    the status of the copy (e.g., available, borrowed)
     */
    public CopiesEntity(BooksEntity book, int copyNumber, String status) {
        this.bookid = book;
        this.copynumber = copyNumber;
        this.status = status;
    }

    /**
     * Gets the ID of the copy.
     *
     * @return the ID of the copy
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the ID of the copy.
     *
     * @param id the ID of the copy
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the book associated with this copy.
     *
     * @return the book associated with this copy
     */
    public BooksEntity getBookid() {
        return bookid;
    }

    /**
     * Sets the book associated with this copy.
     *
     * @param bookid the book associated with this copy
     */
    public void setBookid(BooksEntity bookid) {
        this.bookid = bookid;
    }

    /**
     * Gets the number of the copy.
     *
     * @return the number of the copy
     */
    public int getCopynumber() {
        return copynumber;
    }

    /**
     * Sets the number of the copy.
     *
     * @param copynumber the number of the copy
     */
    public void setCopynumber(int copynumber) {
        this.copynumber = copynumber;
    }

    /**
     * Gets the status of the copy.
     *
     * @return the status of the copy
     */
    public Object getStatus() {
        return status;
    }

    /**
     * Sets the status of the copy.
     *
     * @param status the status of the copy (e.g., available, borrowed)
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Compares this copy entity to another object for equality.
     *
     * @param o the object to compare this copy entity to
     * @return true if the two objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CopiesEntity that = (CopiesEntity) o;
        return id == that.id &&
                copynumber == that.copynumber &&
                Objects.equals(bookid, that.bookid) &&
                Objects.equals(status, that.status);
    }

    /**
     * Generates a hash code for this copy entity.
     *
     * @return the hash code for this copy entity
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, bookid, copynumber, status);
    }

    /**
     * Returns a list of all attributes of the copy entity.
     *
     * @return a list containing the ID, book ID, copy number, and status of the copy
     */
    public List<Object> returnAll() {
        List<Object> list = new ArrayList<>();
        list.add(id);
        list.add(bookid);
        list.add(copynumber);
        list.add(status);
        return list;
    }
}
