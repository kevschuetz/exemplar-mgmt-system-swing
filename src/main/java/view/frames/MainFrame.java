package view.frames;

import javax.swing.*;
import java.awt.*;


public class MainFrame extends JFrame{
    private JTabbedPane tabPanel = new JTabbedPane();



    public MainFrame() {
        addComponents();
    }

    void addComponents(){
        add(tabPanel);
    }

    public void addTab(String title, Component component){
        tabPanel.addTab(title, component);
    }

    public void removeTab(Component component){
        tabPanel.remove(component);
    }


}
