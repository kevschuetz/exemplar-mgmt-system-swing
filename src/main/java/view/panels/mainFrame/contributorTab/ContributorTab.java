package view.panels.mainFrame.contributorTab;


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

        //Exemplars
        this.exemplars = exemplarClient.getExemplarsForUser(contributor.getUsername());
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

    void setLayout(){
        setLayout(new GridBagLayout());
        parentPanel.setLayout(new GridBagLayout());
    }

    Border getBorder (String s){
        return BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(s),
                BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    void initializeButtons(){
        buttonPanel= new JPanel();
        buttonPanel.setLayout(new GridLayout(1,2));
        openExemplarsButton = new JButton("Open Selected");
        openExemplarsButton.addActionListener(x->openExemplars());
        closeButton = new JButton("Close Tab");
        buttonPanel.add(openExemplarsButton);
        buttonPanel.add(closeButton);
    }

    void initializeComponents(){
        initializeMetaInfoPanel();
        configurationPanel.setLayout(new GridLayout(1, 7));
        configurationPanel.add(buttonPanel);
    }

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

    public void refreshInfoPanel(){
        parentPanel.remove(metaInfoPanel);
        initializeMetaInfoPanel();
        GridBagConstraints c = new GridBagConstraints();
        c.weighty = 0.1;
        c.weightx=1;
        c.gridy = 0;
        c.gridx = 0;
        c.fill= GridBagConstraints.BOTH;
        parentPanel.add(metaInfoPanel, c);

    }


    private JPanel initializeLabelPanel() {
        JPanel labelPanel = new JPanel();
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


    void addComponents(){
        GridBagConstraints c = new GridBagConstraints();
        c.weighty = 0.3;
        c.weightx=1;
        c.gridy = 0;
        c.gridx = 0;
        c.fill= GridBagConstraints.BOTH;
        //c.anchor= Anchor.HORIZONTAL;
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

    double getAvgRating()  {
        ratingClient = new RatingClient();
        return exemplars.stream().mapToDouble(e -> ratingClient.getAvgRatingForExemplar(e.getName())).average().orElse(0);
    }

    List <Label> getLabels (){
        return exemplars.stream().flatMap(e -> e.getLabels().stream()).collect(Collectors.toList());
    }


    void addActionListener(){
        closeButton.addActionListener(x->closeListener.componentSubmitted(this));

    }

    public void setCloseListener(ActionWithComponentListener closeListener) {
        this.closeListener = closeListener;
    }

    public User getContributor() {
        return contributor;
    }

    // Exemplars

    public void createExemplarPanels(){

        for(Exemplar e : exemplars){
            ratingMap.put(e, new double[]{ratingClient.getAvgRatingForExemplar(e.getName()),
                    ratingClient.getRatingsForExemplar(e.getName()).size()});
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

    void addExemplarPanelsToParentPanel(){
        for(Exemplar e : exemplars){
            exemplarPanelParent.add(exemplarJPanelMap.get(e));
        }
    }

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

    public void setExemplarListener(NewTabListener exemplarListener) {
        this.exemplarListener = exemplarListener;
    }


}

