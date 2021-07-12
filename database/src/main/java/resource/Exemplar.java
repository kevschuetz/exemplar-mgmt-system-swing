package resource;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
public class Exemplar {
    @Id
    private String name;

    @Column(length=2048)
    private String problem;
    @Column(length=2048)
    private String solution;

    @ManyToOne
    private User creator;

    @ManyToMany
    private List<User> contributors;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Label> labels;

    public List<Label> getLabels() {
        return labels;
    }

    public void setLabels(List<Label> labels) {
        this.labels = labels;
    }

    public List<User> getContributors() {
        return contributors;
    }

    public void setContributors(List<User> contributors) {
        this.contributors = contributors;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
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
        if (o == null || getClass() != o.getClass()) return false;
        Exemplar exemplar = (Exemplar) o;
        return name != null && name.equals(exemplar.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public String toString(){
        return this.name;

    }
}
