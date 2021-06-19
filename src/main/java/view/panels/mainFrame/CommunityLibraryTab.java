package view.panels.mainFrame;

import model.entities.Community;
import model.entities.Exemplar;
import model.entities.Label;
import model.httpclients.CommunityClient;
import model.httpclients.ExemplarClient;
import model.httpclients.RatingClient;
import view.frames.mainFrame.FilterLabelPopupFrame;
import view.listeners.mainframe.ActionWithComponentListener;
import view.listeners.mainframe.FilterByLabelListener;
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

    private ExemplarClient exemplarClient = new ExemplarClient();
    private RatingClient ratingClient = new RatingClient();

    private JComboBox sortingComboBox;
    private JComboBox sortingComboBox2;

    private ItemListener sortingListener;
    private ActionWithComponentListener closeListener;
    FilterByLabelListener filterListener;
    private NewTabListener userListener;

    FilterLabelPopupFrame filterLabelPopupFrame;

    private List<String> filteredLabels = new ArrayList<>();
    private Set<Label> allLabels = new HashSet<>();
    private List<Community> allCommunities;
    private Map<Community, double []> exemplarMap = new HashMap();
    private Map <Community, List<model.entities.Label>> labelsPerCommunity = new HashMap();
    private Map<String, JCheckBox> selectedCommunityMap = new HashMap<>();
    private Map<Community, List<Exemplar>> communityExemplarMap = new HashMap<>();

    boolean filtered = false;

    public CommunityLibraryTab(String searchTerm){
        scrollPane = new JScrollPane(communityPanelParent);
        scrollPane.setLayout(new ScrollPaneLayout());

        fetchCommunities(searchTerm);
        communityPanelParent.setLayout(new GridLayout(allCommunities.size()+1, 1));
        addUsersToScrollPane();
        initializeSortingListener();
        initializeFilterLabelFrame();
        initializeButtonPanel();
        addComponents();
    }

    public void fetchCommunities(String searchTerm){
        allCommunities = new CommunityClient().searchCommunities(searchTerm);
        allCommunities = allCommunities
                .stream()
                .filter(u->u.getName() != null)
                .collect(Collectors.toList());

    }


    public void addUsersToScrollPane(){
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
                            userListener.tabRequested(users);
                        }
                    }
                });

                JLabel name = new JLabel("Name: ");
                JLabel userName = new JLabel(c.getName());
                JLabel labelCreator = new JLabel("Creator: ");
                JLabel creator = new JLabel((Icon) c.getCreator());
                JLabel labelNumberOfUsers = new JLabel("Number of Users: ");
                JLabel numberOfUsers = new JLabel((Icon) c.getMembers());
                JLabel labelNumberOfExemplars = new JLabel("Number of Exemplars: ");
                JLabel numberOfExemplars = new JLabel(String.valueOf((int)exemplarMap.get(c)[1]));
                JLabel labelAverageRatingOfExemplars = new JLabel("Average Rating: ");
                JLabel averageRatingOfExemplars = new JLabel(String.valueOf(Math.round(exemplarMap.get(c)[0] * 100.00) / 100.00));
                JLabel labelExemplarLabels = new JLabel("Labels of Exemplars: ");

                JCheckBox checkBox = new JCheckBox();
                // if (i % 2 == 0) checkBox.setBackground(Color.LIGHT_GRAY);
                panel.add(name);
                panel.add(userName);
                panel.add(new JLabel(""));
                panel.add(labelCreator);
                panel.add(creator);
                panel.add(new JLabel(""));
                panel.add(labelNumberOfUsers);
                panel.add(numberOfUsers);
                panel.add(new JLabel(""));
                panel.add(labelNumberOfExemplars);
                panel.add(numberOfExemplars);
                panel.add(new JLabel(""));
                panel.add(labelAverageRatingOfExemplars);
                panel.add(averageRatingOfExemplars);
                panel.add(new JLabel(""));
                panel.add(labelExemplarLabels);
                StringBuilder labels = new StringBuilder();
                /*for(model.entities.Label l: labelsPerContributor.get(u)){
                    labels.append(l.getValue() + "   ");
                }*/
                panel.add(new JLabel(labels.toString()));
                panel.add(checkBox);
                panel.setBorder(border);
                panel.setPreferredSize(new Dimension(200, 50));
                //if (i % 2 == 0) panel.setBackground(Color.LIGHT_GRAY);
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
        String [] sortingComboBoxList = {"Sort Alphabetically", "Sort by average Rating of Exemplars", "Sort by Number of Exemplars"};
        String [] sortingComboBoxList2 = {"ascending", "descending" };
        sortingComboBox = new JComboBox(sortingComboBoxList);
        sortingComboBox2 = new JComboBox(sortingComboBoxList2);
        JButton openContributorsButton = new JButton("Open Selected");
        JButton closeLibraryButton = new JButton("Close Library");
        sortingComboBox.addItemListener(sortingListener);
        sortingComboBox2.addItemListener(sortingListener);

        openContributorsButton.addActionListener((x)->openContributors());
        closeLibraryButton.addActionListener((x)->closeListener.componentSubmitted(this));

        buttonPanel.add(sortingComboBox);
        buttonPanel.add(sortingComboBox2);
        buttonPanel.add(openContributorsButton);
        buttonPanel.add(closeLibraryButton);
        buttonPanel.setBorder(border);
    }

    void openContributors(){
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
                 * Sort by avg rating of exemplar
                 */
                if(sortingComboBox.getSelectedIndex() == 1) {
                    allCommunities = allCommunities.stream().
                            sorted(Comparator.comparingDouble(c -> exemplarMap.get(c)[0])).collect(Collectors.toList());


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
        addUsersToScrollPane();
        addComponents();
    }

    void initializeFilterLabelFrame(){
        filterLabelPopupFrame = new FilterLabelPopupFrame(allLabels, "Filter Contributors");
        filterLabelPopupFrame.setVisible(false);
        filterLabelPopupFrame.setSize(new Dimension(350, 400));
        filterLabelPopupFrame.setLocationRelativeTo(this);

        filterLabelPopupFrame.setListener((labels) -> {
            filteredLabels = labels;
            if(filteredLabels.size()==0) filtered = false;
            //else filterUsers();
            updateTab();
            filterLabelPopupFrame.setVisible(false);

        });
    }



    public void setCloseListener(ActionWithComponentListener closeListener) {
        this.closeListener = closeListener;
    }

    public void setContributorListener(NewTabListener userListener) {
        this.userListener = userListener;
    }
}

