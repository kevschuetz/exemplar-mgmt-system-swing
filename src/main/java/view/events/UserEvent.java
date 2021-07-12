package view.events;

/**
 * Wraps informations regarding an Event that requires details about a User
 */
public class UserEvent {
    private String username;
    private String fullname;
    private String password;
    private int isContributor;

    public UserEvent(String username, String fullname, String password, int isContributor){
        this.username=username;
        this.fullname=fullname;
        this.password=password;
        this.isContributor=isContributor;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getIsContributor() {
        return isContributor;
    }

    public void setIsContributor(int isContributor) {
        this.isContributor = isContributor;
    }
}
