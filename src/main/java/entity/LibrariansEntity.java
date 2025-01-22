package entity;

import jakarta.persistence.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entity representing a librarian in the system. Maps to the "LIBRARIANS" table in the database.
 */
@Entity
@Table(name = "LIBRARIANS", schema = "PUBLIC", catalog = "DATABASE")
public class LibrariansEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "ID")
    private int id;

    @OneToOne
    @JoinColumn(name = "USERID")
    private UsersEntity userid;

    @Basic
    @Column(name = "EMPLOYMENTDATE")
    private Date employmentdate;

    @Basic
    @Column(name = "POSITION")
    private String position;

    /**
     * Default constructor.
     */
    public LibrariansEntity() {
    }

    /**
     * Constructor that initializes all fields of the librarian entity.
     *
     * @param user         the user associated with this librarian
     * @param employmentDate the employment date of the librarian
     * @param position     the position held by the librarian
     */
    public LibrariansEntity(UsersEntity user, java.sql.Date employmentDate, String position) {
        this.userid = user;
        this.employmentdate = employmentDate;
        this.position = position;
    }

    /**
     * Gets the ID of the librarian.
     *
     * @return the ID of the librarian
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the ID of the librarian.
     *
     * @param id the ID of the librarian
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the user associated with this librarian.
     *
     * @return the user associated with this librarian
     */
    public UsersEntity getUserid() {
        return userid;
    }

    /**
     * Sets the user associated with this librarian.
     *
     * @param userid the user associated with this librarian
     */
    public void setUserid(UsersEntity userid) {
        this.userid = userid;
    }

    /**
     * Gets the employment date of the librarian.
     *
     * @return the employment date of the librarian
     */
    public Date getEmploymentdate() {
        return employmentdate;
    }

    /**
     * Sets the employment date of the librarian.
     *
     * @param employmentdate the employment date of the librarian
     */
    public void setEmploymentdate(Date employmentdate) {
        this.employmentdate = employmentdate;
    }

    /**
     * Gets the position of the librarian.
     *
     * @return the position of the librarian
     */
    public Object getPosition() {
        return position;
    }

    /**
     * Sets the position of the librarian.
     *
     * @param position the position of the librarian
     */
    public void setPosition(String position) {
        this.position = position;
    }

    /**
     * Compares this librarian entity to another object for equality.
     *
     * @param o the object to compare this librarian entity to
     * @return true if the two objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LibrariansEntity that = (LibrariansEntity) o;
        return id == that.id &&
                userid == that.userid &&
                Objects.equals(employmentdate, that.employmentdate) &&
                Objects.equals(position, that.position);
    }

    /**
     * Generates a hash code for this librarian entity.
     *
     * @return the hash code for this librarian entity
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, userid, employmentdate, position);
    }

    /**
     * Returns a list of all attributes of the librarian entity.
     *
     * @return a list containing the ID, user ID, employment date, and position of the librarian
     */
    public List<Object> returnAll() {
        List<Object> list = new ArrayList<>();
        list.add(id);
        list.add(userid);
        list.add(employmentdate);
        list.add(position);
        return list;
    }

}
