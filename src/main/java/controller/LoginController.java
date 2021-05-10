package controller;

import model.entities.User;
import model.httpclients.UserClient;
import view.events.UserEvent;
import view.frames.login.LoginFrame;
import view.frames.login.RegisterForm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class LoginController {
    private UserClient userClient = new UserClient();

    private LoginFrame loginFrame;
    private RegisterForm registerForm;

    private User currentUser;
    private ActionListener loginListener;


    Color backGroundColor = new Color(157,188,212);
    public LoginController(){
        initializeLoginFrame();
        loginFrame.setLoginListener((u,p) ->{
            processLoginRequest(u,p);
        });



        loginFrame.setRegisterListener(()->{
            //loginFrame.setVisible(false);
            registerForm.setVisible(true);
        });

        initializeRegisterForm();
        registerForm.setRegisterFormListener((e)->{
            processRegistrationRequest(e);
        });



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
            if(username.equalsIgnoreCase("guest")) {
                loginFrame.setVisible(false);
                loginListener.actionPerformed(null);
            }else{
                User user = userClient.get(username.trim());
                if(user != null && user.getPassword().trim().equals(password.trim())) {
                    loginFrame.setVisible(false);
                    JOptionPane.showMessageDialog(loginFrame, "Login Successful");
                    currentUser = user;
                    loginListener.actionPerformed(null);
                }else{
                    JOptionPane.showMessageDialog(loginFrame, "Invalid Username or Password");
                }
            }

        }catch(Exception e){e.printStackTrace();}
    }

    void processRegistrationRequest(UserEvent e){
        if(e.getUsername().trim().length()<4) JOptionPane.showMessageDialog(registerForm, "Username must have at least 4 characters");
        else if(e.getFullname().trim().length()<1) JOptionPane.showMessageDialog(registerForm, "Fullname cannot be empty");
        else if(e.getPassword().trim().length()<8) JOptionPane.showMessageDialog(registerForm, "Password must have at least 8 characters");
        else{
            User newUser = new User(e.getUsername().trim(), e.getFullname().trim(), e.getPassword().trim(), e.getIsContributor());
            try{
                User response = userClient.add(newUser);
                if (newUser.equals(response)) {
                    JOptionPane.showMessageDialog(registerForm, "Registration succesfully");
                    registerForm.setVisible(false);
                }else  {
                    JOptionPane.showMessageDialog(registerForm, "Username is already taken");
                }


            } catch(Exception exc){
                exc.printStackTrace();
            }


        }
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
        loginFrame.getContainer().setBackground(backGroundColor);
        loginFrame.getShowPassword().setBackground(backGroundColor);

    }
    // REGISTER
    void initializeRegisterForm(){
        registerForm = new RegisterForm();
        registerForm.setVisible(false);
        registerForm.setTitle("Registration Form");
        registerForm.setBounds(300, 90, 900, 600);
        registerForm.setResizable(false);
        registerForm.getContainer().setBackground(backGroundColor);
        registerForm.getContributorCheckBox().setBackground(backGroundColor);

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
