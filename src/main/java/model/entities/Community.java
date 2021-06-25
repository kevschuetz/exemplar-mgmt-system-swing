package model.entities;

import java.util.List;
import java.util.Objects;

public class Community {
    private String name;
    private List<Exemplar> exemplars;
    private List<User> members;
    private User creator;


    public Community(){
        //empty constructor
    }

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

    public void setMembers(List<User> members) {
        this.members = members;
    }

    public User getCreator() {
        return creator;
    }


    public void setCreator(User creator) {
        this.creator = creator;
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
