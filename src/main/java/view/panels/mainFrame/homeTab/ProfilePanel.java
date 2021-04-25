package view.panels.mainFrame.homeTab;

import model.entities.User;

import javax.swing.*;
import java.awt.*;

public class ProfilePanel extends JPanel {
    JLabel userNameLabel = new JLabel("Username");
    JLabel fullNameLabel = new JLabel("Full name");
    JLabel oldPasswordLabel = new JLabel("Old Password");
    JLabel passwordLabel2 = new JLabel ("Password");
    JLabel passwordLabel1 = new JLabel("Password");
    JTextField userNameText = new JTextField();
    JTextField fullNameText = new JTextField();
    JPasswordField oldPasswordField = new JPasswordField();
    JPasswordField passwordField1 = new JPasswordField();
    JPasswordField passwordField2 = new JPasswordField();
    JLabel contributorLabel = new JLabel("Contributor ?");
    JCheckBox contributorCheckBox = new JCheckBox();
    JButton updateButton = new JButton("UPDATE");
    User user;

    public ProfilePanel(User user){
        this.user = user;
        setLayout(new GridLayout(7,2));
        addComponents();
        setData();


    }

    void addComponents(){
        add(userNameLabel);
        add(userNameText);
        add(fullNameLabel);
        add(fullNameText);
        add(oldPasswordLabel);
        add(oldPasswordField);
        add(passwordLabel1);
        add(passwordField1);
        add(passwordLabel2);
        add(passwordField2);
        add(contributorLabel);
        add(contributorCheckBox);

        updateButton.setSize(100,20);
        add(updateButton);
    }

    void setData(){
        userNameText.setText(user.getUsername());
        fullNameText.setText(user.getFullName());
        if(user.getIsContributor() == 1)contributorCheckBox.doClick();
    }

}
