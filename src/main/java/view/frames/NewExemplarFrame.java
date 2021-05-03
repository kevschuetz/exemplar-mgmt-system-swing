package view.frames;

import view.listeners.mainframe.NewExemplarListener;

import javax.swing.*;
import java.awt.*;

public class NewExemplarFrame extends JFrame {
    private JPanel infoPanel = new JPanel();
    private JLabel infoLabel = new JLabel("Please provide a name for your new Exemplar");
    private JPanel namePanel = new JPanel();
    private JLabel nameLabel = new JLabel("Name:");
    private JTextField nameField = new JTextField();
    private JButton button = new JButton("Create");

    private NewExemplarListener listener;
    public NewExemplarFrame(){
        setSize(new Dimension(100,100));
        setLayout(new GridLayout(3,1));
        infoPanel.add(infoLabel);
        namePanel.setLayout(new GridLayout(1,2));
        namePanel.add(nameLabel);
        namePanel.add(nameField);

        button.addActionListener(x->listener.NewExemplarRequested(nameField.getText()));

        add(infoPanel);
        add(namePanel);
        add(button);
        getRootPane().setDefaultButton(button);
    }

    public void setListener(NewExemplarListener listener) {
        this.listener = listener;

    }

    public void clean(){
        nameField.setText("");
    }

}
