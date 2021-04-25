package view.panels.mainFrame.homeTab;

import model.entities.User;

import javax.swing.*;
import java.awt.*;

public class MyCommunitiesPanel extends JPanel {


    public MyCommunitiesPanel(User user){
        setLayout(new GridLayout(6,2));
        JLabel label = new JLabel("my communities");
        add(label);
    }
}
