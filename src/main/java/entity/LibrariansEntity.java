package entity;

import jakarta.persistence.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public LibrariansEntity() {
    }

    public LibrariansEntity(UsersEntity user, java.sql.Date employmentDate, String position) {
        this.userid = user;
        this.employmentdate = employmentDate;
        this.position = position;
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

    public Date getEmploymentdate() {
        return employmentdate;
    }

    public void setEmploymentdate(Date employmentdate) {
        this.employmentdate = employmentdate;
    }

    public Object getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LibrariansEntity that = (LibrariansEntity) o;
        return id == that.id && userid == that.userid && Objects.equals(employmentdate, that.employmentdate) && Objects.equals(position, that.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userid, employmentdate, position);
    }

    public List<Object> returnAll() {
        List<Object> list = new ArrayList<>();
        list.add(id);
        list.add(userid);
        list.add(employmentdate);
        list.add(position);
        return list;
    }

}
