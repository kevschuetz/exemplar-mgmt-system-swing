package view.frames;

import view.listeners.CustomListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame{
    public CustomListener customListener;


    public MainFrame(){
        setVisible(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(500, 500));


        JPanel jPanel = new JPanel();
        jPanel.setVisible(false);
        jPanel.setSize(100, 100);

        JTextField textField = new JTextField(10);
        JButton button = new JButton("Print");


        jPanel.add(textField);
        jPanel.add(button);
        add(jPanel);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                customListener.listenerActivated(textField.getText());
            }
        });
        jPanel.setVisible(true);
    }
}
