package view.panels.mainFrame;

import model.entities.Exemplar;
import model.entities.User;
import model.httpclients.ExemplarClient;
import model.httpclients.RatingClient;
import model.httpclients.UserClient;
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
/**
 * Panel that lists all the contributors of the system and provides different sorting, filtering options
 */
public class ContributorLibraryTab extends JPanel {
    JPanel contributorPanelParent = new JPanel();
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
    private NewTabListener contributorListener;

    FilterLabelPopupFrame filterLabelPopupFrame;

    private List <String> filteredLabels = new ArrayList<>();
    private Set<model.entities.Label> allLabels = new HashSet<>();
    private List<User> allContributors;
    private List<User> filteredContributors = new ArrayList<>();
    private Map <User, double []> exemplarMap = new HashMap(); // [0] = average Rating [1] = number of Exemplars
    private Map <User, List<model.entities.Label>> labelsPerContributor = new HashMap();
    private Map<String, JCheckBox> selectedContributorMap = new HashMap<>();
    private Map<User, List<Exemplar>> contributorExemplarMap = new HashMap<>();

    boolean filtered = false;

    public ContributorLibraryTab(String searchTerm){
        scrollPane = new JScrollPane(contributorPanelParent);
        scrollPane.setLayout(new ScrollPaneLayout());

        fetchContributors(searchTerm);
        addExemplarInformation();

        contributorPanelParent.setLayout(new GridLayout(allContributors.size()+1, 1));
        addContributorsToScrollPane();
        initializeSortingListener();
        initializeFilterLabelFrame();
        initializeButtonPanel();
        addComponents();
    }

    /**
     * Fetches all the contributors from the database
     * @param searchTerm a string used to search contributors by a specific term
     */
    public void fetchContributors(String searchTerm){
        allContributors = new UserClient().searchUsers(searchTerm);
        allContributors = allContributors
                .stream()
                .filter(u->u.getIsContributor()==1)
                .sorted(Comparator.comparing(c -> c.getUsername().toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Collects all the relevant information in respect to a contributor's exemplars like average rating or associated labels
     */
    public void addExemplarInformation(){
        for(User u : allContributors){
            List<Exemplar> forUser = exemplarClient.getExemplarsForUser(u.getUsername());
            contributorExemplarMap.put(u, forUser);
            exemplarMap.put(u,
                    new double[]{
                        forUser.stream().
                                mapToDouble(e -> ratingClient.getAvgRatingForExemplar(e.getName())).
                                average().orElse(0),
                        forUser.size()});

            List<model.entities.Label> labelsForContributor = forUser.stream().
                    flatMap(e -> e.getLabels().stream()).collect(Collectors.toList());
            for(model.entities.Label l : labelsForContributor){
                allLabels.add(l);
            }
            labelsPerContributor.put(u, labelsForContributor);
        }

    }
    /**
     * Adds all the contributors to the scroll pane
     */
    public void addContributorsToScrollPane(){
        List<User> contributors = allContributors;
        if(filtered) contributors = filteredContributors;
        for(User u : contributors){
            if(u.getIsContributor() ==1) {
                JPanel panel = new JPanel();
                panel.setLayout(new GridLayout(4, 3));

                panel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent event) {
                        if(event.getClickCount()==2 && event.getButton() == MouseEvent.BUTTON1){
                            List<String> contributors = new ArrayList<>();
                            contributors.add(u.getUsername());
                            contributorListener.tabRequested(contributors);
                        }
                    }
                });

                JLabel userName = new JLabel(u.getUsername());
                userName.setFont(new Font("Verdana", Font.BOLD, 14));
                JLabel labelNumberOfExemplars = new JLabel("Number of Exemplars: ");
                JLabel numberOfExemplars = new JLabel(String.valueOf((int)exemplarMap.get(u)[1]));
                JLabel labelAverageRatingOfExemplars = new JLabel("Average Rating: ");
                JLabel averageRatingOfExemplars = new JLabel(String.valueOf(Math.round(exemplarMap.get(u)[0] * 100.00) / 100.00));
                JLabel labelExemplarLabels = new JLabel("Labels of Exemplars: ");

                JCheckBox checkBox = new JCheckBox();

                panel.add(userName);
                panel.add(new JLabel());
                panel.add(new JLabel(""));
                panel.add(labelNumberOfExemplars);
                panel.add(numberOfExemplars);
                panel.add(new JLabel(""));
                panel.add(labelAverageRatingOfExemplars);
                panel.add(averageRatingOfExemplars);
                panel.add(new JLabel(""));
                panel.add(labelExemplarLabels);
                StringBuilder labels = new StringBuilder();
                for(model.entities.Label l: labelsPerContributor.get(u)){
                    labels.append(l.getValue() + "   ");
                }
                panel.add(new JLabel(labels.toString()));
                panel.add(checkBox);
                panel.setBorder(border);
                panel.setPreferredSize(new Dimension(200, 50));
                selectedContributorMap.put(u.getUsername(), checkBox);
                contributorPanelParent.add(panel);
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
        String [] sortingComboBoxList = {"Sort Alphabetically", "Sort by average Rating of Exemplars", "Sort by Number of Exemplars"};
        String [] sortingComboBoxList2 = {"ascending", "descending" };
        sortingComboBox = new JComboBox(sortingComboBoxList);
        sortingComboBox2 = new JComboBox(sortingComboBoxList2);
        JButton filterButton = new JButton("Filter by Label");
        JButton openContributorsButton = new JButton("Open Selected");
        JButton closeLibraryButton = new JButton("Close Library");
        sortingComboBox.addItemListener(sortingListener);
        sortingComboBox2.addItemListener(sortingListener);

        openContributorsButton.addActionListener(x->openContributors());
        closeLibraryButton.addActionListener(x->closeListener.componentSubmitted(this));
        filterButton.addActionListener(e -> filterLabelPopupFrame.setVisible(true));

        buttonPanel.add(sortingComboBox);
        buttonPanel.add(sortingComboBox2);
        buttonPanel.add(filterButton);
        buttonPanel.add(openContributorsButton);
        buttonPanel.add(closeLibraryButton);
        buttonPanel.setBorder(border);
    }

    /**
     * Opens new tabs for the contributors which were requested by the user
     */
    void openContributors(){
        Set<Map.Entry<String, JCheckBox>> entrySet = selectedContributorMap.entrySet();
        List<String> selectedContributors = new ArrayList<>();
        for(Map.Entry<String, JCheckBox> e: entrySet){
            if(e.getValue().isSelected()) {
                selectedContributors.add(e.getKey());
                e.getValue().doClick();
            }
        }
        contributorListener.tabRequested(selectedContributors);
    }

    /**
     * Initializes sorting listener that sorts the contributors according to the combo-boxes
     */
    private void initializeSortingListener() {
        sortingListener = event -> {
            /**
             * Sort alphabetically
             */
            if(sortingComboBox.getSelectedIndex() == 0) {
                allContributors = allContributors.stream().
                        sorted(Comparator.comparing(c -> c.getUsername().toLowerCase())).collect(Collectors.toList());

                filteredContributors = filteredContributors.stream().
                        sorted(Comparator.comparing(c -> c.getUsername().toLowerCase())).collect(Collectors.toList());

                if(sortingComboBox2.getSelectedIndex() == 1) {
                    Collections.reverse(allContributors);
                    Collections.reverse(filteredContributors);
                }
            }
            /**
             * Sort by avg rating of exemplar
             */
            if(sortingComboBox.getSelectedIndex() == 1) {
                allContributors = allContributors.stream().
                            sorted(Comparator.comparingDouble(c -> exemplarMap.get(c)[0])).collect(Collectors.toList());

                filteredContributors = filteredContributors.stream().
                            sorted(Comparator.comparingDouble(c -> exemplarMap.get(c)[0])).collect(Collectors.toList());

                if(sortingComboBox2.getSelectedIndex() == 1) {
                    Collections.reverse(allContributors);
                    Collections.reverse(filteredContributors);
                }
            }
            /**
             * Sort by number of users
             */
            if(sortingComboBox.getSelectedIndex() == 2) {
                    allContributors= allContributors.stream().
                            sorted(Comparator.comparingDouble(c -> exemplarMap.get(c)[1])).collect(Collectors.toList());
                    filteredContributors= filteredContributors.stream().
                            sorted(Comparator.comparingDouble(c -> exemplarMap.get(c)[1])).collect(Collectors.toList());

                if(sortingComboBox2.getSelectedIndex() == 1){
                    Collections.reverse(allContributors);
                    Collections.reverse(filteredContributors);
                }
            }
            updateTab();
        };
    }

    /**
     * Updates the panel by removing all contributors and adding them once more
     */
    public void updateTab (){
        removeAll();
        contributorPanelParent.removeAll();
        addContributorsToScrollPane();
        addComponents();
    }

    /**
     * Initializes the pop up frame for filtering contributors by label
     */
    void initializeFilterLabelFrame(){
        filterLabelPopupFrame = new FilterLabelPopupFrame(allLabels, "Filter Contributors");
        filterLabelPopupFrame.setVisible(false);
        filterLabelPopupFrame.setSize(new Dimension(350, 400));
        filterLabelPopupFrame.setLocationRelativeTo(this);

        filterLabelPopupFrame.setListener(labels -> {
            filteredLabels = labels;
            if(filteredLabels.isEmpty()) filtered = false;
            else filterContributors();
            updateTab();
            filterLabelPopupFrame.setVisible(false);

        });
    }

    /**
     * Filters contributors by label
     */
    public void filterContributors(){
        filteredContributors = allContributors.stream().
                filter(c -> {
                    List <String> allLabelsOfContributor = labelsPerContributor.get(c).stream().
                                    map(l -> l.getValue().toLowerCase()).collect(Collectors.toList());
                    int i = filteredLabels.size();
                    int j = 0;
                    for(String s: filteredLabels){
                        if(allLabelsOfContributor.contains(s.toLowerCase())) j++;
                    }
                    return i==j;
                }).collect(Collectors.toList());
        filtered = true;
    }

    public void setCloseListener(ActionWithComponentListener closeListener) {
        this.closeListener = closeListener;
    }

    public void setContributorListener(NewTabListener contributorListener) {
        this.contributorListener = contributorListener;
    }
}
