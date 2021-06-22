package view.panels.mainFrame.exemplarTab;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.entities.*;
import model.entities.Label;
import model.httpclients.CommentClient;
import model.httpclients.CommunityClient;
import model.httpclients.RatingClient;
import view.frames.mainFrame.AddCommentPopupFrame;
import view.frames.mainFrame.ConfirmExemplarDeletionFrame;
import view.listeners.ActionWithStringListener;
import view.listeners.mainframe.ActionWithComponentListener;
import view.listeners.mainframe.exemplarTab.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


public class ExemplarTab extends JPanel {
    private Exemplar exemplar;
    private int avgRating;
    private Label[] labels;
    private User[] contributors;
    private ObjectMapper mapper;

    JScrollPane scrollPane = new JScrollPane();
    JScrollPane commentScrollPane;
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
    JButton addToCommunityButton = new JButton("Add to Community");

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
    private AddToCommunityFrame addToCommunityFrame;

    private List<Comment> comments;
    private CommentClient commentClient = new CommentClient();

    public ExemplarTab(Exemplar exemplar, boolean editable, User currentUser){
        this.exemplar = exemplar;
        this.editable = editable;
        this.currentUser = currentUser;


        setLayout();
        setBorder(getBorder(exemplar.getName()));
        fetchComments();
        initializeComponents();
        setEditable();
        addCommentsToPanel();
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
        initializeAddCommentPopupFrame();
        initializeAddToCommunityFrame();

        problemPanel.setLayout(new GridLayout(1,1));
        problemPanel.setBorder(getBorder("Description"));
        problemTextArea.setLineWrap(true);
        problemTextArea.setWrapStyleWord(true);
        problemTextArea.setText(exemplar.getProblem());
        problemTextArea.setSize(new Dimension(100,100));
        JScrollPane problemScrollPane = new JScrollPane(problemTextArea);
        problemPanel.add(problemScrollPane);
        problemPanel.setPreferredSize(new Dimension(100, 250));

        solutionPanel.setLayout(new GridLayout(1,1));
        solutionPanel.setBorder(getBorder("Solution"));
        solutionTextArea.setLineWrap(true);
        solutionTextArea.setWrapStyleWord(true);
        solutionTextArea.setText(exemplar.getSolution());
        solutionTextArea.setSize(new Dimension(100,100));
        JScrollPane solutionScrollPane = new JScrollPane(solutionTextArea);
        solutionPanel.add(solutionScrollPane);
        solutionPanel.setPreferredSize(new Dimension(100,250));

        commentPanel.setLayout(new GridLayout(comments.size(), 1));
        commentPanel.setBorder(getBorder("Comments"));
        commentScrollPane=new JScrollPane(commentPanel);

        configurationPanel.setLayout(new GridLayout(1, 9));
        if(editable){
            configurationPanel.add(updateButton);
            configurationPanel.add(addContributorButton);
            configurationPanel.add(deleteButton);
            configurationPanel.add(exportButton);
        }

        configurationPanel.add(addLabelButton);
        configurationPanel.add(ratingButton);
        configurationPanel.add(commentButton);
        configurationPanel.add(addToCommunityButton);
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
        c.weighty = 0.01;
        parentPanel.add(commentScrollPane, c);

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
        addToCommunityButton.addActionListener((e)->addToCommunityFrame.setVisible(true));

    }

    public void setAddToCommunityListener(ActionWithStringListener listener){
        addToCommunityFrame.setAddListener(listener);
    }

    public void initializeDeletalFrame(){
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

    void initializeAddCommentPopupFrame(){
        commentPopup = new AddCommentPopupFrame();
        commentPopup.setVisible(false);
        commentPopup.setSize(new Dimension(350, 400));
        commentPopup.setLocationRelativeTo(this);
        setDefaultListenerForCommentPopupFrame();

    }

    void initializeAddToCommunityFrame(){
        addToCommunityFrame = new AddToCommunityFrame(currentUser);
        addToCommunityFrame.setSize(new Dimension(300,300));
        addToCommunityFrame.setVisible(false);
    }

    void setDefaultListenerForCommentPopupFrame(){
        commentPopup.setListener((comment) -> {
            addNewComment(commentPopup.getComment());
            addCommentsToPanel();
            commentPopup.clean();
            commentPopup.setVisible(false);
        });
    }

    void fetchComments (){
        comments = commentClient.findCommentsForExemplar(exemplar.getName());
    }

    void addNewComment(String comment){

        Comment c = new Comment();
        c.setCreator(currentUser);
        c.setValue(comment);
        c.setExemplar(exemplar);
        try {
            commentClient.add(c);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        comments.add(c);
        addCommentsToPanel();
    }


    void addCommentsToPanel(){
        commentPanel.removeAll();
        commentPanel.setVisible(false);
        commentPanel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = getDefaultGridbagConstraints();
        for(Comment c : comments){
            JPanel panel = new JPanel();
            panel.setLayout(new GridBagLayout());
            GridBagConstraints singleCommentConstraint = getDefaultGridbagConstraints();

            JLabel comment = new JLabel(c.getValue());
            LineBorder line = new LineBorder(Color.blue, 4, true);
            comment.setBorder(line);
            comment.setBorder(getBorder(c.getCreator().getUsername()));
            //Icon replyIcon = new ImageIcon(getClass().getResource("/images/reply.png"));
            JButton replyButton = new JButton("Reply");
            addReplyListenerToButton(replyButton, c);

            panel.add(comment,singleCommentConstraint);
            singleCommentConstraint.gridx=1;
            singleCommentConstraint.weightx=0.02;
            panel.add(replyButton,singleCommentConstraint);


            commentPanel.add(panel, constraints);

            for(Comment reply : c.getAnswers()){
                constraints.gridy++;

                JPanel replyPanel = new JPanel();

                replyPanel.setLayout(new GridBagLayout());
                GridBagConstraints replyConstraint = getDefaultGridbagConstraints();
                replyConstraint.weightx = 0.03;
                replyPanel.add(new JLabel(), replyConstraint);
                replyConstraint.weightx = 1;
                replyConstraint.gridx=1;

                JLabel replyLabel = new JLabel(reply.getValue());
                LineBorder l = new LineBorder(Color.blue, 4, true);
                replyLabel.setBorder(l);
                replyLabel.setBorder(getBorder(reply.getCreator().getUsername()));
                replyPanel.add(replyLabel, replyConstraint);

                commentPanel.add(replyPanel, constraints);
            }

            constraints.weightx=1;
            constraints.gridx=0;
            constraints.gridy++;
        }
        commentPanel.setVisible(true);
    }

    GridBagConstraints getDefaultGridbagConstraints(){
        GridBagConstraints c = new GridBagConstraints();
        c.fill=GridBagConstraints.BOTH;
        c.gridy =0;
        c.gridx=0;
        c.weightx=1;
        c.weighty=1;
        return c;
    }

    private void addReplyListenerToButton(JButton replyButton, Comment c) {
        replyButton.addActionListener((e)->{
            commentPopup.setListener((comment)->{
                Comment reply = new Comment();
                reply.setExemplar(c.getExemplar());
                reply.setCreator(currentUser);
                reply.setValue(comment);
                c.getAnswers().add(reply);
                commentClient.update(Long.toString(c.getId()), c);
                commentPopup.clean();
                commentPopup.setVisible(false);
                setDefaultListenerForCommentPopupFrame();
                addCommentsToPanel();
            });
            commentPopup.setVisible(true);
        });
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

class AddToCommunityFrame extends JFrame{
    private JPanel panel = new JPanel();
    private JList communityList;
    private List<String> communities;
    private JButton addButton = new JButton("Add");
    private String selectedCommunity;
    private ActionWithStringListener addListener;
    private CommunityClient communityClient = new CommunityClient();
    private User user;

    public AddToCommunityFrame(User user){
        this.user = user;

        fetchCommunities();
        addButton.addActionListener((e)-> addListener.stringSubmitted((String)communityList.getSelectedValue()));
        communityList=new JList(communities.toArray());

        panel.setLayout(new GridLayout(1,1));
        JScrollPane scrollPane = new JScrollPane(communityList);
        panel.add(scrollPane);

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridy = 0;
        c.gridx = 0;
        c.weightx = 1;
        c.weighty = 0.9;
        add(panel,c);
        c.gridy=1;
        c.weighty=0.1;
        add(addButton,c);

    }

    void fetchCommunities(){
        communities = communityClient.getCommunitiesForUser(this.user.getUsername())
                .stream()
                .map(c -> c.getName())
                .collect(Collectors.toList());


    }

    public void setAddListener(ActionWithStringListener addListener) {
        this.addListener = addListener;
    }
}
