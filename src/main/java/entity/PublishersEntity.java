package entity;

import jakarta.persistence.*;

import java.util.*;

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

    public PublishersEntity(String name, String address, String phoneNumber) {
        this.name = name;
        this.address = address;
        this.phonenumber = phoneNumber;
    }
    public PublishersEntity(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Object getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Object getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PublishersEntity that = (PublishersEntity) o;
        return id == that.id && Objects.equals(name, that.name) && Objects.equals(address, that.address) && Objects.equals(phonenumber, that.phonenumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, address, phonenumber);
    }

    public Set<BooksEntity> getBooks(){return books;}
    public List<Object> returnAll() {
        List<Object> list = new ArrayList<>();
        list.add(id);
        list.add(name);
        list.add(address);
        list.add(phonenumber);
        return list;
    }
}
