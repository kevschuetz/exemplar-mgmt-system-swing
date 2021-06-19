package view.panels.mainFrame;

import model.entities.*;
import model.entities.Label;
import model.httpclients.CommunityClient;
import model.httpclients.ExemplarClient;
import model.httpclients.RatingClient;
import view.frames.mainFrame.AddCommentPopupFrame;
import view.frames.mainFrame.AddUserrFrame;
import view.frames.mainFrame.ConfirmCommunityDeletionFrame;
import view.frames.mainFrame.ConfirmExemplarDeletionFrame;
import view.listeners.mainframe.ActionWithComponentListener;
import view.listeners.mainframe.communityTap.AddUserListener;
import view.listeners.mainframe.communityTap.DeleteCommunityListener;
import view.listeners.mainframe.communityTap.UpdateCommunityListener;
import view.listeners.mainframe.exemplarTab.AddLabelListener;
import view.listeners.mainframe.exemplarTab.DeleteExemplarListener;
import view.listeners.mainframe.exemplarTab.UpdateExemplarListener;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class CommunityTab extends JPanel {
    private Community community;
    private User currentUser;
    private User[] members;

    private ActionWithComponentListener closeListener;
    private List <Exemplar> referenceExemplars;

    private JPanel parentPanel = new JPanel();
    private JPanel metaInfoPanel = new JPanel();
    private JPanel configurationPanel= new JPanel();
    JScrollPane scrollPane = new JScrollPane();

    private JPanel buttons;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton addUserButton;
    private JButton closeButton;

    private JPanel exemplarParentPanel;
    private JPanel membersParentPanel;

    private AddUserListener addUserListener;

    private AddUserrFrame userPopup;
    private List<User> members;
    private JLabel label;
    private UpdateCommunityListener updateCommunityListener;
    private DeleteCommunityListener deleteCommunityListener;
    private ConfirmCommunityDeletionFrame confirmCommunityDeletionFrame;

    boolean editable = false;

    private ExemplarClient exemplarClient;
    private CommunityClient communityClient;
    private RatingClient ratingClient;
    private Map<Exemplar, JPanel> exemplarJPanelMap = new HashMap<>();
    private Map<User, JPanel> membersJPanelMap = new HashMap<>();

    public CommunityTab(Community community, User currentUser){
        this.community=community;
        this.currentUser = currentUser;
        this.exemplarClient = new ExemplarClient();
        this.communityClient = new CommunityClient();
        this.ratingClient = new RatingClient();
        this.label = new JLabel("community details");
        this.buttons = new JPanel();
        this.exemplarParentPanel = new JPanel();
        this.membersParentPanel = new JPanel();
        this.members = community.getMembers();
        this.referenceExemplars = community.getExemplars();
        fetchExemplars();
        addExemplarPanelsToParentPanel();
        createExemplarPanels();
        createMemberPanels();
        addMemberPanelsToParentPanel();

        setEditable();
        setLayout();
        setBorder(getBorder(community.getName()));
        initializeButtons();
        initializeComponents();
        addComponents();

        addActionListener();
        initializeDeleteFrame();
    }

    void setEditable(){
        editable = true;
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

        membersParentPanel.setBorder(getBorder( "Members:"));
        c.gridx = 0;
        parentPanel.add(membersParentPanel, c);

        exemplarParentPanel.setBorder(getBorder( "Exemplars:"));
        c.gridx = 0;
        parentPanel.add(exemplarParentPanel, c);

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

    void addActionListener(){
        closeButton.addActionListener((x)->closeListener.componentSubmitted(this));
        updateButton.addActionListener((x)->{
            try {
                updateCommunityListener.updateRequested(community);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        deleteButton.addActionListener((e)->{
            confirmCommunityDeletionFrame.setVisible(true);
        });

        addUserButton.addActionListener((e)-> addUserListener.buttonClicked(this));
    }

    void initializeDeleteFrame(){
        confirmCommunityDeletionFrame = new ConfirmCommunityDeletionFrame(community.getName());
        confirmCommunityDeletionFrame.setVisible(false);
        confirmCommunityDeletionFrame.setSize(new Dimension(400
                ,300));
        confirmCommunityDeletionFrame.setLocationRelativeTo(this);
        confirmCommunityDeletionFrame.setConfirmListener((x)->{
            confirmCommunityDeletionFrame.setVisible(false);
            deleteCommunityListener.deleteRequested(community.getName(), this);
        });
    }

    void initializeComponents(){
        initializeMetaInfoPanel();
        configurationPanel.setLayout(new GridLayout(3, 8));
        configurationPanel.add(buttons);
    }

    void initializeButtons(){
        updateButton = new JButton ("Update");
        deleteButton = new JButton("Delete");
        addUserButton = new JButton("add User");
        closeButton = new JButton("Close");
        if(editable){
            buttons.add(updateButton);
            if(community.getCreator().equals(currentUser))buttons.add(deleteButton);
            buttons.add(addUserButton);
        }
        buttons.add(closeButton);
    }

    private void initializeMetaInfoPanel() {
        metaInfoPanel = new JPanel();

        JLabel nameLabel = new JLabel("Name: "+ community.getName());
        nameLabel.setHorizontalAlignment(SwingConstants.LEFT);
        JLabel creatorLabel = new JLabel("");
        if(community.getCreator() != null)  creatorLabel = new JLabel ("Creator: " + community.getCreator().getUsername());
        creatorLabel.setHorizontalAlignment(SwingConstants.LEFT);

        metaInfoPanel.setBorder(getBorder("Info"));
        metaInfoPanel.setLayout(new GridLayout(5,1));


        metaInfoPanel.add(nameLabel);
        metaInfoPanel.add(creatorLabel);
    }

    public void fetchExemplars (){
        referenceExemplars = new ArrayList <Exemplar> ();
        Set<Exemplar> allExemplars = new HashSet<>();

        for(User m : members){
            allExemplars.addAll(exemplarClient.getExemplarsForUser(m.getUsername()));
        }
        referenceExemplars.addAll(allExemplars);
    }

    public void createExemplarPanels(){
        for(Exemplar e : referenceExemplars){
            JPanel panel = new JPanel();
            panel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent event) {
                    if(event.getClickCount()==2 && event.getButton() == MouseEvent.BUTTON1){
                        List<String> exemplar = new ArrayList<>();
                        exemplar.add(e.getName());
                        //exemplarListener.tabRequested(exemplar);
                    }
                }
            });
            panel.setLayout(new GridLayout(4,3));

            JLabel name = new JLabel("Name: ");
            JLabel exemplarName = new JLabel(e.getName());
            exemplarName.setFont(new Font("Verdana", Font.BOLD, 14));
            JLabel ratingLabel = new JLabel("Rating:");
            JCheckBox checkBox = new JCheckBox();
            panel.add(exemplarName);
            panel.add(new JLabel(""));
            panel.add(new JLabel(""));
            panel.add(ratingLabel);
            String rating = "";
            rating += ratingClient.getAvgRatingForExemplar(e.getName());
            panel.add(new JLabel(rating));
            panel.add(checkBox);
            panel.add(new JLabel("Labels: "));
            String labels = "";
            for(model.entities.Label l : e.getLabels()){
                labels +=l.getValue()+", ";
            }
            panel.add(new JLabel(labels));

            panel.setBorder(getBorder());
            panel.setPreferredSize(new Dimension(200, 75));
            //selectedExemplarMap.put(e.getName(), checkBox);
            exemplarJPanelMap.put(e, panel);
        }
    }


    void addExemplarPanelsToParentPanel(){
        for(Exemplar e : referenceExemplars){
            exemplarParentPanel.add(exemplarJPanelMap.get(e));
        }
    }

    public void createMemberPanels(){
        for(User m : members){
            JPanel panel = new JPanel();
            panel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent event) {
                    if(event.getClickCount()==2 && event.getButton() == MouseEvent.BUTTON1){
                        List<String> exemplar = new ArrayList<>();
                        exemplar.add(m.getUsername());
                        //exemplarListener.tabRequested(exemplar);
                    }
                }
            });
            panel.setLayout(new GridLayout(4,3));
            JLabel usernameLabel = new JLabel("Username: ");
            JLabel username = new JLabel(m.getUsername());
            panel.add(username);


            //muss noch ergänzt werden!!

            membersJPanelMap.put(m, panel);
        }
    }

    void addMemberPanelsToParentPanel(){
        for(User m : members){
            membersParentPanel.add(membersJPanelMap.get(m));
        }
    }


    public JButton getUpdateButton() {
        return updateButton;
    }

    public void setUpdateCommunityListener(UpdateCommunityListener updateCommunityListener) {
        this.updateCommunityListener = updateCommunityListener;
    }

    public void setDeleteCommunityListener(DeleteCommunityListener deleteCommunityListener) {
        this.deleteCommunityListener = deleteCommunityListener;
    }
    public void setCloseListener(ActionWithComponentListener closeListener) {
        this.closeListener = closeListener;
    }

    public void setUserListener(AddUserListener userListener) {
        this.addUserListener = userListener;
    }


    public boolean isEditable(){
        return this.editable;
    }

    /*public Exemplar getExemplar() {
    }*/
}
