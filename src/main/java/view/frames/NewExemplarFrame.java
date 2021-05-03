package view.frames;

import javax.swing.*;
import java.awt.*;

public class NewExemplarFrame extends JFrame {
    private JPanel infoPanel = new JPanel();
    private JLabel infoLabel = new JLabel("Please provide a name for your new Exemplar");
    private JPanel namePanel = new JPanel();
    private JLabel nameLabel = new JLabel("Name:");
    private JTextField nameField = new JTextField();


    public NewExemplarFrame(){
        setSize(new Dimension(100,100));
        setLayout(new GridLayout(2,1));
        infoPanel.add(infoLabel);
        namePanel.setLayout(new GridLayout(1,2));
        namePanel.add(nameLabel);
        namePanel.add(nameField);
    }


}
