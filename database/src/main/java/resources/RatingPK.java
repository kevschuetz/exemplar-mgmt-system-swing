package resources;



import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class RatingPK implements Serializable {
    @ManyToOne
    private User user;
    @ManyToOne
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

    @Override
    public int hashCode() {
        return Objects.hash(user.getUsername(), exemplar.getName());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this){
            return true;
        }
        if (obj instanceof RatingPK){
            return Objects.equals(((RatingPK)obj).getUser().getUsername(), this.user.getUsername())
                    && Objects.equals(((RatingPK)obj).getExemplar().getName(), this.exemplar.getName());
        }
        return false;
    }
}
