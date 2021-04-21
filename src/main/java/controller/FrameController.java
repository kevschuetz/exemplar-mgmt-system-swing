package controller;

import model.entities.Exemplar;
import model.entities.User;
import model.httpclients.UserClient;
import view.frames.login.LoginFrame;
import view.frames.MainFrame;

import javax.swing.*;

public class FrameController {
    public static void main(String[] args) {
        new FrameController();
    }

    private Exemplar currentExemplar;
    private User currentUser;

    private UserClient userClient = new UserClient();

    private LoginFrame loginFrame;
    private MainFrame mainFrame;

    /**
     * Initializes the LoginFrame and sets the listener of the frame
     * (username/password to login : admin/admin)
     */
    public FrameController(){
        //login
        initializeLoginFrame();
        loginFrame.setLoginListener((u,p) ->processLoginRequest(u,p));
    }

    /**
     * Method is called by processLoginRequest(..), after a valid Username/Password combination has been submitted
     */
    void loginSuccesfull(){
        mainFrame = new MainFrame();
        mainFrame.setVisible(true);
        mainFrame.setListener(s -> System.out.println(s));

    }

    /**
     * Method is called when the LoginListener of the LoginFrame is activated(Login-Button)
     * -fetches the User with the given username from the database
     * -compares the password
     * - sets LoginFrame to not visible
     * - sets currentUser to fetched User
     *  - and calls loginSuccesfull(), (!)if combination is valid
     * @param username the username submitted in the LoginFrame
     * @param password the password submitted in the LoginFrame
     */
    void processLoginRequest(String username, String password)  {
        try {
            User user = userClient.get(username);
            if(user != null && user.getPassword().equals(password)) {
                loginFrame.setVisible(false);
                currentUser = user;
                loginSuccesfull();
            }
        }catch(Exception e){e.printStackTrace();}
    }


    /**
     * Initializes LoginFrame (size, etc.)
     */
    void initializeLoginFrame(){
        loginFrame =new LoginFrame();
        loginFrame.setTitle("Login");
        loginFrame.setVisible(true);
        loginFrame.setBounds(10,10,370,600);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setResizable(false);
    }
}
