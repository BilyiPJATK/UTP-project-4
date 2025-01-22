package entity;

import jakarta.persistence.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "BORROWINGS", schema = "PUBLIC", catalog = "DATABASE")
public class BorrowingsEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "ID")
    private int id;
    @ManyToOne
    @JoinColumn(name = "USERID")
    private UsersEntity userid;
    @ManyToOne
    @JoinColumn(name = "COPYID")
    private CopiesEntity copyid;
    @Basic
    @Column(name = "BORROWDATE")
    private Date borrowdate;
    @Basic
    @Column(name = "RETURNDATE", nullable = true)
    private Date returndate;

    public BorrowingsEntity() {
    }

    public BorrowingsEntity(UsersEntity user, CopiesEntity copy, java.sql.Date borrowDate, java.sql.Date returnDate) {
        this.userid = user;
        this.copyid = copy;
        this.borrowdate = borrowDate;
        this.returndate = returnDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UsersEntity getUserid() {
        return userid;
    }

    public void setUserid(UsersEntity userid) {
        this.userid = userid;
    }

    public CopiesEntity getCopyid() {
        return copyid;
    }

    public void setCopyid(CopiesEntity copyid) {
        this.copyid = copyid;
    }

    public Date getBorrowdate() {
        return borrowdate;
    }

    public void setBorrowdate(Date borrowdate) {
        this.borrowdate = borrowdate;
    }

    public Date getReturndate() {
        return returndate;
    }

    public void setReturndate(Date returndate) {
        this.returndate = returndate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BorrowingsEntity that = (BorrowingsEntity) o;
        return id == that.id && userid == that.userid && copyid == that.copyid && Objects.equals(borrowdate, that.borrowdate) && Objects.equals(returndate, that.returndate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userid, copyid, borrowdate, returndate);
    }

    public List<Object> returnAll(){
        List<Object> list = new ArrayList<>();
        list.add(id);
        list.add(userid.getId());
        list.add(copyid.getId());
        list.add(borrowdate);
        list.add(returndate);
        return list;
    }
    public UsersEntity returnUser(){return userid;}
    public CopiesEntity returnCopy(){return copyid;}

}
