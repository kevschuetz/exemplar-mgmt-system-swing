package application;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Users {
    @Id
    private String username;

    private int is_contributor;

    public Users(){
    }
    public Users(String username, int isContributor){
        this.username = username;
        this.is_contributor = isContributor;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getUsername(){
        return this.username;
    }

    public int getIsContributor() {
        return is_contributor;
    }

    public void setIsContributor(int isContributor) {
        this.is_contributor = isContributor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (!(o instanceof Users)){
            return false;
        }
        return username != null && this.username.equals(((Users) o).username);
    }
}
