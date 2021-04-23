package controller;

import model.entities.Exemplar;
import model.entities.User;
import model.httpclients.UserClient;
import view.frames.MainFrame;

public class MainController {
    public static void main(String[] args) {
        new MainController();
    }
    private LoginController loginController;

    private Exemplar currentExemplar;
    private User currentUser;

    private UserClient userClient = new UserClient();

    private MainFrame mainFrame;

    /**
     * Initializes the LoginController and starts the login process
     */
    public MainController(){
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
        mainFrame = new MainFrame();
        mainFrame.setVisible(true);
        mainFrame.setListener(s -> System.out.println(s));

    }





}
