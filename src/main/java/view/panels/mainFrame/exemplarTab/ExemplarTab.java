package view.panels.mainFrame.exemplarTab;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.entities.Comment;
import model.entities.Exemplar;
import model.entities.Label;
import model.entities.User;
import model.httpclients.CommentClient;
import model.httpclients.RatingClient;
import view.frames.mainFrame.AddCommentPopupFrame;
import view.frames.mainFrame.ConfirmExemplarDeletionFrame;
import view.frames.mainFrame.FilterLabelPopupFrame;
import view.frames.mainFrame.NewExemplarPopupFrame;
import view.listeners.mainframe.ActionWithComponentListener;
import view.listeners.mainframe.exemplarTab.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    JButton addLabelButton = new JButton("Add Label");
    JButton exportButton = new JButton("Export");

    private ActionWithComponentListener closeListener;
    private UpdateExemplarListener updateExemplarListener;
    private DeleteExemplarListener deleteExemplarListener;
    private AddLabelListener addLabelListener;
    private RatingListener ratingListener;
    private ContributorListener contributorListener;
    private ActionWithComponentListener exportListener;

    private ConfirmExemplarDeletionFrame confirmExemplarDeletionFrame;

    boolean editable = false;
    private User currentUser;

    private AddCommentPopupFrame commentPopup;
    //private List<String[]> comments = new ArrayList<>(); //[0] = username [1] = comment
    private List<Comment> comments;
    private CommentClient commentClient = new CommentClient();

    public ExemplarTab(Exemplar exemplar, boolean editable, User currentUser){
        this.exemplar = exemplar;
        this.editable = editable;
        this.currentUser = currentUser;

        fetchComments();

        setLayout();
        setBorder(getBorder(exemplar.getName()));
        initializeComponents();
        setEditable();
        addComponents();
        addActionListener();
        initializeDeletalFrame();
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
        initializeAddCommentLabelFrame();

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

        commentPanel.setLayout(new GridLayout(5, 1));
        commentPanel.setBorder(getBorder("Comments"));
        addNewComment("Hello World");
        addCommentsToPanel();

        configurationPanel.setLayout(new GridLayout(1, 8));
        if(editable){
            configurationPanel.add(updateButton);
            configurationPanel.add(addContributorButton);
            configurationPanel.add(deleteButton);
            configurationPanel.add(exportButton);
        }
        configurationPanel.add(addLabelButton);
        configurationPanel.add(ratingButton);
        configurationPanel.add(commentButton);
        configurationPanel.add(closeButton);
    }

    private void initializeMetaInfoPanel() {
        metaInfoPanel = new JPanel();

        JLabel nameLabel = new JLabel("Name: "+ exemplar.getName());
        nameLabel.setHorizontalAlignment(SwingConstants.LEFT);
        JLabel creatorLabel = new JLabel("");
        if(exemplar.getCreator() != null)  creatorLabel = new JLabel ("Creator: "+ exemplar.getCreator().getUsername());
        creatorLabel.setHorizontalAlignment(SwingConstants.LEFT);

        double avgRating = getAvgRating();
        JLabel avgRatingLabel = new JLabel("Rating: " + getAvgRating());

        metaInfoPanel.setBorder(getBorder("Info"));
        metaInfoPanel.setLayout(new GridLayout(5,1));


        labelPanel = initializeLabelPanel();
        contributorPanel = initializeContributorPanel();


        metaInfoPanel.add(nameLabel);
        metaInfoPanel.add(creatorLabel);
        metaInfoPanel.add(avgRatingLabel);
        metaInfoPanel.add(labelPanel);
        metaInfoPanel.add(contributorPanel);
    }

    public void refreshInfoPanel(){
        setVisible(false);
        parentPanel.remove(metaInfoPanel);
        initializeMetaInfoPanel();
        GridBagConstraints c = new GridBagConstraints();
        c.weighty = 0.1;
        c.weightx=1;
        c.gridy = 0;
        c.gridx = 0;
        c.fill= GridBagConstraints.BOTH;
        parentPanel.add(metaInfoPanel, c);
        setVisible(true);

    }

    private JPanel initializeContributorPanel() {
        JPanel contributorPanel = new JPanel();
        contributorPanel.setLayout(new GridLayout(1,exemplar.getContributors().size()+2));
        JLabel contributors = new JLabel("Contributors:");
        contributorPanel.add(contributors);
        for(User u : exemplar.getContributors()){
            JLabel newLabel = new JLabel(u.getUsername(), JLabel.LEFT);
            contributorPanel.add(newLabel);
        }
        return contributorPanel;
    }

    private JPanel initializeLabelPanel() {
        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new GridLayout(1, exemplar.getLabels().size()+2));
        JLabel labels = new JLabel("Labels:");
        labelPanel.add(labels);
        for(Label l : exemplar.getLabels()){
            JLabel newLabel = new JLabel(l.getValue(), JLabel.LEFT);
            newLabel.setHorizontalAlignment(SwingConstants.LEFT);
            labelPanel.add(newLabel);
        }
        return labelPanel;
    }

    void setEditable(){
            solutionTextArea.setEditable(editable);
            problemTextArea.setEditable(editable);
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

        c.weighty = 0.4;
        c.gridy = 1;
        parentPanel.add(problemPanel, c);

        c.gridy= 2;
        c.weighty = 0.4;
        parentPanel.add(solutionPanel, c);

        c.gridy= 3;
        c.weighty = 0.4;
        parentPanel.add(commentPanel, c);

        scrollPane = new JScrollPane(parentPanel);
        c.weighty=0.97;
        c.gridy = 0;
        add(scrollPane,c);

        c.weighty=0.03;
        c.gridy=1;
        add(configurationPanel,c);

    }

    double getAvgRating()  {
       return new RatingClient().getAvgRatingForExemplar(exemplar.getName());
    }

    void addActionListener(){
        closeButton.addActionListener((x)->closeListener.componentSubmitted(this));
        updateButton.addActionListener((x)->{
            exemplar.setSolution(solutionTextArea.getText());
            exemplar.setProblem(problemTextArea.getText());
            updateExemplarListener.updateRequested(exemplar);
        });
        deleteButton.addActionListener((e)->{
            confirmExemplarDeletionFrame.setVisible(true);
        });
        addLabelButton.addActionListener((e)->{
            addLabelListener.buttonClicked(this);
        });
        ratingButton.addActionListener((x)-> ratingListener.ratingRequested(this));
        addContributorButton.addActionListener((x)-> contributorListener.frameRequested(this));
        exportButton.addActionListener((e)-> exportListener.componentSubmitted(this));
        commentButton.addActionListener((x) -> commentPopup.setVisible(true));
    }

    void initializeDeletalFrame(){
        confirmExemplarDeletionFrame = new ConfirmExemplarDeletionFrame(exemplar.getName());
        confirmExemplarDeletionFrame.setVisible(false);
        confirmExemplarDeletionFrame.setSize(new Dimension(400
                ,300));
        confirmExemplarDeletionFrame.setLocationRelativeTo(this);
        confirmExemplarDeletionFrame.setConfirmListener((x)->{
            confirmExemplarDeletionFrame.setVisible(false);
            deleteExemplarListener.deleteRequested(exemplar.getName(), this);
        });
    }

    void initializeAddCommentLabelFrame(){
        commentPopup = new AddCommentPopupFrame();
        commentPopup.setVisible(false);
        commentPopup.setSize(new Dimension(350, 400));
        commentPopup.setLocationRelativeTo(this);

        commentPopup.setListener((labels) -> {
            addNewComment(commentPopup.getComment());
            addCommentsToPanel();
            updateExemplarListener.updateRequested(exemplar);
            commentPopup.clean();
            commentPopup.setVisible(false);
        });
    }

    void fetchComments (){
        comments = commentClient.findCommentsForExemplar(exemplar.getName());
    }

    //muss noch überarbeitet werden!!!!!!(Datenbank)
    void addNewComment(String comment){
        Comment c = new Comment();
        c.setCreator(currentUser);
        c.setValue(comment);
        try {
            commentClient.add(c);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //muss noch überarbeitet werden!!!!!!(Datenbank)
    void addCommentsToPanel(){
        commentPanel.removeAll();
        for(Comment c : comments){
            JLabel comment = new JLabel(c.getValue());
            LineBorder line = new LineBorder(Color.blue, 4, true);
            comment.setBorder(line);
            comment.setBorder(getBorder(c.getCreator().getUsername()));
            commentPanel.add(comment);
        }
    }



    public void setCloseListener(ActionWithComponentListener closeListener) {
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

    public void setRatingListener(RatingListener ratingListener) {
        this.ratingListener = ratingListener;
    }

    public void setContributorListener(ContributorListener contributorListener) {
        this.contributorListener = contributorListener;
    }

    public Exemplar getExemplar() {
        return exemplar;
    }

    public JButton getUpdateButton() {
        return updateButton;
    }

    public void setUpdateButton(JButton updateButton) {
        this.updateButton = updateButton;
    }

    public void setExportListener(ActionWithComponentListener exportListener) {
        this.exportListener = exportListener;
    }

    public boolean isEditable(){
        return this.editable;
    }
}
