package entity;

import jakarta.persistence.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entity representing a borrowing transaction in the system. Maps to the "BORROWINGS" table in the database.
 */
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

    /**
     * Default constructor.
     */
    public BorrowingsEntity() {
    }

    /**
     * Constructor that initializes all fields of the borrowing entity.
     *
     * @param user       the user borrowing the copy
     * @param copy       the copy of the book being borrowed
     * @param borrowDate the date the book was borrowed
     * @param returnDate the date the book was returned (nullable)
     */
    public BorrowingsEntity(UsersEntity user, CopiesEntity copy, java.sql.Date borrowDate, java.sql.Date returnDate) {
        this.userid = user;
        this.copyid = copy;
        this.borrowdate = borrowDate;
        this.returndate = returnDate;
    }

    /**
     * Gets the ID of the borrowing transaction.
     *
     * @return the ID of the borrowing transaction
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the ID of the borrowing transaction.
     *
     * @param id the ID of the borrowing transaction
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the user associated with this borrowing.
     *
     * @return the user who borrowed the book
     */
    public UsersEntity getUserid() {
        return userid;
    }

    /**
     * Sets the user associated with this borrowing.
     *
     * @param userid the user who borrowed the book
     */
    public void setUserid(UsersEntity userid) {
        this.userid = userid;
    }

    /**
     * Gets the copy associated with this borrowing.
     *
     * @return the copy of the book that was borrowed
     */
    public CopiesEntity getCopyid() {
        return copyid;
    }

    /**
     * Sets the copy associated with this borrowing.
     *
     * @param copyid the copy of the book that was borrowed
     */
    public void setCopyid(CopiesEntity copyid) {
        this.copyid = copyid;
    }

    /**
     * Gets the borrow date of this transaction.
     *
     * @return the borrow date of the transaction
     */
    public Date getBorrowdate() {
        return borrowdate;
    }

    /**
     * Sets the borrow date of this transaction.
     *
     * @param borrowdate the borrow date of the transaction
     */
    public void setBorrowdate(Date borrowdate) {
        this.borrowdate = borrowdate;
    }

    /**
     * Gets the return date of this transaction.
     *
     * @return the return date of the transaction
     */
    public Date getReturndate() {
        return returndate;
    }

    /**
     * Sets the return date of this transaction.
     *
     * @param returndate the return date of the transaction
     */
    public void setReturndate(Date returndate) {
        this.returndate = returndate;
    }

    /**
     * Compares this borrowing transaction to another object for equality.
     *
     * @param o the object to compare this borrowing transaction to
     * @return true if the two objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BorrowingsEntity that = (BorrowingsEntity) o;
        return id == that.id &&
                userid == that.userid &&
                copyid == that.copyid &&
                Objects.equals(borrowdate, that.borrowdate) &&
                Objects.equals(returndate, that.returndate);
    }

    /**
     * Generates a hash code for this borrowing transaction.
     *
     * @return the hash code for this borrowing transaction
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, userid, copyid, borrowdate, returndate);
    }

    /**
     * Returns a list of all attributes of the borrowing transaction.
     *
     * @return a list containing the ID, user ID, copy ID, borrow date, and return date of the transaction
     */
    public List<Object> returnAll() {
        List<Object> list = new ArrayList<>();
        list.add(id);
        list.add(userid.getId());
        list.add(copyid.getId());
        list.add(borrowdate);
        list.add(returndate);
        return list;
    }

    /**
     * Returns the user associated with the borrowing transaction.
     *
     * @return the user associated with the borrowing transaction
     */
    public UsersEntity returnUser() {
        return userid;
    }

    /**
     * Returns the copy associated with the borrowing transaction.
     *
     * @return the copy associated with the borrowing transaction
     */
    public CopiesEntity returnCopy() {
        return copyid;
    }
}
