package view.panels.mainFrame;

import model.entities.*;
import model.entities.Label;
import view.frames.mainFrame.ConfirmCommunityDeletionFrame;
import view.listeners.ActionWithStringListener;
import view.listeners.mainframe.ActionWithComponentListener;
import view.listeners.mainframe.communityTap.DeleteCommunityListener;
import view.listeners.mainframe.communityTap.ActionWithUserListener;
import view.listeners.mainframe.communityTap.UpdateCommunityListener;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
/**
 * Represents information about a specific community (Community Dashboard)
 */
public class CommunityTab extends JPanel {
    private Community community;
    private User currentUser;

    private ActionWithComponentListener closeListener;
    private List <Exemplar> referenceExemplars;

    private JPanel parentPanel = new JPanel();
    private JPanel metaInfoPanel = new JPanel();
    private JPanel configurationPanel= new JPanel();
    JScrollPane scrollPane = new JScrollPane();

    private JPanel buttons;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton joinButton;
    private JButton closeButton;

    private JPanel exemplarParentPanel=new JPanel();
    private JPanel membersParentPanel;

    private ActionWithUserListener joinListener;
    private ActionWithUserListener leaveListener;
    private ActionWithStringListener showExemplarListener;
    private ActionWithStringListener removeExemplarListener;
    private ActionListener joinAction;
    private ActionListener leaveAction;

    private List<User> members;
    private UpdateCommunityListener updateCommunityListener;
    private DeleteCommunityListener deleteCommunityListener;
    private ConfirmCommunityDeletionFrame confirmCommunityDeletionFrame;
    private ActionWithStringListener memberClickedListener;

    boolean editable = false;

    private Map<User, JPanel> membersJPanelMap = new HashMap<>();
    private JButton leaveButton = new JButton("Leave");

    public CommunityTab(Community community, User currentUser){
        this.community=community;
        this.currentUser = currentUser;
        this.buttons = new JPanel();
        this.exemplarParentPanel = new JPanel();
        this.membersParentPanel = new JPanel();
        this.members = community.getMembers();
        this.referenceExemplars = community.getExemplars();
        createMemberPanels();
        addMemberPanelsToParentPanel();

        setEditable();
        setLayout();
        setBorder(getBorder(community.getName()));
        initializeButtons();
        initializeComponents();
        addComponents();

        initializeActionListeners();
        addActionListener();
        initializeDeleteFrame();
    }

    void setEditable(){
        editable = true;
    }
    /**
     * Adds all the components to the panel
     */
    void addComponents(){
        GridBagConstraints c = new GridBagConstraints();
        c.weighty = 0.3;
        c.weightx=1;
        c.gridy = 0;
        c.gridx = 0;
        c.fill= GridBagConstraints.BOTH;
        parentPanel.add(metaInfoPanel,c);

        c.weighty=0.7;
        c.gridy=1;
        membersParentPanel.setBorder(getBorder( "Members:"));
        parentPanel.add(membersParentPanel, c);

        c.gridy=2;
        parentPanel.add(exemplarParentPanel,c);


        scrollPane = new JScrollPane(parentPanel);
        setLayout(new GridBagLayout());
        c.weighty = 0.9;
        c.weightx=1;
        c.gridy = 0;
        c.gridx = 0;
        c.fill= GridBagConstraints.BOTH;
        add(scrollPane,c);
        c.gridy=1;
        c.weighty=0.01;
        add(configurationPanel,c);
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
    /**
     * Adds action listeners to the buttons
     */
    void addActionListener(){
        joinButton.addActionListener(joinAction);
        leaveButton.addActionListener(leaveAction);

        closeButton.addActionListener(x->closeListener.componentSubmitted(this));
        updateButton.addActionListener(x->{
            try {
                updateCommunityListener.updateRequested(community);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        deleteButton.addActionListener(e->
            confirmCommunityDeletionFrame.setVisible(true));
    }
    /**
     * Initializes button related action listeners
     */
    void initializeActionListeners(){
        this.leaveAction= e -> {
            leaveListener.actionPerformed(currentUser);
            buttons.setVisible(false);
            buttons.remove(leaveButton);
            buttons.add(joinButton);
            buttons.setVisible(true);
            members.remove(currentUser);
            membersJPanelMap.remove(currentUser);
            addMemberPanelsToParentPanel();
        };

        this.joinAction= e -> {
            joinListener.actionPerformed(currentUser);
            buttons.setVisible(false);
            buttons.remove(joinButton);
            buttons.add(leaveButton);
            buttons.setVisible(true);
            members.add(currentUser);
            membersJPanelMap.put(currentUser, createMemberPanel(currentUser));
            addMemberPanelsToParentPanel();
        };

    }
    /**
     * Initializes pop-up frame for deleting the community
     */
    void initializeDeleteFrame(){
        confirmCommunityDeletionFrame = new ConfirmCommunityDeletionFrame(community.getName());
        confirmCommunityDeletionFrame.setVisible(false);
        confirmCommunityDeletionFrame.setSize(new Dimension(400
                ,300));
        confirmCommunityDeletionFrame.setLocationRelativeTo(this);
        confirmCommunityDeletionFrame.setConfirmListener(x->{
            confirmCommunityDeletionFrame.setVisible(false);
            deleteCommunityListener.deleteRequested(community.getName(), this);
        });
    }
    /**
     * Initializes basic components of the panel
     */
    void initializeComponents(){
        initializeMetaInfoPanel();
        initializeExemplarParentPanel();
        configurationPanel.setLayout(new GridLayout(3, 8));
        configurationPanel.add(buttons);
    }
    /**
     * Initializes the button panel
     */
    void initializeButtons(){
        updateButton = new JButton ("Update");
        deleteButton = new JButton("Delete");
        joinButton = new JButton("Join");
        closeButton = new JButton("Close");
        if(editable){
            if(community.getCreator().equals(currentUser)){
                buttons.add(deleteButton);
            }else{
                if(!userIsMember(currentUser)){
                    buttons.add(joinButton);
                }else{buttons.add(leaveButton);}
            }
            buttons.add(closeButton);
        }

    }
    /**
     * Initializes the meta info panel
     */
    private void initializeMetaInfoPanel() {
        metaInfoPanel = new JPanel();

        JLabel nameLabel = new JLabel("Name: "+ community.getName());
        nameLabel.setHorizontalAlignment(SwingConstants.LEFT);
        JLabel creatorLabel = new JLabel("");
        if(community.getCreator() != null)  creatorLabel = new JLabel ("Creator: " + community.getCreator().getUsername());
        creatorLabel.setHorizontalAlignment(SwingConstants.LEFT);
        JLabel numberOfExemplars = new JLabel("Number  of Exemplars: "+ referenceExemplars.size());
        JLabel numberOfMembers = new JLabel("Number  of Members: "+ community.getMembers().size());

        List<model.entities.Label> labels = new ArrayList<>();
        Set<model.entities.Label> labelsDistinct = new HashSet<>();
        for(Exemplar e : referenceExemplars){
            labels.addAll(e.getLabels());
            labelsDistinct.addAll(e.getLabels());
        }

        Map<Label, Integer> labelCount = new HashMap<>();
        for(Label l : labelsDistinct){
            labelCount.put(l, 0);
        }
        for(Label l : labels){
            labelCount.put(l, labelCount.get(l)+1);
        }
       Iterator it = labelCount.entrySet().iterator();
       int highest = -1;
       Label top = null;
       while(it.hasNext()){
           Map.Entry<Label, Integer> entry = (Map.Entry<Label, Integer>) it.next();
           if(entry.getValue()>highest){
               highest = entry.getValue();
               top = entry.getKey();
           }
       }
        JLabel topLabel = null;
        if(top != null)  topLabel = new JLabel("Top Label: "+top.getValue());
        metaInfoPanel.setBorder(getBorder("Info"));
        metaInfoPanel.setLayout(new GridLayout(5,1));


        metaInfoPanel.add(nameLabel);
        metaInfoPanel.add(creatorLabel);
        metaInfoPanel.add(numberOfExemplars);
        metaInfoPanel.add(numberOfMembers);
        if(topLabel != null) metaInfoPanel.add(topLabel);
    }
    /**
     * Checks if the given user is a member of this community
     * @param u the user whose membership needs to be checked 
     * @return True if the given user is a member False if the user is not a member
     */
    public boolean userIsMember(User u){
        return community.getMembers().contains(u);
    }
    /**
     * Initializes the exemplar parent panel which stores all the exemplars which were added to this community
     */
    void initializeExemplarParentPanel(){
        exemplarParentPanel.removeAll();
        JList exemplarList = new JList(referenceExemplars
                .stream()
                .map(Exemplar::getName)
                .collect(Collectors.toList())
                .toArray());
        exemplarParentPanel.setBorder(getBorder("Exemplars"));

        JPanel exemplarButtonPanel = new JPanel();
        exemplarButtonPanel.setLayout(new GridLayout(1,2));
        JButton showButton = new JButton("Show");
        showButton.setSize(new Dimension(60,60));
        showButton.addActionListener(e-> showExemplarListener.stringSubmitted((String)exemplarList.getSelectedValue()));
        JButton removeButton = new JButton("Remove");
        removeButton.setSize(new Dimension(60,60));
        removeButton.addActionListener(e-> removeExemplarListener.stringSubmitted((String)exemplarList.getSelectedValue()));
        exemplarButtonPanel.add(showButton);
        exemplarButtonPanel.add(removeButton);
        exemplarButtonPanel.setBorder(getBorder("Options"));


        exemplarParentPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridy=0;
        c.gridx=0;
        c.weighty=0.9;
        c.weightx=1;
        c.fill=GridBagConstraints.BOTH;

        exemplarParentPanel.add(exemplarList,c);

        c.weightx=0.1;
        c.gridx=1;
        c.weighty=0.05;
        exemplarParentPanel.add(exemplarButtonPanel, c);
    }
    /**
     * Creates panels for all the members of the community
     */
    public void createMemberPanels(){
        for(User m : members){
            JPanel panel = createMemberPanel(m);
            membersJPanelMap.put(m, panel);
        }
    }
    /**
     * Creates a panel for the given user
     * @param m the user for whom a panel should be created
     * @return panel the panel which was created for the user
     */
    public JPanel createMemberPanel(User m){
        JPanel panel = new JPanel();
        panel.setBorder(getBorder(""));
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if(event.getClickCount()==2 && event.getButton() == MouseEvent.BUTTON1){
                    memberClickedListener.stringSubmitted(m.getUsername());
                }
            }
        });
        panel.setLayout(new GridLayout(1,1));
        JLabel username = new JLabel(m.getUsername());
        panel.add(username);
        return panel;
    }
    /**
     * Adds all the panels which were created for the community's members to a separate panel
     */
    void addMemberPanelsToParentPanel(){
        membersParentPanel.removeAll();
        membersParentPanel.setVisible(false);
        for(User m : members){
            membersParentPanel.add(membersJPanelMap.get(m));
        }
        membersParentPanel.setVisible(true);
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

    public void setJoinCommunityListener(ActionWithUserListener joinListener) {
        this.joinListener = joinListener;
    }


    public boolean isEditable(){
        return this.editable;
    }

    public Community getCommunity() {
        return community;
    }

    public void setLeaveListener(ActionWithUserListener leaveListener) {
        this.leaveListener = leaveListener;
    }

    public void setMemberClickedListener(ActionWithStringListener memberClickedListener) {
        this.memberClickedListener = memberClickedListener;
    }

    public void setShowExemplarListener(ActionWithStringListener showExemplarListener) {
        this.showExemplarListener = showExemplarListener;
    }

    public void setRemoveExemplarListener(ActionWithStringListener removeExemplarListener) {
        this.removeExemplarListener = removeExemplarListener;
    }
}
