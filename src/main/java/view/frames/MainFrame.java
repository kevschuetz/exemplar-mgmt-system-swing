package view.frames;

import view.listeners.mainframe.MainFrameListener;
import view.panels.mainFrame.AllExemplarsPanel;

import javax.swing.*;
import java.awt.*;


public class MainFrame extends JFrame{
    private MainFrameListener listener;
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

        button.addActionListener((x)->listener.listenerActivated(textField.getText()));

        //set all components to visible except "this"
        jPanel.setVisible(true);
    }


    public void setListener(MainFrameListener listener) {
        this.listener = listener;
    }
}
