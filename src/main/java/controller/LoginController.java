package controller;

import model.entities.User;
import model.httpclients.UserClient;
import view.frames.login.LoginFrame;
import view.frames.login.RegisterForm;

import javax.swing.*;
import java.awt.event.ActionListener;

public class LoginController {
    private UserClient userClient = new UserClient();

    private LoginFrame loginFrame;
    private RegisterForm registerForm;

    private User currentUser;
    private ActionListener loginListener;

    public LoginController(){
        initializeLoginFrame();
        loginFrame.setLoginListener((u,p) ->{
            processLoginRequest(u,p);
        });


        initializeRegisterForm();
        loginFrame.setRegisterListener(()->registerForm.setVisible(true));
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
                JOptionPane.showMessageDialog(loginFrame, "Login Successful");
                currentUser = user;
                loginListener.actionPerformed(null);
            }else{
                JOptionPane.showMessageDialog(loginFrame, "Invalid Username or Password");
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
    // REGISTER
    void initializeRegisterForm(){
        registerForm = new RegisterForm();
        registerForm.setVisible(false);
        registerForm.setTitle("Register");
    }

    public void startLoginProcess(){
        loginFrame.setVisible(true);
    }

    public void setLoginListener(ActionListener loginListener) {
        this.loginListener = loginListener;
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
