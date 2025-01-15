package entity;

import jakarta.persistence.*;

@Entity
@Table(name = "LIBRARIANS")
public class Librarian {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @Column(name = "employmentDate")
    private java.sql.Date employmentDate;

    @Column(name = "position")
    private String position;


    // Constructors
    public Librarian() {
    }

    public Librarian(User user, java.sql.Date employmentDate, String position) {
        this.user = user;
        this.employmentDate = employmentDate;
        this.position = position;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public java.sql.Date getEmploymentDate() {
        return employmentDate;
    }

    public void setEmploymentDate(java.sql.Date employmentDate) {
        this.employmentDate = employmentDate;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "Librarian{" +
                "id=" + id +
                ", user=" + user +
                ", employmentDate=" + employmentDate +
                ", position='" + position + '\'' +
                '}';
    }
}
