package view.frames.mainFrame;

import view.listeners.ActionWithStringListener;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;


public class MainFrame extends JFrame{
    private JTabbedPane tabPanel = new JTabbedPane();
    private MenuPanel menuPanel = new MenuPanel();
    private CMenuBar menuBar = new CMenuBar();
    private java.util.List<JComponent> openSearchTabs = new ArrayList<>();


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

    public String getSearchTerm(){
        return menuPanel.searchField.getText();
    }

    public void referenceOpenTab(JComponent c){openSearchTabs.add(c);}

    public java.util.List<JComponent> getOpenSearchTabs(){return openSearchTabs;}

    public void setOpenSearchTabs(java.util.List l){openSearchTabs = l;}

    public void addTab(String title, Component component){
        tabPanel.addTab(title, component);
    }

    public void removeTab(Component component){
        tabPanel.remove(component);
    }

    public void setLastTabSelected(){
        tabPanel.setSelectedIndex(tabPanel.getTabCount()-1);
    }

    public void setExemplarButtonListener(ActionListener l){
        menuPanel.exemplarButton.addActionListener(l);

    }

    public void setLogoutListener(ActionListener l){menuBar.logoutListener = l;}

    public void setContributorButtonListener(ActionListener l){
        menuPanel.contributorButton.addActionListener(l);
    }

    public void setSearchExemplarListener(ActionListener l){menuPanel.searchExemplarListener = l;}

    public void setSearchContributorListener(ActionListener l) {menuPanel.searchContributorListener = l;}

    public void setImportListener(ActionWithStringListener importListener) {
        menuBar.importListener = importListener;
    }

    public void setCreateExemplarListener(ActionListener l){menuBar.createExemplarListener = l;}

    private class MenuPanel extends JPanel{
        private JButton exemplarButton;
        private JButton communityButton;
        private JButton contributorButton;
        private JButton searchButton;
        private JComboBox searchCombobox;
        private JTextField searchField;


        ActionListener exemplarButtonListener;
        ActionListener searchExemplarListener;
        ActionListener searchContributorListener;


        MenuPanel(){
            setBorder(BorderFactory.createBevelBorder(0));
            exemplarButton = new JButton("Exemplars");
            communityButton = new JButton("Communities");
            contributorButton = new JButton("Contributors");
            searchButton = new JButton("Search");
            exemplarButton.setBackground(Color.lightGray);
            communityButton.setBackground(Color.lightGray);
            contributorButton.setBackground(Color.lightGray);
            //searchButton.setBackground(Color.lightGray);

            Border emptyBorder = BorderFactory.createEmptyBorder();
            exemplarButton.setBorder(emptyBorder);
            communityButton.setBorder(emptyBorder);
            contributorButton.setBorder(emptyBorder);
            searchButton.setBorder(emptyBorder);


            String[] searchOptions = {"Exemplars", "Contributors", "Communities"};
            searchCombobox = new JComboBox(searchOptions);
            searchField = new JTextField();
            addSearchButtonListener();

            setLayout(new GridLayout(3,9));
            addDummyPanels(9);
            addDummyPanels(3);
            add(exemplarButton);
            add(communityButton);
            add(contributorButton);
            add(searchField);
            add(searchCombobox);
            add(searchButton);
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

        void addSearchButtonListener(){
            searchButton.addActionListener((e)->{
                switch(searchCombobox.getSelectedIndex()){
                    case 0:
                        searchExemplarListener.actionPerformed(e);
                        break;
                    case 1:
                        searchContributorListener.actionPerformed(e);
                        break;
                    case 2:
                        System.out.println("");
                }
            });
        }


    }

    private class CMenuBar extends JMenuBar{
        private JMenu menu = new JMenu("Menu");
        private JMenu exemplarMenu = new JMenu("Exemplars");

        private JMenuItem importExemplarItem = new JMenuItem("Import");
        private JMenuItem createExemplarItem = new JMenuItem("Create");
        private JMenuItem logOutItem = new JMenuItem("Log Out");



        private ActionWithStringListener importListener;
        private ActionListener createExemplarListener;
        private ActionListener logoutListener;

        CMenuBar(){
            exemplarMenu.add(importExemplarItem);
            exemplarMenu.add(createExemplarItem);
            menu.add(exemplarMenu);
            menu.add(logOutItem);
            add(menu);

            addListeners();

        }

        private void addListeners() {
            importExemplarItem.addActionListener((e)->{
                JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                jfc.setDialogTitle("Select a text or json file");
                jfc.setAcceptAllFileFilterUsed(false);
                String[] acceptedExtensions = {"txt","json"};
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Text and JSON Files", acceptedExtensions);
                jfc.addChoosableFileFilter(filter);

                int returnValue = jfc.showOpenDialog(null);

                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = jfc.getSelectedFile();
                    importListener.stringSubmitted(selectedFile.getAbsolutePath());
                }
            });

            createExemplarItem.addActionListener((e)-> createExemplarListener.actionPerformed(e));
            logOutItem.addActionListener((e)->logoutListener.actionPerformed(e));
        }
    }
}
