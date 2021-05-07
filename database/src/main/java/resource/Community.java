package resource;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
public class Community {
    @Id
    private String name;

    @ManyToMany
    private List<Exemplar> exemplars;

    @ManyToMany
    private List<User> members;

    @ManyToOne
    private User creator;

    public Community(){};

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

    public List<User> getMembers() {
        return members;
    }

    public void setUsers(List<User> members) {
        this.members = members;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public String toString(){
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Community)) return false;
        Community community = (Community) o;
        return getName() != null && getName().equals(community.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
