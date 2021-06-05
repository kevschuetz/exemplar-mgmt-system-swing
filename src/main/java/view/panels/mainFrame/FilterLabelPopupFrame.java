package view.panels.mainFrame;


import view.panels.mainFrame.exemplarTab.ExemplarTab;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;


public class FilterLabelPopupFrame extends JFrame {
    private JPanel infoPanel = new JPanel();
    private JLabel infoLabel = new JLabel("Enter a label");
    private JPanel namePanel = new JPanel();
    private JLabel nameLabel = new JLabel("Label:");
    private JTextField labelField = new JTextField();
    private JButton button = new JButton("Filter");
    private ExemplarTab tab;

    private ActionListener listener;

    public FilterLabelPopupFrame (){
        JPanel parentPanel = new JPanel();
        parentPanel.setLayout(new GridLayout(3,1));
        parentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Filter by label"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        setSize(new Dimension(100,100));
        setLayout(new GridLayout(1,1));
        infoPanel.add(infoLabel);
        namePanel.setLayout(new GridLayout(1,2));
        namePanel.add(nameLabel);
        namePanel.add(labelField);

        //button.addActionListener(x->listener.(labelField.getText()));

        parentPanel.add(infoPanel);
        parentPanel.add(namePanel);
        parentPanel.add(button);
        add(parentPanel);
        getRootPane().setDefaultButton(button);
    }

    public void setListener(ActionListener listener) {
        this.listener = listener;
    }

    public void clean(){
        labelField.setText("");
    }
}


