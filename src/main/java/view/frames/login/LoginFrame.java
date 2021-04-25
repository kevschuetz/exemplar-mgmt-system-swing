package view.frames.login;

import view.listeners.login.LoginListener;
import view.listeners.login.RegisterButtonListener;

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
    JButton guestButton=new JButton("GUEST");
    JButton registerButton=new JButton("REGISTER");
    JCheckBox showPassword=new JCheckBox("Show Password");
    LoginListener loginListener;
    RegisterButtonListener registerListener;
    JLabel title = new JLabel("LOGIN");

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
        guestButton.setBounds(50,350,100,30);
        registerButton.setBounds(200,350,100,30);
        title.setBounds(130,50,100,30);
        title.setFont(new Font("Arial", Font.PLAIN, 30));
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
        container.add(guestButton);
        container.add(registerButton);
        container.add(title);
    }

    public void addListenersToComponents(){
        loginButton.addActionListener(x-> loginListener.loginRequested(userTextField.getText(), passwordField.getText()));
        showPassword.addActionListener(x->{
            if (showPassword.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('*');
            }
        });
        registerButton.addActionListener(x->registerListener.registerFormRequested());
        resetButton.addActionListener((x)->{
            userTextField.setText("");
            passwordField.setText("");
        });
        guestButton.addActionListener((x)->{
            loginListener.loginRequested("guest","");
        });
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

    public void setRegisterListener(RegisterButtonListener registerListener) {
        this.registerListener = registerListener;
    }

    public JButton getLoginButton() {
        return loginButton;
    }

    public Container getContainer() {
        return container;
    }

    public JCheckBox getShowPassword() {
        return showPassword;
    }
}
