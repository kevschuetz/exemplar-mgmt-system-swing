package view.panels.mainFrame.contributorTab;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.entities.Exemplar;
import model.entities.Label;
import model.entities.User;
import model.httpclients.ExemplarClient;
import model.httpclients.RatingClient;
import view.frames.mainFrame.ConfirmExemplarDeletionFrame;
import view.listeners.mainframe.CloseTabListener;
import view.listeners.mainframe.exemplarTab.*;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ContributorTab extends JPanel {
    private User contributor;
    private double avgRating;
    private List <Label> labels;
    private List<Exemplar> exemplars;
    private ObjectMapper mapper;

    JScrollPane scrollPane = new JScrollPane();
    JPanel parentPanel = new JPanel();
    private JPanel metaInfoPanel = new JPanel();
    private JPanel configurationPanel= new JPanel();
    private JPanel labelPanel;
    private JPanel contributorPanel;

    private JButton closeButton = new JButton("Close Tab");

    private CloseTabListener closeListener;
    private ContributorListener contributorListener;

    private ConfirmExemplarDeletionFrame confirmExemplarDeletionFrame;



    public ContributorTab(User contributor){
        this.contributor = contributor;
        ExemplarClient exemplarClient = new ExemplarClient();
        this.exemplars = exemplarClient.getExemplarForCreator(contributor.getUsername());
        this.avgRating = getAvgRating();
        this.labels = getLabels();
        setLayout();
        setBorder(getBorder(contributor.getUsername()));
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

    void initializeComponents(){
        initializeMetaInfoPanel();



        configurationPanel.setLayout(new GridLayout(1, 7));
        configurationPanel.add(closeButton);
    }

    private void initializeMetaInfoPanel() {
        metaInfoPanel = new JPanel();

        JLabel nameLabel = new JLabel("Username: "+ contributor.getUsername());
        nameLabel.setHorizontalAlignment(SwingConstants.LEFT);

        double avgRating = getAvgRating();
        JLabel avgRatingLabel = new JLabel("Average Rating of Exemplars: " + getAvgRating());

        metaInfoPanel.setBorder(getBorder("Info"));
        metaInfoPanel.setLayout(new GridLayout(5,1));

        labelPanel = initializeLabelPanel();
        //contributorPanel = initializeContributorPanel();


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
        for(Label l : labels){
            JLabel newLabel = new JLabel(l.getValue());
            newLabel.setHorizontalAlignment(SwingConstants.LEFT);
            labelPanel.add(newLabel);
        }
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

    }

    List <Exemplar> getExemplars(){
        List <Exemplar> allExemplars = new ArrayList<>();
        try {
             allExemplars = new ExemplarClient().getAll();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return allExemplars.stream().filter(e -> e.getContributors().contains(contributor)).collect(Collectors.toList());
    }

    double getAvgRating()  {
        RatingClient ratingClient = new RatingClient();
        return exemplars.stream().mapToDouble(e -> ratingClient.getAvgRatingForExemplar(e.getName())).average().orElse(0);
    }

    List <Label> getLabels (){
        return exemplars.stream().flatMap(e -> e.getLabels().stream()).sorted().collect(Collectors.toList());
    }


    void addActionListener(){
        closeButton.addActionListener((x)->closeListener.shutdownRequested(this));

    }

    public void setCloseListener(CloseTabListener closeListener) {
        this.closeListener = closeListener;
    }

    public User getContributor() {
        return contributor;
    }

}

