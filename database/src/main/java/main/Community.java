package main;

import javax.persistence.*;
import java.util.List;

@Entity
public class Community {
    @Id
    private String name;

    @ManyToMany
    private List<Exemplar> exemplars;

    @ManyToMany
    private List<User> users;

    @ManyToOne
    private User creator;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Exemplar> getExemplars() {
        return exemplars;
    }

    public void setExemplars(List<Exemplar> exemplars) {
        this.exemplars = exemplars;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }
}
