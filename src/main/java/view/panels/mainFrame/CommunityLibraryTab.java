package view.panels.mainFrame;

import model.entities.Community;
import model.httpclients.CommunityClient;
import model.httpclients.ExemplarClient;
import view.listeners.mainframe.ActionWithComponentListener;
import view.listeners.mainframe.homeTab.NewTabListener;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class CommunityLibraryTab extends JPanel{
    JPanel communityPanelParent = new JPanel();
    private JScrollPane scrollPane;
    Border border = BorderFactory.createBevelBorder(0);
    JPanel buttonPanel;

    private CommunityClient communityClient = new CommunityClient();
    private ExemplarClient exemplarClient = new ExemplarClient();

    private JComboBox sortingComboBox;
    private JComboBox sortingComboBox2;

    private ItemListener sortingListener;
    private ActionWithComponentListener closeListener;
    private NewTabListener userListener;

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


    public void fetchCommunities(String searchTerm){
        allCommunities = new CommunityClient().searchCommunities(searchTerm);
        allCommunities = allCommunities
                .stream()
                .filter(c->c.getName() != null)
                .collect(Collectors.toList());
        if(allCommunities.size() <= 0) System.exit(0);
    }


    public void addCommunitiesToScrollPane(){
        int i = 0;
        List<Community> communities = allCommunities;
        for(Community c : communities){
            if(c.getName() != null) {
                JPanel panel = new JPanel();
                panel.setLayout(new GridLayout(5, 3));

                panel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent event) {
                        if(event.getClickCount()==2 && event.getButton() == MouseEvent.BUTTON1){
                            List<String> users = new ArrayList<>();
                            users.add(c.getName());
                            //userListener.tabRequested(users);
                        }
                    }
                });

                JLabel name = new JLabel("Name: ");
                JLabel userName = new JLabel(c.getName());
                JLabel labelNumberOfUsers = new JLabel("Number of Users: ");
                JLabel numberOfUsers = new JLabel(String.valueOf(c.getMembers().size()));

                JCheckBox checkBox = new JCheckBox();
                panel.add(name);
                panel.add(userName);
                panel.add(labelNumberOfUsers);
                panel.add(numberOfUsers);
                panel.add(new JLabel(""));
                StringBuilder labels = new StringBuilder();
                panel.add(new JLabel(labels.toString()));
                panel.add(checkBox);
                panel.setBorder(border);
                panel.setPreferredSize(new Dimension(200, 50));
                selectedCommunityMap.put(c.getName(), checkBox);
                communityPanelParent.add(panel);
                i++;
            }
        }

    }

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

    void initializeButtonPanel(){
        buttonPanel= new JPanel();
        buttonPanel.setLayout(new GridLayout(1,3));
        String [] sortingComboBoxList = {"Sort Alphabetically"};
        String [] sortingComboBoxList2 = {"ascending", "descending" };
        sortingComboBox = new JComboBox(sortingComboBoxList);
        sortingComboBox2 = new JComboBox(sortingComboBoxList2);
        JButton openContributorsButton = new JButton("Open Selected");
        JButton closeLibraryButton = new JButton("Close Library");
        sortingComboBox.addItemListener(sortingListener);
        sortingComboBox2.addItemListener(sortingListener);

        openContributorsButton.addActionListener((x)->openCommunities());
        closeLibraryButton.addActionListener((x)->closeListener.componentSubmitted(this));

        //buttonPanel.add(sortingComboBox);
        buttonPanel.add(sortingComboBox2);
        buttonPanel.add(openContributorsButton);
        buttonPanel.add(closeLibraryButton);
        buttonPanel.setBorder(border);
    }

    void openCommunities(){
        Set<Map.Entry<String, JCheckBox>> entrySet = selectedCommunityMap.entrySet();
        List<String> selectedContributors = new ArrayList<>();
        for(Map.Entry<String, JCheckBox> e: entrySet){
            if(e.getValue().isSelected()) {
                selectedContributors.add(e.getKey());
                e.getValue().doClick();
            }
        }
        userListener.tabRequested(selectedContributors);
    }

    private void initializeSortingListener() {
        sortingListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent event) {
                /**
                 * Sort alphabetically
                 */
                if(sortingComboBox.getSelectedIndex() == 0) {
                    allCommunities = allCommunities.stream().
                            sorted(Comparator.comparing(c -> c.getName())).collect(Collectors.toList());

                    if(sortingComboBox2.getSelectedIndex() == 1) {
                        Collections.reverse(allCommunities);
                        //Collections.reverse(filteredContributors);
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
            }
        };
    }

    public void updateTab (){
        removeAll();
        communityPanelParent.removeAll();
        addCommunitiesToScrollPane();
        addComponents();
    }

    public void setCloseListener(ActionWithComponentListener closeListener) {
        this.closeListener = closeListener;
    }

    public void setContributorListener(NewTabListener userListener) {
        this.userListener = userListener;
    }
}

