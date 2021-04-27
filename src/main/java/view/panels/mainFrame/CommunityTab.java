package view.panels.mainFrame;

import model.entities.Community;
import view.listeners.mainframe.CloseTabListener;

import javax.swing.*;

public class CommunityTab extends JPanel {
    private Community community;
    private JButton closeButton = new JButton("Close Tab");
    private CloseTabListener closeListener;

    public CommunityTab(Community community){
        this.community=community;
        JLabel label = new JLabel("community details");
        add(label);
        addComponents();
        addActionListener();


    }

    void addComponents(){
        add(closeButton);
    }
    void addActionListener(){
        closeButton.addActionListener((x)->closeListener.shutdownRequested(this));
    }




    public void setCloseListener(CloseTabListener closeListener) {
        this.closeListener = closeListener;
    }
}
