package entity;

import jakarta.persistence.*;

import java.util.*;

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
    public BooksEntity() {
    }

    public BooksEntity(String title, String author, PublishersEntity publisher, String publicationYear, String isbn) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publicationyear = publicationYear;
        this.isbn = isbn;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Object getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Object getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Object getPublisher() {
        return publisher;
    }

    public void setPublisher(PublishersEntity publisher) {
        this.publisher = publisher;
    }

    public String getPublicationyear() {
        return publicationyear;
    }

    public void setPublicationyear(String publicationyear) {
        this.publicationyear = publicationyear;
    }

    public Object getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BooksEntity that = (BooksEntity) o;
        return id == that.id && Objects.equals(title, that.title) && Objects.equals(author, that.author) && Objects.equals(publisher, that.publisher) && Objects.equals(publicationyear, that.publicationyear) && Objects.equals(isbn, that.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, author, publisher, publicationyear, isbn);
    }

    public List<Object> returnAll(){
        List<Object> list = new ArrayList<>();
        list.add(id);
        list.add(title);
        list.add(author);
        list.add(publisher.getId());
        list.add(publicationyear);
        list.add(isbn);
        return list;
    }
    public Set<CopiesEntity> returnCopy(){return copies;}
}
