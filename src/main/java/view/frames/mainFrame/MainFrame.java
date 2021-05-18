package view.frames.mainFrame;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;


public class MainFrame extends JFrame{
    private JTabbedPane tabPanel = new JTabbedPane();
    private MenuPanel menuPanel = new MenuPanel();
    private CMenuBar menuBar = new CMenuBar();


    public MainFrame() {

        addComponents();
    }

    void addComponents(){
        setLayout(new GridBagLayout());
        setJMenuBar(menuBar);
        int y = 0;
        int x = 0;
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;

        c.weightx = 1;
        c.weighty = 0.01;
        c.gridy = y;
        c.gridx = x;
        add(menuPanel, c);


        y++;
        c.gridy = y;
        c.weighty = 0.95;
        add(tabPanel,c);
    }

    public void addTab(String title, Component component){
        tabPanel.addTab(title, component);
    }

    public void removeTab(Component component){
        tabPanel.remove(component);
    }

    private class MenuPanel extends JPanel{
        private JButton exemplarButton;
        private JButton communityButton;
        private JButton contributorButton;

        MenuPanel(){
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createTitledBorder(""),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)));
            exemplarButton = new JButton("Exemplars");
            communityButton = new JButton("Communities");
            contributorButton = new JButton("Contributors");
            exemplarButton.setBackground(Color.lightGray);
            communityButton.setBackground(Color.lightGray);
            contributorButton.setBackground(Color.lightGray
            );
            Border emptyBorder = BorderFactory.createEmptyBorder();
            exemplarButton.setBorder(emptyBorder);
            communityButton.setBorder(emptyBorder);
            contributorButton.setBorder(emptyBorder);


            setLayout(new GridLayout(3,9));
            addDummyPanels(9);
            addDummyPanels(3);
            add(exemplarButton);
            add(communityButton);
            add(contributorButton);
            addDummyPanels(3);
            addDummyPanels(9);

        }

        void addDummyPanels(int j){
            for(int i = 0; i < j; i++){
                JPanel newPanel = new JPanel();
                newPanel.setBackground(Color.LIGHT_GRAY);
                add(newPanel);
            }
        }
    }

    private class CMenuBar extends JMenuBar{
        private JMenu menu = new JMenu("Menu");
        private JMenuItem importItem = new JMenuItem("Import");
        CMenuBar(){
            menu.add(importItem);
            add(menu);

        }
    }
}
