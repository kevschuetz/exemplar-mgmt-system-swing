package resource;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name="Users")
public class User {
    @Id
    private String username;
    private int isContributor;
    private String fullName;
    private String password;

    public User(){
    }

    public User(String username, String fullName, String password, int isContributor){
        this.username = username;
        this.isContributor = isContributor;
        this.fullName=fullName;
        this.password=password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getUsername(){
        return this.username;
    }

    public int getIsContributor() {
        return isContributor;
    }

    public void setIsContributor(int isContributor) {
        this.isContributor = isContributor;
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
        if (!(o instanceof User)){
            return false;
        }
        return username != null && this.username.equals(((User) o).username);
    }
    public String toString(){
        String s = isContributor == 1 ? "yes" : "no";
        return "User " + username + " is " + s + " contributor";
    }
}
