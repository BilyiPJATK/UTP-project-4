package entity;

import jakarta.persistence.*;

import java.util.*;

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

    public UsersEntity(String name, String email, String phoneNumber, String address){
        this.name = name;
        this.email = email;
        this.phonenumber = phoneNumber;
        this.address = address;
    }
    public UsersEntity(){}

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

    public Object getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Object getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public Object getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsersEntity that = (UsersEntity) o;
        return id == that.id && Objects.equals(name, that.name) && Objects.equals(email, that.email) && Objects.equals(phonenumber, that.phonenumber) && Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, phonenumber, address);
    }

    public LibrariansEntity getLibrarian(){return librarian_user;}
    public void setLibrarian_user(){}

    public List<Object> returnAll(){
        List<Object> list = new ArrayList<>();
        list.add(id);
        list.add(name);
        list.add(email);
        list.add(phonenumber);
        list.add(address);
        return list;
    }

    public Set<BorrowingsEntity> returnBorriwings(){ return borrows;}
}

