package view.panels.mainFrame;

import model.entities.User;
import model.httpclients.ExemplarClient;
import model.httpclients.RatingClient;
import model.httpclients.UserClient;
import view.frames.mainFrame.NewLabelPopupFrame;
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

public class ContributorLibraryTab extends JPanel {
    JPanel contributorPanelParent = new JPanel();
    private List<User> allContributors;
    private JScrollPane scrollPane;
    Border border = BorderFactory.createBevelBorder(0);
    private NewTabListener contributorListener;
    private Map<String, JCheckBox> selectedContributorMap = new HashMap<>();
    JPanel buttonPanel;
    private ActionWithComponentListener closeListener;
    private Map <User, double []> exemplarMap = new HashMap(); // [0] = average Rating [1] = number of Exemplars
    private Map <User, List<model.entities.Label>> labelsPerContributor = new HashMap();
    private ExemplarClient exemplarClient = new ExemplarClient();
    private RatingClient ratingClient = new RatingClient();

    private JComboBox sortingComboBox;
    private JComboBox sortingComboBox2;
    private ItemListener sortingListener;

    FilterLabelPopupFrame filterLabelPopupFrame;
    FilterByLabelListener filterListener;
    private List <String> filteredLabels = new ArrayList<>();

    public ContributorLibraryTab(String searchTerm){
        scrollPane = new JScrollPane(contributorPanelParent);
        scrollPane.setLayout(new ScrollPaneLayout());

        fetchContributors(searchTerm);

        contributorPanelParent.setLayout(new GridLayout(allContributors.size()+1, 1));
        addContributorsToScrollPane();
        initializeSortingListener();
        initializeNewLabelPopupFrame();
        initializeButtonPanel();
        addComponents();
    }

    public void fetchContributors(String searchTerm){
        allContributors = new UserClient().searchUsers(searchTerm);
        allContributors = allContributors
                .stream()
                .filter(u->u.getIsContributor()==1)
                .collect(Collectors.toList());
        addExemplarInformation();
    }

    public void addExemplarInformation(){
        for(User u : allContributors){
            exemplarMap.put(u, new double[]{
                    exemplarClient.getExemplarsForUser(u.getUsername()).stream().
                            mapToDouble(e -> ratingClient.getAvgRatingForExemplar(e.getName())).
                            average().orElse(0),
                    exemplarClient.getExemplarsForUser(u.getUsername()).size()});
        }
    }

    public void addContributorsToScrollPane(){
        int i = 0;
        for(User u : allContributors){
            if(u.getIsContributor() ==1) {
                JPanel panel = new JPanel();
                panel.setLayout(new GridLayout(5, 3));

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
                labelsPerContributor.put(u, exemplarClient.getExemplarsForUser(u.getUsername()).stream().
                        flatMap(e -> e.getLabels().stream()).collect(Collectors.toList()));
                JLabel name = new JLabel("Name: ");
                JLabel userName = new JLabel(u.getUsername());
                JLabel labelNumberOfExemplars = new JLabel("Number of Exemplars: ");
                JLabel numberOfExemplars = new JLabel(String.valueOf((int)exemplarMap.get(u)[1]));
                JLabel labelAverageRatingOfExemplars = new JLabel("Average Rating: ");
                JLabel averageRatingOfExemplars = new JLabel(String.valueOf(Math.round(exemplarMap.get(u)[0] * 100.00) / 100.00));
                JLabel labelExemplarLabels = new JLabel("Labels of Exemplars: ");

                JCheckBox checkBox = new JCheckBox();
               // if (i % 2 == 0) checkBox.setBackground(Color.LIGHT_GRAY);
                panel.add(name);
                panel.add(userName);
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
                //if (i % 2 == 0) panel.setBackground(Color.LIGHT_GRAY);
                selectedContributorMap.put(u.getUsername(), checkBox);
                contributorPanelParent.add(panel);
                i++;
            }
        }

    }

    void addComponents(){
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

    }

    void initializeButtonPanel(){
        buttonPanel= new JPanel();
        buttonPanel.setLayout(new GridLayout(1,3));
        String [] sortingComboBoxList = {"Sort by average Rating of Exemplars", "Sort by Number of Exemplars"};
        String [] sortingComboBoxList2 = {"descending", "ascending"};
        sortingComboBox = new JComboBox(sortingComboBoxList);
        sortingComboBox2 = new JComboBox(sortingComboBoxList2);
        JButton filterButton = new JButton("Filter by Label");
        JButton openContributorsButton = new JButton("Open Selected");
        JButton closeLibraryButton = new JButton("Close Library");
        sortingComboBox.addItemListener(sortingListener);
        sortingComboBox2.addItemListener(sortingListener);

        openContributorsButton.addActionListener((x)->openContributors());
        closeLibraryButton.addActionListener((x)->closeListener.componentSubmitted(this));
        filterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterLabelPopupFrame.setVisible(true);
            }
        });

        buttonPanel.add(sortingComboBox);
        buttonPanel.add(sortingComboBox2);
        buttonPanel.add(filterButton);
        buttonPanel.add(openContributorsButton);
        buttonPanel.add(closeLibraryButton);
        buttonPanel.setBorder(border);
    }

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

    private void initializeSortingListener() {
        sortingListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent event) {
                // do something
                if(sortingComboBox.getSelectedIndex() == 0) {
                    if(sortingComboBox2.getSelectedIndex() == 0) {
                        allContributors = allContributors.stream().
                                sorted(Comparator.comparingDouble(c -> exemplarMap.get(c)[0])).collect(Collectors.toList());
                        Collections.reverse(allContributors);
                    }else
                        allContributors = allContributors.stream().
                                sorted(Comparator.comparingDouble(c -> exemplarMap.get(c)[0])).collect(Collectors.toList());
                }
                if(sortingComboBox.getSelectedIndex() == 1) {
                    if(sortingComboBox2.getSelectedIndex() == 0) {
                        allContributors= allContributors.stream().
                                sorted(Comparator.comparingDouble(c -> exemplarMap.get(c)[1])).collect(Collectors.toList());
                        Collections.reverse(allContributors);
                    }else
                        allContributors= allContributors.stream().
                                sorted(Comparator.comparingDouble(c -> exemplarMap.get(c)[1])).collect(Collectors.toList());
                }
                updateTab();
            }
        };
    }

    public void updateTab (){
        removeAll();
        contributorPanelParent.removeAll();
        addContributorsToScrollPane();
        addComponents();
    }

    void initializeNewLabelPopupFrame(){
        filterLabelPopupFrame = new FilterLabelPopupFrame();
        filterLabelPopupFrame.setVisible(false);
        filterLabelPopupFrame.setSize(new Dimension(350, 200));
        filterLabelPopupFrame.setLocationRelativeTo(this);

        filterLabelPopupFrame.setListener((i) -> {
            filteredLabels.add(filterLabelPopupFrame.getLabel());
            filter();
            filterLabelPopupFrame.setVisible(false);

        });
    }

    public void filter (){
        allContributors = allContributors.stream().
                filter(c -> {
                    List <String> allLabels = labelsPerContributor.get(c).stream().
                                    map(l -> l.getValue().toLowerCase()).collect(Collectors.toList());
                    for(String s: filteredLabels){
                        if(allLabels.contains(s.toLowerCase())) return true;
                    }
                    return false;
                }).collect(Collectors.toList());
        addExemplarInformation();
        updateTab();


    }



    public void setCloseListener(ActionWithComponentListener closeListener) {
        this.closeListener = closeListener;
    }

    public void setContributorListener(NewTabListener contributorListener) {
        this.contributorListener = contributorListener;
    }
}
