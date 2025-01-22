package entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public CopiesEntity() {
    }

    public CopiesEntity(BooksEntity book, int copyNumber, String status) {
        this.bookid = book;
        this.copynumber = copyNumber;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BooksEntity getBookid() {
        return bookid;
    }

    public void setBookid(BooksEntity bookid) {
        this.bookid = bookid;
    }

    public int getCopynumber() {
        return copynumber;
    }

    public void setCopynumber(int copynumber) {
        this.copynumber = copynumber;
    }

    public Object getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CopiesEntity that = (CopiesEntity) o;
        return id == that.id && copynumber == that.copynumber && Objects.equals(bookid, that.bookid) && Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, bookid, copynumber, status);
    }

    public List<Object> returnAll(){
        List<Object> list = new ArrayList<>();
        list.add(id);
        list.add(bookid);
        list.add(copynumber);
        list.add(status);
        return list;
    }
}
