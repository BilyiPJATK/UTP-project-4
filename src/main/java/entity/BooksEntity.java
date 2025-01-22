package entity;

import jakarta.persistence.*;

import java.util.*;

/**
 * Entity representing a book in the system. Maps to the "BOOKS" table in the database.
 */
@Entity
@Table(name = "BOOKS", schema = "PUBLIC", catalog = "DATABASE")
public class BooksEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "ID")
    private int id;

    @Basic
    @Column(name = "TITLE")
    private String title;

    @Basic
    @Column(name = "AUTHOR")
    private String author;

    @ManyToOne
    @JoinColumn(name = "PUBLISHER")
    private PublishersEntity publisher;

    @Basic
    @Column(name = "PUBLICATIONYEAR")
    private String publicationyear;

    @Basic
    @Column(name = "ISBN")
    private String isbn;

    @OneToMany(mappedBy = "bookid")
    private Set<CopiesEntity> copies = new LinkedHashSet<>();

    /**
     * Default constructor.
     */
    public BooksEntity() {
    }

    /**
     * Constructor that initializes all fields of the book entity.
     *
     * @param title          the title of the book
     * @param author         the author of the book
     * @param publisher      the publisher of the book
     * @param publicationYear the publication year of the book
     * @param isbn           the ISBN of the book
     */
    public BooksEntity(String title, String author, PublishersEntity publisher, String publicationYear, String isbn) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publicationyear = publicationYear;
        this.isbn = isbn;
    }

    /**
     * Gets the ID of the book.
     *
     * @return the ID of the book
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the ID of the book.
     *
     * @param id the ID of the book
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the title of the book.
     *
     * @return the title of the book
     */
    public Object getTitle() {
        return title;
    }

    /**
     * Sets the title of the book.
     *
     * @param title the title of the book
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the author of the book.
     *
     * @return the author of the book
     */
    public Object getAuthor() {
        return author;
    }

    /**
     * Sets the author of the book.
     *
     * @param author the author of the book
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Gets the publisher of the book.
     *
     * @return the publisher of the book
     */
    public Object getPublisher() {
        return publisher;
    }

    /**
     * Sets the publisher of the book.
     *
     * @param publisher the publisher of the book
     */
    public void setPublisher(PublishersEntity publisher) {
        this.publisher = publisher;
    }

    /**
     * Gets the publication year of the book.
     *
     * @return the publication year of the book
     */
    public String getPublicationyear() {
        return publicationyear;
    }

    /**
     * Sets the publication year of the book.
     *
     * @param publicationyear the publication year of the book
     */
    public void setPublicationyear(String publicationyear) {
        this.publicationyear = publicationyear;
    }

    /**
     * Gets the ISBN of the book.
     *
     * @return the ISBN of the book
     */
    public Object getIsbn() {
        return isbn;
    }

    /**
     * Sets the ISBN of the book.
     *
     * @param isbn the ISBN of the book
     */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    /**
     * Compares this book to another object for equality.
     *
     * @param o the object to compare this book to
     * @return true if the two objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BooksEntity that = (BooksEntity) o;
        return id == that.id &&
                Objects.equals(title, that.title) &&
                Objects.equals(author, that.author) &&
                Objects.equals(publisher, that.publisher) &&
                Objects.equals(publicationyear, that.publicationyear) &&
                Objects.equals(isbn, that.isbn);
    }

    /**
     * Generates a hash code for this book.
     *
     * @return the hash code for this book
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, title, author, publisher, publicationyear, isbn);
    }

    /**
     * Returns a list of all book attributes.
     *
     * @return a list containing the ID, title, author, publisher ID, publication year, and ISBN of the book
     */
    public List<Object> returnAll() {
        List<Object> list = new ArrayList<>();
        list.add(id);
        list.add(title);
        list.add(author);
        list.add(publisher.getId());
        list.add(publicationyear);
        list.add(isbn);
        return list;
    }

    /**
     * Returns the set of copies associated with the book.
     *
     * @return a set of copies of the book
     */
    public Set<CopiesEntity> returnCopy() {
        return copies;
    }
}
