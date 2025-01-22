package entity;

import jakarta.persistence.*;
import java.util.*;

/**
 * Entity representing a publisher in the system. Maps to the "PUBLISHERS" table in the database.
 */
@Entity
@Table(name = "PUBLISHERS", schema = "PUBLIC", catalog = "DATABASE")
public class PublishersEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "ID")
    private int id;

    @Basic
    @Column(name = "NAME")
    private String name;

    @Basic
    @Column(name = "ADDRESS")
    private String address;

    @Basic
    @Column(name = "PHONENUMBER")
    private String phonenumber;

    @OneToMany(mappedBy = "id", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BooksEntity> books = new LinkedHashSet<>();

    /**
     * Constructor that initializes the name, address, and phone number of the publisher.
     *
     * @param name       the name of the publisher
     * @param address    the address of the publisher
     * @param phoneNumber the phone number of the publisher
     */
    public PublishersEntity(String name, String address, String phoneNumber) {
        this.name = name;
        this.address = address;
        this.phonenumber = phoneNumber;
    }

    /**
     * Default constructor.
     */
    public PublishersEntity() {}

    /**
     * Gets the ID of the publisher.
     *
     * @return the ID of the publisher
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the ID of the publisher.
     *
     * @param id the ID of the publisher
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the name of the publisher.
     *
     * @return the name of the publisher
     */
    public Object getName() {
        return name;
    }

    /**
     * Sets the name of the publisher.
     *
     * @param name the name of the publisher
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the address of the publisher.
     *
     * @return the address of the publisher
     */
    public Object getAddress() {
        return address;
    }

    /**
     * Sets the address of the publisher.
     *
     * @param address the address of the publisher
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Gets the phone number of the publisher.
     *
     * @return the phone number of the publisher
     */
    public Object getPhonenumber() {
        return phonenumber;
    }

    /**
     * Sets the phone number of the publisher.
     *
     * @param phonenumber the phone number of the publisher
     */
    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    /**
     * Compares this publisher entity to another object for equality.
     *
     * @param o the object to compare this publisher entity to
     * @return true if the two objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PublishersEntity that = (PublishersEntity) o;
        return id == that.id &&
                Objects.equals(name, that.name) &&
                Objects.equals(address, that.address) &&
                Objects.equals(phonenumber, that.phonenumber);
    }

    /**
     * Generates a hash code for this publisher entity.
     *
     * @return the hash code for this publisher entity
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, name, address, phonenumber);
    }

    /**
     * Gets the set of books associated with this publisher.
     *
     * @return a set of books associated with this publisher
     */
    public Set<BooksEntity> getBooks() {
        return books;
    }

    /**
     * Returns a list of all attributes of the publisher entity.
     *
     * @return a list containing the ID, name, address, and phone number of the publisher
     */
    public List<Object> returnAll() {
        List<Object> list = new ArrayList<>();
        list.add(id);
        list.add(name);
        list.add(address);
        list.add(phonenumber);
        return list;
    }
}
