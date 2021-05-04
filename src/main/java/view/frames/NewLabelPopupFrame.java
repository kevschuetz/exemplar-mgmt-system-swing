package view.frames;

import view.listeners.mainframe.NewExemplarListener;
import view.listeners.mainframe.NewLabelListener;

import javax.swing.*;
import java.awt.*;

public class NewLabelPopupFrame extends JFrame{
    private JPanel infoPanel = new JPanel();
    private JLabel infoLabel = new JLabel("Enter a value for the label");
    private JPanel namePanel = new JPanel();
    private JLabel nameLabel = new JLabel("Name:");
    private JTextField nameField = new JTextField();
    private JButton button = new JButton("Add to Exemplar");

    private NewLabelListener listener;
    public NewLabelPopupFrame(){
        JPanel parentPanel = new JPanel();
        parentPanel.setLayout(new GridLayout(3,1));
        parentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("New Label"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        setSize(new Dimension(100,100));
        setLayout(new GridLayout(1,1));
        infoPanel.add(infoLabel);
        namePanel.setLayout(new GridLayout(1,2));
        namePanel.add(nameLabel);
        namePanel.add(nameField);

        button.addActionListener(x->listener.labelRequested(nameField.getText()));

        parentPanel.add(infoPanel);
        parentPanel.add(namePanel);
        parentPanel.add(button);
        add(parentPanel);
        getRootPane().setDefaultButton(button);
    }

    public void setListener(NewLabelListener listener) {
        this.listener = listener;
    }




}
