package resources;

import javax.persistence.*;

@Entity
public class Rating {
    @EmbeddedId
    RatingPK key;

    private double rating;

    public Rating(){
    }

    public RatingPK getKey() {
        return key;
    }

    public void setKey(RatingPK key) {
        this.key = key;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
