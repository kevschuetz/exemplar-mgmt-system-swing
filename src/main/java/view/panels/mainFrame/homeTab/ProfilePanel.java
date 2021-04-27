package view.panels.mainFrame.homeTab;

import model.entities.User;
import view.listeners.mainframe.homeTab.ProfilePanelListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

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
    JButton deleteUserButton = new JButton("DELETE");
    User user;
    ProfilePanelListener updateListener;
    ProfilePanelListener deleteListener;
    private ConfirmDeletalFrame confirmFrame;


    public ProfilePanel(User user){
        this.user = user;
        setLayout(new GridLayout(7,2));
        addComponents();
        setData();
        addActionListener();
        initializeConfirmFrame();
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
        add(deleteUserButton);
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
            }else if(newPassword1.length()<8){
                JOptionPane.showMessageDialog(this, "Password must have at least 8 characters");
            }else{
                User toBeUpdated = new User(user.getUsername(), fullname, newPassword1, isContributor);
                updateListener.updateRequested(toBeUpdated);
            }
        });

        deleteUserButton.addActionListener((x)->{
            confirmFrame.setVisible(true);
        });
    }

    void initializeConfirmFrame(){
        confirmFrame = new ConfirmDeletalFrame();
        confirmFrame.setSize(250,200);
        confirmFrame.setVisible(false);
        confirmFrame.setCancelledListener((x)->confirmFrame.setVisible(false));
        confirmFrame.setConfirmedListener((x)->{
            confirmFrame.setVisible(false);
            deleteListener.updateRequested(user);
        });
    }
    public void setUser(User user) {
        this.user = user;
        setData();
    }
    public void setUpdateUserListener(ProfilePanelListener profilePanelListener) {
        this.updateListener = profilePanelListener;
    }

    public void setDeleteListener(ProfilePanelListener deleteListener) {
        this.deleteListener = deleteListener;
    }

    private class ConfirmDeletalFrame extends JFrame{
        ActionListener confirmedListener;
        ActionListener cancelledListener;
        private JButton confirmButton = new JButton("Confirm");
        private JButton cancelButton = new JButton("Cancel");
        private JPanel buttonPanel = new JPanel();
        private JPanel panel = new JPanel();
        private JLabel label = new JLabel("Attention! Deleting cannot be undone. \n Are you sure?");
        public ConfirmDeletalFrame(){
            panel.setLayout(new GridLayout(2,1));
            panel.add(label);
            buttonPanel.setLayout(new GridLayout(1,2));
            buttonPanel.add(confirmButton);
            buttonPanel.add(cancelButton);
            panel.add(buttonPanel);
            addListeners();
            add(panel);
        }

        void addListeners(){
            confirmButton.addActionListener((x)-> confirmedListener.actionPerformed(x));
            cancelButton.addActionListener((x)->cancelledListener.actionPerformed(x));
        }
        public void setConfirmedListener(ActionListener confirmedListener) {
            this.confirmedListener = confirmedListener;
        }

        public void setCancelledListener(ActionListener cancelledListener) {
            this.cancelledListener = cancelledListener;
        }
    }
}
