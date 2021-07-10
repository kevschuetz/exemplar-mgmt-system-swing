package view.frames.mainFrame;

import view.listeners.mainframe.NewCommentListener;
import view.panels.mainFrame.exemplarTab.ExemplarTab;

import javax.swing.*;
import java.awt.*;

/**
 * Popup-Frame where a comment can be entered
 */
public class AddCommentPopupFrame extends JFrame {
    private JPanel infoPanel = new JPanel();
    private JLabel infoLabel = new JLabel("Enter a comment:");
    private JTextField commentField = new JTextField();
    private JButton button;
    private ExemplarTab tab;

    private NewCommentListener listener;

    public AddCommentPopupFrame(){
        JPanel parentPanel = new JPanel();
        parentPanel.setLayout(new GridLayout(3,1));
        parentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Your Comment"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        setSize(new Dimension(100,100));
        setLayout(new GridLayout(1,1));
        infoPanel.add(infoLabel);
        button = new JButton("Publish");
        button.addActionListener(x->listener.addNewComment(commentField.getText()));

        parentPanel.add(infoPanel);
        parentPanel.add(commentField);
        parentPanel.add(button);
        add(parentPanel);
        getRootPane().setDefaultButton(button);
    }

    public void setListener(NewCommentListener listener) {
        this.listener = listener;
    }

    public ExemplarTab getTab() {
        return tab;
    }

    public String getComment(){
        return commentField.getText();
    }

    public void clean(){
        commentField.setText("");
    }
}

