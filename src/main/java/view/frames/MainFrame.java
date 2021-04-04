package view.frames;

import view.listeners.CustomListener;
import view.panels.mainFrame.AllExemplarsPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame{
    public CustomListener customListener;
    private AllExemplarsPanel exemplarList;

    public MainFrame() {
        setVisible(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(1000, 750));
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        setLayout(new BorderLayout());

        JPanel jPanel = new JPanel();
        jPanel.setVisible(false);
        jPanel.setSize(100, 100);

        JTextField textField = new JTextField(10);
        JButton button = new JButton("Print");


        jPanel.add(textField);
        jPanel.add(button);
        add(BorderLayout.PAGE_START, jPanel);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                customListener.listenerActivated(textField.getText());
            }
        });

        exemplarList = new AllExemplarsPanel();
        add(BorderLayout.WEST, exemplarList);

        jPanel.setVisible(true);
    }
}
