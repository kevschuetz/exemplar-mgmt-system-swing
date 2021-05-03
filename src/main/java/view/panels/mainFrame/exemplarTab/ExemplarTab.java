package view.panels.mainFrame.exemplarTab;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.entities.Exemplar;
import model.entities.Label;
import model.entities.Rating;
import model.entities.User;
import model.httpclients.RatingClient;
import view.listeners.mainframe.CloseTabListener;
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


    JTextArea problemTextArea = new JTextArea();
    JTextArea solutionTextArea = new JTextArea();

    private JButton closeButton = new JButton("Close Tab");
    JButton ratingButton = new JButton("Rate Exemplar");
    JButton commentButton = new JButton("Leave Commment");
    JButton addContributorButton = new JButton("Add Contributor");
    JButton updateButton = new JButton ("Update");


    private CloseTabListener closeListener;
    private UpdateExemplarListener updateExemplarListener;

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
        setLayout(new GridLayout(1,1));
        parentPanel.setLayout(new GridBagLayout());

    }

    Border getBorder (String s){
        return BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(s),
                BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    void initializeComponents(){
        metaInfoPanel = new JPanel();
        JLabel nameLabel = new JLabel("Name: "+ exemplar.getName());
        JLabel creatorLabel = new JLabel ("Creator: "+ exemplar.getCreator().getUsername());
        long avgRating = getAvgRating();
        JLabel avgRatingLabel = new JLabel("Rating: " + getAvgRating());
        metaInfoPanel.setBorder(getBorder("Info"));
        metaInfoPanel.setLayout(new GridLayout(1,3));
        metaInfoPanel.add(nameLabel);
        metaInfoPanel.add(creatorLabel);
        metaInfoPanel.add(avgRatingLabel);

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




        configurationPanel.setLayout(new GridLayout(1, 5));
        if(editable){
            configurationPanel.add(updateButton);
            configurationPanel.add(addContributorButton);
        }

        configurationPanel.add(ratingButton);
        configurationPanel.add(commentButton);
        configurationPanel.add(closeButton);
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


        c.weighty= 0.1;
        c.gridy = 3;
        c.gridx = 0;
        parentPanel.add(configurationPanel, c);





        scrollPane = new JScrollPane(parentPanel);
        add(scrollPane);

    }

    long getAvgRating()  {
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
            long sum = ratings
                    .stream()
                    .filter(x -> x.getKey().getExemplar().equals(exemplar))
                    .map(x -> x.getRating())
                    .count();

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

    }

    public void setCloseListener(CloseTabListener closeListener) {
        this.closeListener = closeListener;
    }

    public void setUpdateExemplarListener(UpdateExemplarListener updateExemplarListener) {
        this.updateExemplarListener = updateExemplarListener;
    }
}
