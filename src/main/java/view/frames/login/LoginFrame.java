package view.frames.login;

import view.listeners.login.LoginListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame implements ActionListener {
    Container container = getContentPane();
    JLabel userLabel=new JLabel("USERNAME");
    JLabel passwordLabel=new JLabel("PASSWORD");
    JTextField userTextField=new JTextField();
    JPasswordField passwordField=new JPasswordField();
    JButton loginButton=new JButton("LOGIN");
    JButton resetButton=new JButton("RESET");
    JCheckBox showPassword=new JCheckBox("Show Password");
    LoginListener loginListener;

    public LoginFrame(){
        setLayoutManager();
        setLocationAndSize();
        addComponentsToContainer();
        addListenersToComponents();
    }

    public void setLayoutManager()
    {
        //Setting layout manager of Container to null
        container.setLayout(null);
    }

    public void setLocationAndSize()
    {
        //Setting location and Size of each components using setBounds() method.
        userLabel.setBounds(50,150,100,30);
        passwordLabel.setBounds(50,220,100,30);
        userTextField.setBounds(150,150,150,30);
        passwordField.setBounds(150,220,150,30);
        showPassword.setBounds(150,250,150,30);
        loginButton.setBounds(50,300,100,30);
        resetButton.setBounds(200,300,100,30);


    }
    public void addComponentsToContainer()
    {
        //Adding each component to the Container
        container.add(userLabel);
        container.add(passwordLabel);
        container.add(userTextField);
        container.add(passwordField);
        container.add(showPassword);
        container.add(loginButton);
        container.add(resetButton);
    }

    public void addListenersToComponents(){
        loginButton.addActionListener((x)-> loginListener.loginRequested(userTextField.getText(), passwordField.getText()));
    }

    public LoginListener getLoginListener() {
        return loginListener;
    }

    public void setLoginListener(LoginListener loginListener) {
        this.loginListener = loginListener;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
