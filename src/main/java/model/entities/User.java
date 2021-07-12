package model.entities;

import java.util.Objects;

/**
 * Represents a user of the application
 */
public class User {
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getIsContributor() {
        return isContributor;
    }

    public void setIsContributor(int is_contributor) {
        this.isContributor = is_contributor;
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
        return username;
    }


}
