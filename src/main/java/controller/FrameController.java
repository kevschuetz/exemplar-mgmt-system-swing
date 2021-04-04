package controller;

import model.entities.Exemplar;
import model.entities.Users;
import model.httpclients.UserClient;
import view.frames.MainFrame;
import view.listeners.CustomListener;

import java.io.IOException;

public class FrameController<Usrs> {
    public static void main(String[] args) {
        new FrameController();
    }
    private Exemplar currentExemplar;
    private Users currentUser;

    private UserClient userClient;

    private MainFrame mainFrame;

    public FrameController(){
        userClient = new UserClient();
        mainFrame = new MainFrame();


        mainFrame.customListener = new CustomListener() {
            @Override
            public void listenerActivated(String s) {
                try {
                    userClient.add(new Users(s , 0));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(s);
            }
        };
        mainFrame.setVisible(true);
    }

}
