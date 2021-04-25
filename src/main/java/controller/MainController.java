package controller;

import model.entities.Exemplar;
import model.entities.User;
import model.httpclients.UserClient;
import view.frames.MainFrame;
import view.panels.mainFrame.homeTab.HomeTab;

import javax.swing.*;
import java.awt.*;

public class MainController {
    public static void main(String[] args) {
        new MainController();
    }
    private LoginController loginController;

    private Exemplar currentExemplar;
    private User currentUser;

    private UserClient userClient = new UserClient();

    private MainFrame mainFrame;
    private HomeTab homeTab;
    /**
     * Initializes the LoginController and starts the login process
     */
    public MainController(){
        initializeMainFrame();

        //login
       loginController = new LoginController();
       loginController.setLoginListener(x->{
           currentUser = loginController.getCurrentUser();
           loginSuccesfull();
       });
       loginController.startLoginProcess();

    }

    /**
     * Method is triggered by LoginController after succesfull login
     */
    void loginSuccesfull(){
        mainFrame.setVisible(true);
        if(currentUser != null){
            mainFrame.setTitle("Welcome, "+currentUser.getUsername()+" !");
            homeTab = new HomeTab(currentUser);
            mainFrame.addTab("Home", homeTab);
        }
        else mainFrame.setTitle("Welcome!");
    }

    void initializeMainFrame(){
        mainFrame = new MainFrame();
        mainFrame.setVisible(false);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(new Dimension(1000, 750));
    }



}
