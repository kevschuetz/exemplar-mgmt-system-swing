package view.panels.mainFrame.homeTab;

import model.entities.User;

import javax.swing.*;
import java.awt.*;

public class MyExemplarsPanel extends JPanel {


    public MyExemplarsPanel(User user){
        setLayout(new GridLayout(6,2));
        JLabel label = new JLabel("my exemplars");
        add(label);
    }
}
