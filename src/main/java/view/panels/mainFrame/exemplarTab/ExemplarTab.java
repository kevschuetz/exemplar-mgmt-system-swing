package view.panels.mainFrame.exemplarTab;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.entities.Exemplar;
import model.entities.Label;
import model.entities.Rating;
import model.entities.User;
import model.httpclients.RatingClient;
import view.listeners.mainframe.CloseTabListener;
import view.listeners.mainframe.exemplarTab.AddLabelListener;
import view.listeners.mainframe.exemplarTab.DeleteExemplarListener;
import view.listeners.mainframe.exemplarTab.UpdateExemplarListener;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;


public class ExemplarTab extends JPanel {
    private Exemplar exemplar;
    private int avgRating;
    private Label[] labels;
    private User[] contributors;
    private ObjectMapper mapper;

    JScrollPane scrollPane = new JScrollPane();
    JPanel parentPanel = new JPanel();
    private JPanel metaInfoPanel = new JPanel();
    private JPanel problemPanel= new JPanel();
    private JPanel solutionPanel= new JPanel();
    private JPanel configurationPanel= new JPanel();
    private JPanel commentPanel= new JPanel();
    private JPanel labelPanel;
    private JPanel contributorPanel;

    JTextArea problemTextArea = new JTextArea();
    JTextArea solutionTextArea = new JTextArea();

    private JButton closeButton = new JButton("Close Tab");
    JButton ratingButton = new JButton("Rate Exemplar");
    JButton commentButton = new JButton("Leave Commment");
    JButton addContributorButton = new JButton("Add Contributor");
    JButton updateButton = new JButton ("Update");
    JButton deleteButton = new JButton("Delete");
    JButton addLabelButton = new JButton("Add");

    private CloseTabListener closeListener;
    private UpdateExemplarListener updateExemplarListener;
    private DeleteExemplarListener deleteExemplarListener;
    private AddLabelListener addLabelListener;

    boolean editable = false;


    public ExemplarTab(Exemplar exemplar, boolean editable){
        this.exemplar = exemplar;
        this.editable = editable;

        setLayout();
        setBorder(getBorder(exemplar.getName()));
        initializeComponents();
        setEditable();
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

    void initializeComponents(){
        initializeMetaInfoPanel();

        problemPanel.setLayout(new GridLayout(1,1));
        problemPanel.setBorder(getBorder("Description"));
        problemTextArea.setLineWrap(true);
        problemTextArea.setWrapStyleWord(true);
        problemTextArea.setText(exemplar.getProblem());
        problemTextArea.setSize(new Dimension(100,100));
        JScrollPane problemScrollPane = new JScrollPane(problemTextArea);
        problemPanel.add(problemScrollPane);

        solutionPanel.setLayout(new GridLayout(1,1));
        solutionPanel.setBorder(getBorder("Solution"));
        solutionTextArea.setLineWrap(true);
        solutionTextArea.setWrapStyleWord(true);
        solutionTextArea.setText(exemplar.getSolution());
        solutionTextArea.setSize(new Dimension(100,100));
        JScrollPane solutionScrollPane = new JScrollPane(solutionTextArea);
        solutionPanel.add(solutionScrollPane);

        configurationPanel.setLayout(new GridLayout(1, 6));
        if(editable){
            configurationPanel.add(updateButton);
            configurationPanel.add(addContributorButton);
            configurationPanel.add(deleteButton);
        }

        configurationPanel.add(ratingButton);
        configurationPanel.add(commentButton);
        configurationPanel.add(closeButton);
    }

    private void initializeMetaInfoPanel() {
        metaInfoPanel = new JPanel();
        metaInfoPanel.setSize(new Dimension(100,100));
        metaInfoPanel.setPreferredSize(new Dimension(100,100));
        JLabel nameLabel = new JLabel("Name: "+ exemplar.getName());
        JLabel creatorLabel = new JLabel ("Creator: "+ exemplar.getCreator().getUsername());
        double avgRating = getAvgRating();
        JLabel avgRatingLabel = new JLabel("Rating: " + getAvgRating());
        metaInfoPanel.setBorder(getBorder("Info"));
        metaInfoPanel.setLayout(new GridLayout(2,3));
        metaInfoPanel.add(nameLabel);
        metaInfoPanel.add(creatorLabel);
        metaInfoPanel.add(avgRatingLabel);
        labelPanel = initializeLabelPanel();
        contributorPanel = initializeContributorPanel();
        labelPanel.add(addLabelButton);
        metaInfoPanel.add(labelPanel);
        metaInfoPanel.add(new JPanel());
        metaInfoPanel.add(contributorPanel);
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
        //c.anchor= Anchor.HORIZONTAL;
        parentPanel.add(metaInfoPanel, c);
    }

    private JPanel initializeContributorPanel() {
        JPanel contributorPanel = new JPanel();
        contributorPanel.setLayout(new GridLayout(1,exemplar.getContributors().size()+1));
        JLabel contributors = new JLabel("Contributors:");
        contributorPanel.add(contributors);
        for(User u : exemplar.getContributors()){
            JLabel newLabel = new JLabel(u.getUsername());
            contributorPanel.add(newLabel);
        }
        
        return contributorPanel;
    }

    private JPanel initializeLabelPanel() {
        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new GridLayout(1, exemplar.getLabels().size()+2));
        JLabel labels = new JLabel("Lables:");
        labelPanel.add(labels);
        for(Label l : exemplar.getLabels()){
            labelPanel.add(new JLabel(l.getValue()));
        }
        return labelPanel;
    }

    void setEditable(){
            solutionTextArea.setEditable(editable);
            problemTextArea.setEditable(editable);
    }

    void addComponents(){
        GridBagConstraints c = new GridBagConstraints();
        c.weighty = 0.1;
        c.weightx=1;
        c.gridy = 0;
        c.gridx = 0;
        c.fill= GridBagConstraints.BOTH;
        //c.anchor= Anchor.HORIZONTAL;
        parentPanel.add(metaInfoPanel, c);

        c.weighty = 0.4;
        c.gridy = 1;
        parentPanel.add(problemPanel, c);

        c.gridy= 2;
        c.weighty = 0.4;
        parentPanel.add(solutionPanel, c);

        scrollPane = new JScrollPane(parentPanel);
        c.weighty=0.97;
        c.gridy = 0;
        add(scrollPane,c);

        c.weighty=0.03;
        c.gridy=1;
        add(configurationPanel,c);

    }

    double getAvgRating()  {
        RatingClient client = new RatingClient();
        java.util.List<Rating> ratings = new ArrayList<>();
        boolean ok = false;
        try{
            ratings = client.getAll();
            ok = true;

        }catch(Exception e){
            e.printStackTrace();
        }
        if(ok) {
            double sum = ratings
                    .stream()
                    .filter(x -> x.getKey().getExemplar().equals(exemplar))
                    .map(x -> x.getRating())
                    .reduce(0.0, (a, b) -> a + b);

            java.util.List<Rating> relevantRatings = ratings
                    .stream()
                    .filter(x -> x.getKey().getExemplar().equals(exemplar))
                    .collect(Collectors.toList());

            if(relevantRatings.size()>0)return sum/relevantRatings.size();
            else return 0;
        }
        return 0;
    }

    void addActionListener(){
        closeButton.addActionListener((x)->closeListener.shutdownRequested(this));
        updateButton.addActionListener((x)->{
            exemplar.setSolution(solutionTextArea.getText());
            exemplar.setProblem(problemTextArea.getText());
            updateExemplarListener.updateRequested(exemplar);
        });
        deleteButton.addActionListener((e)->deleteExemplarListener.deleteRequested(exemplar.getName(), this));
        addLabelButton.addActionListener((e)->{
            addLabelListener.buttonClicked(this);
        });
    }

    public void setCloseListener(CloseTabListener closeListener) {
        this.closeListener = closeListener;
    }

    public void setUpdateExemplarListener(UpdateExemplarListener updateExemplarListener) {
        this.updateExemplarListener = updateExemplarListener;
    }

    public void setDeleteExemplarListener(DeleteExemplarListener deleteExemplarListener) {
        this.deleteExemplarListener = deleteExemplarListener;
    }

    public void setAddLabelListener(AddLabelListener addLabelListener) {
        this.addLabelListener = addLabelListener;
    }

    public Exemplar getExemplar() {
        return exemplar;
    }
}