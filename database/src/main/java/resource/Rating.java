package resource;

import javax.persistence.*;
import java.sql.Date;
import java.util.Objects;

@Entity
public class Rating {
    @EmbeddedId
    RatingPK key;
    @Basic(optional = true)
    private java.sql.Date sqlDate;
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

    public Date getSqlDate() {
        return sqlDate;
    }

    public void setSqlDate(Date sqlDate) {
        this.sqlDate = sqlDate;
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
