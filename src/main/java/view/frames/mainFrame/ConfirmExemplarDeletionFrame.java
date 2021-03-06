package view.frames.mainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Frame that takes a text to confirm deletion of exemplar
 */
public class ConfirmExemplarDeletionFrame extends JFrame {
    private JPanel parentPanel = new JPanel();
    private JLabel label = new JLabel("Please confirm by typing in the name of the exemplar");
    private JTextField textField = new JTextField();
    private JButton button = new JButton("Delete");
    private ActionListener confirmListener;

    public ConfirmExemplarDeletionFrame(String exemplarName){
        setLayout(new GridLayout(1,1));
        parentPanel.setLayout(new GridLayout(3,1));
        parentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("No turning back"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        parentPanel.add(label);
        parentPanel.add(textField);
        parentPanel.add(button);
        add(parentPanel);
        button.addActionListener(x->{
            if(textField.getText().equals(exemplarName))confirmListener.actionPerformed(x);
        });
        getRootPane().setDefaultButton(button);
    }

    public ActionListener getConfirmListener() {
        return confirmListener;
    }

    public void setConfirmListener(ActionListener confirmListener) {
        this.confirmListener = confirmListener;
    }
}
