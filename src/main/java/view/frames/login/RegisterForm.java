package view.frames.login;


import view.events.UserEvent;
import view.listeners.login.RegisterFormListener;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class RegisterForm  extends JFrame {
    Container container = getContentPane();
    JLabel title = new JLabel("Registration Form");
    JLabel userNameLabel = new JLabel("Username");
    JLabel fullNameLabel = new JLabel("Full name");
    JLabel passwordLabel2 = new JLabel ("Password");
    JLabel passwordLabel1 = new JLabel("Password");
    JLabel contributorLabel = new JLabel("Contributor ?");
    JCheckBox contributorCheckBox = new JCheckBox();
    JTextField userNameText = new JTextField();
    JTextField fullNameText = new JTextField();
    JPasswordField passwordField1 = new JPasswordField();
    JPasswordField passwordField2 = new JPasswordField();
    JButton submitButton = new JButton("SUBMIT");
    JButton resetButton = new JButton("RESET");
    JTextArea welcomeArea = new JTextArea();
    Border border = BorderFactory.createLineBorder(Color.BLACK);

    private RegisterFormListener registerFormListener;

    public RegisterForm(){
        setLayoutManager();
        addComponents();
        setLocationAndSize();
        styleComponents();
        addActionListeners();


        welcomeArea.append("Welcome to the Exemplar " +
                "\n Management System! " +
                "\n Join by becoming a contributor, " +
                "\n or as a regular user." +
                "\n" +
                "\n Your details: ");

        getRootPane().setDefaultButton(submitButton);
    }


    public void setLayoutManager()
    {
        //Setting layout manager of Container to null
        container.setLayout(null);
    }

    public void setLocationAndSize(){
        title.setSize(300, 30);
        title.setLocation(300, 30);
        userNameLabel.setSize(100, 20);
        userNameLabel.setLocation(100, 100);
        userNameText.setSize(190, 20);
        userNameText.setLocation(200, 100);
        fullNameLabel.setSize(100, 20);
        fullNameLabel.setLocation(100, 150);
        fullNameText.setSize(190,20);
        fullNameText.setLocation(200, 150);
        passwordLabel1.setSize(100,20);
        passwordLabel2.setSize(100,20);
        passwordLabel1.setLocation(100,200);
        passwordLabel2.setLocation(100,250);
        passwordField1.setSize(100,20);
        passwordField2.setSize(100,20);
        passwordField1.setLocation(200,200);
        passwordField2.setLocation(200,250);
        contributorLabel.setSize(120,20);
        contributorLabel.setLocation(100,300);
        contributorCheckBox.setSize(20,20);
        contributorCheckBox.setLocation(230,300);
        submitButton.setSize(100, 20);
        resetButton.setSize(100,20);
        submitButton.setLocation(100, 350);
        resetButton.setLocation(210,350);
        welcomeArea.setLocation(400,100);
        welcomeArea.setSize(300, 270);
    }

    public void styleComponents(){


        title.setFont(new Font("Arial", Font.PLAIN, 30));
        userNameLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        userNameText.setFont(new Font("Arial", Font.PLAIN, 15));
        fullNameLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        fullNameText.setFont(new Font("Arial", Font.PLAIN, 15));
        passwordLabel1.setFont(new Font("Arial", Font.PLAIN, 20));
        passwordLabel2.setFont(new Font("Arial", Font.PLAIN, 20));
        contributorLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        welcomeArea.setBorder(border);
        userNameText.setBorder(border);
        fullNameText.setBorder(border);
        passwordField1.setBorder(border);
        passwordField2.setBorder(border);

    }

    public void addComponents(){
        container.add(title);
        container.add(userNameLabel);
        container.add(userNameText);
        container.add(fullNameLabel);
        container.add(fullNameText);
        container.add(passwordLabel1);
        container.add(passwordLabel2);
        container.add(passwordField1);
        container.add(passwordField2);
        container.add(submitButton);
        container.add(resetButton);
        container.add(contributorLabel);
        container.add(contributorCheckBox);
        container.add(welcomeArea);
    }

    void addActionListeners(){
        submitButton.addActionListener((x)-> {
            String username = userNameText.getText();
            String fullname = fullNameText.getText();
            String password1 = passwordField1.getText();
            String password2 = passwordField2.getText();
            int contributor = contributorCheckBox.isSelected() ? 1 : 0;
            if(!password1.equals(password2)){
                JOptionPane.showMessageDialog(this, "Passwords do not match");
            }else{
                UserEvent event = new UserEvent(username, fullname,password1, contributor);
                registerFormListener.registrationSubmitted(event);
            }
        });

        resetButton.addActionListener((x -> {
            userNameText.setText("");
            fullNameText.setText("");
            passwordField1.setText("");
            passwordField2.setText("");
            if(contributorCheckBox.isSelected()) contributorCheckBox.doClick();
        }));

    }
    public RegisterFormListener getRegisterFormListener() {
        return registerFormListener;
    }

    public void setRegisterFormListener(RegisterFormListener registerFormListener) {
        this.registerFormListener = registerFormListener;
    }

    public JCheckBox getContributorCheckBox() {
        return contributorCheckBox;
    }

    public Container getContainer() {
        return container;
    }
}
