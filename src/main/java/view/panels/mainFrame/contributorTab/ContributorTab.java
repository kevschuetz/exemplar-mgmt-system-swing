package view.panels.mainFrame.contributorTab;


import controller.MainController;
import model.entities.Exemplar;
import model.entities.Label;
import model.entities.User;
import model.httpclients.ExemplarClient;
import model.httpclients.RatingClient;
import view.listeners.mainframe.ActionWithComponentListener;
import view.listeners.mainframe.homeTab.NewTabListener;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents information about a specific contributor (User Dashboard)
 */
public class ContributorTab extends JPanel {
    private User contributor;
    private List <Label> labels;
    private List<Exemplar> exemplars;

    JScrollPane scrollPane = new JScrollPane();
    JPanel parentPanel = new JPanel();
    private JPanel metaInfoPanel = new JPanel();
    private JPanel configurationPanel= new JPanel();
    private JPanel labelPanel;
    private JPanel buttonPanel;
    private JButton closeButton;
    private JButton openExemplarsButton;
    private ActionWithComponentListener closeListener;
    private ExemplarClient exemplarClient;
    private RatingClient ratingClient;
    private Map<Exemplar, double []> ratingMap = new HashMap<>(); // [0] = average Rating [1] = number of ratings
    private Map<Exemplar, JPanel> exemplarJPanelMap = new HashMap<>();
    private JPanel exemplarPanelParent = new JPanel();
    Border border = BorderFactory.createBevelBorder(0);
    private Map<String, JCheckBox> selectedExemplarMap = new HashMap<>();
    private NewTabListener exemplarListener;

    public ContributorTab(User contributor){
        this.contributor = contributor;
        this.exemplarClient = new ExemplarClient();
        this.ratingClient = new RatingClient();

        this.exemplars = MainController.exemplars.stream().filter(e->(e.getContributors() != null && e.getContributors().contains(contributor)) || (e.getCreator() != null && e.getCreator().equals(contributor))).collect(Collectors.toList());
        exemplarPanelParent.setLayout(new GridLayout(exemplars.size()+1, 1));
        createExemplarPanels();
        addExemplarPanelsToParentPanel();
        this.labels = getLabels();
        setLayout();
        setBorder(getBorder(contributor.getUsername()));
        initializeButtons();
        initializeComponents();
        addComponents();
        addActionListener();
    }
    /**
     * Sets the layout of the panel
     */
    void setLayout(){
        setLayout(new GridBagLayout());
        parentPanel.setLayout(new GridBagLayout());
    }
    /**
     * Creates a border for the panel
     * @param s title of the border
     * @return the border which was created
     */
    Border getBorder (String s){
        return BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(s),
                BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }
    /**
     * Initializes the buttons of the panel
     */
    void initializeButtons(){
        buttonPanel= new JPanel();
        buttonPanel.setLayout(new GridLayout(1,2));
        openExemplarsButton = new JButton("Open Selected");
        openExemplarsButton.addActionListener(x->openExemplars());
        closeButton = new JButton("Close Tab");
        buttonPanel.add(openExemplarsButton);
        buttonPanel.add(closeButton);
    }
    /**
     * Initializes the main components of the panel
     */
    void initializeComponents(){
        initializeMetaInfoPanel();
        configurationPanel.setLayout(new GridLayout(1, 7));
        configurationPanel.add(buttonPanel);
    }
    /**
     * Initializes the meta info panel
     */
    private void initializeMetaInfoPanel() {
        metaInfoPanel = new JPanel();

        JLabel nameLabel = new JLabel("Username: "+ contributor.getUsername());
        nameLabel.setHorizontalAlignment(SwingConstants.LEFT);
        JLabel avgRatingLabel = new JLabel("Average Rating of Exemplars: " + getAvgRating());

        metaInfoPanel.setBorder(getBorder("Info"));
        metaInfoPanel.setLayout(new GridLayout(5,1));

        labelPanel = initializeLabelPanel();

        metaInfoPanel.add(nameLabel);
        metaInfoPanel.add(avgRatingLabel);
        metaInfoPanel.add(labelPanel);
    }
    /**
     * Creates a panel and adds all labels which are connected to the given Contributor's Exemplars
     * @return panel which holds all the labels associated with the given Contributor
     */
    private JPanel initializeLabelPanel() {
        labelPanel = new JPanel();
        labelPanel.setLayout(new GridLayout(1, labels.size()+2));
        JLabel exemplarLabels = new JLabel("Labels of contributed Exemplars:");
        labelPanel.add(exemplarLabels);
        StringBuilder labelsAsString = new StringBuilder("");
        for(Label l : labels){
            labelsAsString.append("  " + l.getValue());
        }
        labelPanel.add(new JLabel(labelsAsString.toString()));

        return labelPanel;
    }
    /**
     * Adds the main components to the panel
     */
    void addComponents(){
        GridBagConstraints c = new GridBagConstraints();
        c.weighty = 0.3;
        c.weightx=1;
        c.gridy = 0;
        c.gridx = 0;
        c.fill= GridBagConstraints.BOTH;
        parentPanel.add(metaInfoPanel, c);

        scrollPane = new JScrollPane(parentPanel);
        c.weighty=0.97;
        c.gridy = 0;
        add(scrollPane,c);

        c.weighty=0.03;
        c.gridy=1;
        add(configurationPanel,c);

        exemplarPanelParent.setBorder(getBorder( contributor.getUsername() + "'s Exemplars"));
        c.gridx = 0;
        parentPanel.add(exemplarPanelParent, c);

    }
    /**
     * Calculates the average rating of the given Contributor's Exemplars
     * @return the average rating of the Exemplars associated with the given Contributor
     */
    double getAvgRating()  {
        ratingClient = new RatingClient();
        return  exemplars
                .stream()
                .mapToDouble(e->{
                    return MainController.ratings
                            .stream()
                            .filter(r->r.getKey().getExemplar().equals(e))
                            .mapToDouble(r->r.getRating())
                            .average().orElse(0);
                }).average().orElse(0);
    }
    /**
     * Creates a list of all labels which are connected to the given Contributor's Exemplars
     * @return list of all the labels associated with the given Contributor's Exemplars
     */
    List <Label> getLabels (){
        return exemplars.stream().flatMap(e -> e.getLabels().stream()).collect(Collectors.toList());
    }
    /**
     * Adds action listener to close button
     */
    void addActionListener(){
        closeButton.addActionListener(x->closeListener.componentSubmitted(this));

    }
    /**
     * Sets close listener
     * @param closeListener listener which should be set
     */
    public void setCloseListener(ActionWithComponentListener closeListener) {
        this.closeListener = closeListener;
    }
    /**
     * Returns Contributor associated with this tab
     * @return the tab's Contributor
     */
    public User getContributor() {
        return contributor;
    }
    /**
     * Creates panels for all Exemplars which are connected to the given Contributor
     */
    public void createExemplarPanels(){
        for(Exemplar e : exemplars){
            ratingMap.put(e,
                    new double[]
                            {
                                    MainController.ratings
                                    .stream()
                                    .filter(r->r.getKey().getExemplar() != null && r.getKey().getExemplar().equals(e))
                                    .mapToDouble(r->r.getRating())
                                    .average().orElse(0)
                                    ,
                                    MainController.ratings.stream().filter(r->r.getKey().getExemplar() != null && r.getKey().getExemplar().equals(e)).collect(Collectors.toList()).size()});
            JPanel panel = new JPanel();
            panel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent event) {
                    if(event.getClickCount()==2 && event.getButton() == MouseEvent.BUTTON1){
                        List<String> exemplar = new ArrayList<>();
                        exemplar.add(e.getName());
                        exemplarListener.tabRequested(exemplar);
                    }
                }
            });
            panel.setLayout(new GridLayout(4,3));
            JLabel exemplarName = new JLabel(e.getName());
            exemplarName.setFont(new Font("Verdana", Font.BOLD, 14));
            JLabel ratingLabel = new JLabel("Rating:");
            JCheckBox checkBox = new JCheckBox();
            panel.add(exemplarName);
            panel.add(new JLabel(""));
            panel.add(new JLabel(""));
            panel.add(ratingLabel);
            String rating = "";
            rating += ratingMap.get(e)[0];
            panel.add(new JLabel(rating));
            panel.add(checkBox);
            panel.add(new JLabel("Number of Ratings: "));
            String numberOfRatings =  "";
            numberOfRatings += ratingMap.get(e)[1];
            panel.add(new JLabel(numberOfRatings));
            panel.add(new JPanel());
            panel.add(new JLabel("Labels: "));
            String labelsString = "";
            for(model.entities.Label l : e.getLabels()){
                labelsString +=l.getValue()+", ";
            }
            panel.add(new JLabel(labelsString));

            panel.setBorder(border);
            panel.setPreferredSize(new Dimension(200, 75));
            selectedExemplarMap.put(e.getName(), checkBox);
            exemplarJPanelMap.put(e, panel);
        }
    }
    /**
     * Adds all of the Exemplar panels to a separate panel
     */
    void addExemplarPanelsToParentPanel(){
        for(Exemplar e : exemplars){
            exemplarPanelParent.add(exemplarJPanelMap.get(e));
        }
    }
    /**
     * Creates a new tab for all the Exemplars which were requested by the current User
     */
    void openExemplars(){
        Set<Map.Entry<String, JCheckBox>> entrySet = selectedExemplarMap.entrySet();
        List<String> selectedExemplars = new ArrayList<>();
        for(Map.Entry<String, JCheckBox> e: entrySet){
            if(e.getValue().isSelected()) {
                selectedExemplars.add(e.getKey());
                e.getValue().doClick();
            }
        }
        exemplarListener.tabRequested(selectedExemplars);
    }
    /**
     * Sets Exemplar listener
     * @param exemplarListener listener which should be set
     */
    public void setExemplarListener(NewTabListener exemplarListener) {
        this.exemplarListener = exemplarListener;
    }

}

