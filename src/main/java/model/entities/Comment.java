package model.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a comment made by a user to an exemplar
 */
public class Comment {

    private long id;
    private String value;
    private User creator;
    private Exemplar exemplar;
    private List<Comment> answers;

    public Comment(){
        answers=new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public Exemplar getExemplar() {
        return exemplar;
    }

    public void setExemplar(Exemplar exemplar){this.exemplar = exemplar;}

    public List<Comment> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Comment> answers) {
        this.answers = answers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Comment)) return false;
        Comment comment = (Comment) o;
        return id == comment.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

