package view.panels.mainFrame;

import controller.MainController;
import model.entities.Community;
import model.httpclients.CommunityClient;
import view.listeners.mainframe.ActionWithComponentListener;
import view.listeners.mainframe.homeTab.NewTabListener;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Panel that lists all the communites of the system
 */
public class CommunityLibraryTab extends JPanel{
    JPanel communityPanelParent = new JPanel();
    private JScrollPane scrollPane;
    Border border = BorderFactory.createBevelBorder(0);
    JPanel buttonPanel;

    private ActionWithComponentListener closeListener;
    private NewTabListener communityListener;

    private JComboBox sortingComboBox;
    private JComboBox sortingComboBox2;

    private ItemListener sortingListener;

    private List<Community> allCommunities;
    private Map<Community, double []> exemplarMap = new HashMap();
    private Map<String, JCheckBox> selectedCommunityMap = new HashMap<>();

    public CommunityLibraryTab(String searchTerm){
        scrollPane = new JScrollPane(communityPanelParent);
        scrollPane.setLayout(new ScrollPaneLayout());

        fetchCommunities(searchTerm);
        communityPanelParent.setLayout(new GridLayout(allCommunities.size()+1, 1));
        addCommunitiesToScrollPane();
        initializeSortingListener();
        initializeButtonPanel();
        addComponents();
    }

    /**
     * Fetches all the communities from the database
     * @param searchTerm a string used to search communities by a specific term
     */
    public void fetchCommunities(String searchTerm){
        allCommunities = MainController.communities
                .stream().filter(c->c.getName().toLowerCase().contains(searchTerm.toLowerCase()))
                .collect(Collectors.toList());
        allCommunities = allCommunities
                .stream()
                .filter(c->c.getName() != null)
                .collect(Collectors.toList());
    }

    /**
     * Adds all the communities to the scroll pane
     */
    public void addCommunitiesToScrollPane(){
        List<Community> communities = allCommunities;
        for(Community c : communities){
            if(c.getName() != null) {
                JPanel panel = new JPanel();
                panel.setLayout(new GridLayout(4, 3));

                panel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent event) {
                        if(event.getClickCount()==2 && event.getButton() == MouseEvent.BUTTON1){
                            List<String> users = new ArrayList<>();
                            users.add(c.getName());
                        }
                    }
                });


                JLabel userName = new JLabel(c.getName());
                userName.setFont(new Font("Verdana", Font.BOLD, 14));
                JLabel labelNumberOfUsers = new JLabel("Number of Users: ");
                JLabel numberOfUsers = new JLabel(String.valueOf(c.getMembers().size()));

                JCheckBox checkBox = new JCheckBox();

                panel.add(userName);
                panel.add(new JLabel());
                panel.add(labelNumberOfUsers);
                panel.add(numberOfUsers);
                panel.add(new JLabel(""));
                StringBuilder labels = new StringBuilder();
                panel.add(new JLabel(labels.toString()));
                panel.add(checkBox);
                panel.setBorder(border);
                panel.setPreferredSize(new Dimension(200, 75));
                selectedCommunityMap.put(c.getName(), checkBox);
                communityPanelParent.add(panel);
            }
        }

    }
    /**
     * Adds all the components to the panel
     */
    void addComponents(){
        setVisible(false);
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        //adding scrollpane
        c.fill = GridBagConstraints.BOTH;
        c.weighty=0.95;
        c.weightx=1;
        c.gridx=0;
        c.gridy=0;
        add(scrollPane, c);

        //adding button
        c.weighty=0.05;
        c.gridx=0;
        c.gridy=1;
        add(buttonPanel, c);
        setVisible(true);

    }
    /**
     * Initializes the button panel
     */
    void initializeButtonPanel(){
        buttonPanel= new JPanel();
        buttonPanel.setLayout(new GridLayout(1,3));
        String [] sortingComboBoxList = {"Sort Alphabetically"};
        String [] sortingComboBoxList2 = {"ascending", "descending" };
        sortingComboBox = new JComboBox(sortingComboBoxList);
        sortingComboBox2 = new JComboBox(sortingComboBoxList2);
        JButton openCommunityButton = new JButton("Open Selected");
        JButton closeLibraryButton = new JButton("Close Library");
        sortingComboBox.addItemListener(sortingListener);
        sortingComboBox2.addItemListener(sortingListener);


        buttonPanel.add(sortingComboBox2);
        buttonPanel.add(openCommunityButton);
        buttonPanel.add(closeLibraryButton);
        buttonPanel.setBorder(border);

        openCommunityButton.addActionListener(x ->openCommunities());
        closeLibraryButton.addActionListener(x->closeListener.componentSubmitted(this));
    }

    /**
     * Initializes sorting listener that sorts the communities according to the combo-boxes
     */
    private void initializeSortingListener() {
        sortingListener = event -> {
            /**
             * Sort alphabetically
             */
            if(sortingComboBox.getSelectedIndex() == 0) {
                allCommunities = allCommunities.stream().
                        sorted(Comparator.comparing(Community::getName)).collect(Collectors.toList());

                if(sortingComboBox2.getSelectedIndex() == 1) {
                    Collections.reverse(allCommunities);
                }
            }
            /**
             * Sort by number of users
             */
            if(sortingComboBox.getSelectedIndex() == 2) {
                allCommunities= allCommunities.stream().
                        sorted(Comparator.comparingDouble(c -> exemplarMap.get(c)[1])).collect(Collectors.toList());

                if(sortingComboBox2.getSelectedIndex() == 1){
                    Collections.reverse(allCommunities);

                }
            }
            updateTab();
        };
    }

    /**
     * Updates the panel by removing all communities and adding them once more
     */
    public void updateTab (){
        removeAll();
        communityPanelParent.removeAll();
        addCommunitiesToScrollPane();
        addComponents();
    }

    /**
     * Opens new tabs for the communities which were requested by the user
     */
    void openCommunities(){
        Set<Map.Entry<String, JCheckBox>> entrySet = selectedCommunityMap.entrySet();
        List<String> selectedCommunity = new ArrayList<>();
        for(Map.Entry<String, JCheckBox> e: entrySet){
            if(e.getValue().isSelected()) {
                selectedCommunity.add(e.getKey());
                e.getValue().doClick();
            }
        }
        communityListener.tabRequested(selectedCommunity);
    }

    public void setCloseListener(ActionWithComponentListener closeListener) {
        this.closeListener = closeListener;
    }

    public void setCommunityListener(NewTabListener communityListener) {
        this.communityListener = communityListener;
    }
}

