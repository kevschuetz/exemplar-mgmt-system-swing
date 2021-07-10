package view.frames.mainFrame;

import view.listeners.mainframe.NewExemplarListener;

import javax.swing.*;
import java.awt.*;

/**
 * Frame that takes the name of a new exemplar to be created
 */
public class NewExemplarPopupFrame extends JFrame {
    private JPanel infoPanel = new JPanel();
    private JLabel infoLabel = new JLabel("Please provide a name for your new Exemplar");
    private JPanel namePanel = new JPanel();
    private JLabel nameLabel = new JLabel("Name:");
    private JTextField nameField = new JTextField();
    private JButton button = new JButton("Create");

    private NewExemplarListener listener;
    public NewExemplarPopupFrame(){
        JPanel parentPanel = new JPanel();
        parentPanel.setLayout(new GridLayout(3,1));
        parentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("New Exemplar"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        setSize(new Dimension(100,100));
        setLayout(new GridLayout(1,1));
        infoPanel.add(infoLabel);
        namePanel.setLayout(new GridLayout(1,2));
        namePanel.add(nameLabel);
        namePanel.add(nameField);

        button.addActionListener(x->listener.NewExemplarRequested(nameField.getText()));

        parentPanel.add(infoPanel);
        parentPanel.add(namePanel);
        parentPanel.add(button);
        add(parentPanel);
        getRootPane().setDefaultButton(button);
    }

    public void setListener(NewExemplarListener listener) {
        this.listener = listener;

    }

    public void clean(){
        nameField.setText("");
    }

}
