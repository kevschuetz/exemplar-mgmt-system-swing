package controller;

import model.entities.Exemplar;
import model.entities.User;
import model.httpclients.ExemplarClient;
import model.httpclients.UserClient;
import view.frames.MainFrame;
import view.panels.mainFrame.ExemplarTab;
import view.panels.mainFrame.homeTab.HomeTab;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class MainController {
    public static void main(String[] args) {
        new MainController();
    }
    private LoginController loginController;

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
            addListenersToHomeTab();
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

    void addListenersToHomeTab(){
        homeTab.setUpdateUserListener((u)-> {
            try {
                User updated = userClient.update(u.getUsername(), u);
                if (updated != null) {
                    this.currentUser = updated;
                    homeTab.setUser(updated);
                    JOptionPane.showMessageDialog(homeTab.getProfilePanel(), "Update successfull");
                }else JOptionPane.showMessageDialog(homeTab.getProfilePanel(), "Update failed");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        homeTab.setOpenExemplarListener((list)->{
            try {
                for(String s : list){
                    Exemplar e = new ExemplarClient().get(s);
                    if(e != null){
                        ExemplarTab newExemplarTab = new ExemplarTab(e);
                        newExemplarTab.setCloseListener((c)->mainFrame.removeTab(c));
                        mainFrame.addTab(s,newExemplarTab);
                    }
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        });
    }
}
