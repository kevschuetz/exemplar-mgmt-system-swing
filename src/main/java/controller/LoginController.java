package controller;

import model.entities.User;
import model.httpclients.UserClient;
import view.frames.login.LoginFrame;

import javax.swing.*;
import java.awt.event.ActionListener;

public class LoginController {
    private UserClient userClient = new UserClient();
    private LoginFrame loginFrame;
    private User currentUser;
    private ActionListener actionListener;

    public LoginController(){
        initializeLoginFrame();
        loginFrame.setLoginListener((u,p) ->processLoginRequest(u,p));
    }
    /**
     * Method is called when the LoginListener of the LoginFrame is activated(Login-Button)
     * -fetches the User with the given username from the database
     * -compares the password
     * - sets LoginFrame to not visible
     * - sets currentUser to fetched User
     *  - triggers the actionListener which is set by the MainController
     * @param username the username submitted in the LoginFrame
     * @param password the password submitted in the LoginFrame
     */
    void processLoginRequest(String username, String password)  {
        try {
            User user = userClient.get(username);
            if(user != null && user.getPassword().equals(password)) {
                loginFrame.setVisible(false);
                currentUser = user;
                actionListener.actionPerformed(null);
            }
        }catch(Exception e){e.printStackTrace();}
    }

    /**
     * Initializes LoginFrame (size, etc.)
     */
    void initializeLoginFrame(){
        loginFrame =new LoginFrame();
        loginFrame.setVisible(false);
        loginFrame.setTitle("Login");
        loginFrame.setBounds(10,10,370,600);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setResizable(false);
    }

    public void startLoginProcess(){
        loginFrame.setVisible(true);
    }

    public void setActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public LoginFrame getLoginFrame() {
        return loginFrame;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}
