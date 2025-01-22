package entity;

import jakarta.persistence.*;
import java.util.*;

/**
 * Entity representing a user in the system. Maps to the "USERS" table in the database.
 */
@Entity
@Table(name = "USERS", schema = "PUBLIC", catalog = "DATABASE")
public class UsersEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "ID")
    private int id;

    @Basic
    @Column(name = "NAME")
    private String name;

    @Basic
    @Column(name = "EMAIL")
    private String email;

    @Basic
    @Column(name = "PHONENUMBER")
    private String phonenumber;

    @Basic
    @Column(name = "ADDRESS")
    private String address;

    @OneToOne(mappedBy = "userid")
    private LibrariansEntity librarian_user;

    @OneToMany(mappedBy = "userid")
    private Set<BorrowingsEntity> borrows = new LinkedHashSet<>();

    /**
     * Constructor that initializes the user's name, email, phone number, and address.
     *
     * @param name       the name of the user
     * @param email      the email of the user
     * @param phoneNumber the phone number of the user
     * @param address    the address of the user
     */
    public UsersEntity(String name, String email, String phoneNumber, String address) {
        this.name = name;
        this.email = email;
        this.phonenumber = phoneNumber;
        this.address = address;
    }

    /**
     * Default constructor.
     */
    public UsersEntity() {}

    /**
     * Gets the ID of the user.
     *
     * @return the ID of the user
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the ID of the user.
     *
     * @param id the ID of the user
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the name of the user.
     *
     * @return the name of the user
     */
    public Object getName() {
        return name;
    }

    /**
     * Sets the name of the user.
     *
     * @param name the name of the user
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the email of the user.
     *
     * @return the email of the user
     */
    public Object getEmail() {
        return email;
    }

    /**
     * Sets the email of the user.
     *
     * @param email the email of the user
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the phone number of the user.
     *
     * @return the phone number of the user
     */
    public Object getPhonenumber() {
        return phonenumber;
    }

    /**
     * Sets the phone number of the user.
     *
     * @param phonenumber the phone number of the user
     */
    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    /**
     * Gets the address of the user.
     *
     * @return the address of the user
     */
    public Object getAddress() {
        return address;
    }

    /**
     * Sets the address of the user.
     *
     * @param address the address of the user
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Compares this user entity to another object for equality.
     *
     * @param o the object to compare this user entity to
     * @return true if the two objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsersEntity that = (UsersEntity) o;
        return id == that.id &&
                Objects.equals(name, that.name) &&
                Objects.equals(email, that.email) &&
                Objects.equals(phonenumber, that.phonenumber) &&
                Objects.equals(address, that.address);
    }

    /**
     * Generates a hash code for this user entity.
     *
     * @return the hash code for this user entity
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, phonenumber, address);
    }

    /**
     * Gets the librarian entity associated with this user.
     *
     * @return the librarian associated with the user, or null if not a librarian
     */
    public LibrariansEntity getLibrarian() {
        return librarian_user;
    }

    /**
     * Sets the librarian entity for this user. Currently does nothing.
     */
    public void setLibrarian_user() {}

    /**
     * Returns a list of all attributes of the user entity.
     *
     * @return a list containing the ID, name, email, phone number, and address of the user
     */
    public List<Object> returnAll() {
        List<Object> list = new ArrayList<>();
        list.add(id);
        list.add(name);
        list.add(email);
        list.add(phonenumber);
        list.add(address);
        return list;
    }

    /**
     * Gets the set of borrowings associated with this user.
     *
     * @return a set of borrowings associated with this user
     */
    public Set<BorrowingsEntity> returnBorriwings() {
        return borrows;
    }
}
