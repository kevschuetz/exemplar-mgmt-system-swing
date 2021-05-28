package view.panels.mainFrame;

import model.entities.Community;
import view.listeners.mainframe.ActionWithComponentListener;

import javax.swing.*;

public class CommunityTab extends JPanel {
    private Community community;
    private JButton closeButton = new JButton("Close");
    private ActionWithComponentListener closeListener;

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
        closeButton.addActionListener((x)->closeListener.componentSubmitted(this));
    }




    public void setCloseListener(ActionWithComponentListener closeListener) {
        this.closeListener = closeListener;
    }
}
