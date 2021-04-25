package view.panels.mainFrame.homeTab;

import model.entities.User;
import view.listeners.mainframe.homeTab.UpdateUserListener;

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
    UpdateUserListener updateUserListener;
    public ProfilePanel(User user){
        this.user = user;
        setLayout(new GridLayout(7,2));
        addComponents();
        setData();
        addActionListener();

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
        add(updateButton);
    }

    void setData(){
        userNameText.setText(user.getUsername());
        userNameText.setEditable(false);
        fullNameText.setText(user.getFullName());
        oldPasswordField.setText("");
        passwordField1.setText("");
        passwordField2.setText("");
        if(user.getIsContributor() == 1 && !contributorCheckBox.isSelected()){
            contributorCheckBox.doClick();
        }else if(user.getIsContributor() == 0 && contributorCheckBox.isSelected() ){
            contributorCheckBox.doClick();
        }
    }

    public void setUpdateUserListener(UpdateUserListener updateUserListener) {
        this.updateUserListener = updateUserListener;
    }

    void addActionListener(){
        updateButton.addActionListener((x)->{
            String fullname = fullNameText.getText();
            String oldPassword = oldPasswordField.getText();
            String newPassword1 = passwordField1.getText();
            String newPassword2 = passwordField2.getText();
            int isContributor = contributorCheckBox.isSelected() ? 1 : 0;

            if(!oldPassword.equals(user.getPassword())){
                JOptionPane.showMessageDialog(this, "Old Password not correct");
            }else if(!newPassword1.equals(newPassword2)){
                JOptionPane.showMessageDialog(this, "Passwords do not match");
            }else if(fullname.length()<1){
                JOptionPane.showMessageDialog(this, "Fullname cannot be empty");
            }else{
                User toBeUpdated = new User(user.getUsername(), fullname, newPassword1, isContributor);
                updateUserListener.updateRequested(toBeUpdated);
            }
        });
    }
    public void setUser(User user) {
        this.user = user;
        setData();
    }


}
