package resource;

import javax.persistence.*;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Rating)) return false;
        Rating rating = (Rating) o;
        return getKey() != null && getKey().equals(rating.getKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey());
    }
}
