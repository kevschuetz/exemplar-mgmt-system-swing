package model.entities;

import java.util.List;
import java.util.Objects;

public class Comment {

    private String id;
    private String value;
    private User creator;
    private Exemplar exemplar;
    private List<Comment> answers;

    public Comment(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

