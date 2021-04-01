package model.entities;

public class Users {
    private String username;
    private int isContributor;

    public Users(){
    }

    public Users(String username, int isContributor){
        this.username = username;
        this.isContributor = isContributor;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getisContributor() {
        return isContributor;
    }

    public void setisContributor(int isContributor) {
        this.isContributor = isContributor;
    }

    public String toString(){
        return this.username;
    }
}
