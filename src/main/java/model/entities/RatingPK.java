package model.entities;

import java.util.Objects;

public class RatingPK {
    private User user;
    private Exemplar exemplar;

    public RatingPK(){
        //empty constructor
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

    public String toString(){
        return exemplar.getName() + "," + user.getUsername();
    }
}
