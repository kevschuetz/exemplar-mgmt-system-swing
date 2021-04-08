package resources;

import javax.persistence.*;

@Entity
@IdClass(RatingPK.class)
public class Rating {
    @Id
    @ManyToOne
    private User user;

    @Id
    @ManyToOne
    private Exemplar exemplar;

    private double rating;

    public Rating(){

    }

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

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
