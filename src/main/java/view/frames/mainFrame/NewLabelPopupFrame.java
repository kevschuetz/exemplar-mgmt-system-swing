package view.frames.mainFrame;

import view.listeners.mainframe.NewLabelListener;
import view.panels.mainFrame.exemplarTab.ExemplarTab;

import javax.swing.*;
import java.awt.*;

/**
 * Frame that takes the value of a new label to be assigned to an exempalar
 */
public class NewLabelPopupFrame extends JFrame{
    private JPanel infoPanel = new JPanel();
    private JLabel infoLabel = new JLabel("Enter a value for the label");
    private JPanel namePanel = new JPanel();
    private JLabel nameLabel = new JLabel("Name:");
    private JTextField nameField = new JTextField();
    private JButton button = new JButton("Add to Exemplar");
    private ExemplarTab tab;

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

    public ExemplarTab getTab() {
        return tab;
    }

    public void setTab(ExemplarTab tab) {
        this.tab = tab;
    }

    public void clean(){
        nameField.setText("");
    }
}
