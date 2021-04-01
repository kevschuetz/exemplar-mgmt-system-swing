package controller;

import view.frames.MainFrame;
import view.listeners.CustomListener;

public class FrameController {
    public static void main(String[] args) {
        new FrameController();
    }

    private MainFrame mainFrame;

    public FrameController(){
        mainFrame = new MainFrame();
        mainFrame.customListener = new CustomListener() {
            @Override
            public void listenerActivated(String s) {
                System.out.println(s);
            }
        };
        mainFrame.setVisible(true);
    }

}
