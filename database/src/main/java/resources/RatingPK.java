package resources;



import java.io.Serializable;

public class RatingPK implements Serializable {
    private User user;
    private Exemplar exemplar;

    public RatingPK(){}

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Exemplar getExemplar() {
        return exemplar;
    }

    public void setExemplar(Exemplar exemplar) {
        this.exemplar = exemplar;
    }
}
