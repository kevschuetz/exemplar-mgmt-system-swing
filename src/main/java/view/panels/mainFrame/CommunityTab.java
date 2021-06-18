package view.panels.mainFrame;

import model.entities.*;
import model.entities.Label;
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
import java.io.IOException;
import java.util.List;

public class CommunityTab extends JPanel {
    private Community community;
    private JButton closeButton = new JButton("Close");
    private ActionWithComponentListener closeListener;
    private Exemplar[] referenceExemplars;

    private JPanel metaInfoPanel = new JPanel();
    private JPanel configurationPanel= new JPanel();
    private JPanel exemplarsPanel= new JPanel();

    JButton updateButton = new JButton ("Update");
    JButton deleteButton = new JButton("Delete");
    JButton addUserButton = new JButton("add User");

    private AddUserListener addUserListener;

    private AddUserrFrame userPopup;
    private List<Comment> users;

    private UpdateCommunityListener updateCommunityListener;
    private DeleteCommunityListener deleteCommunityListener;
    private ConfirmCommunityDeletionFrame confirmCommunityDeletionFrame;

    boolean editable = false;

    public CommunityTab(Community community){
        this.community=community;
        JLabel label = new JLabel("community details");
        add(label);
        addComponents();
        initializeComponents();
        setEditable();
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
        add(closeButton);
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
        configurationPanel.setLayout(new GridLayout(1, 8));
        if(editable){
            configurationPanel.add(updateButton);
            configurationPanel.add(deleteButton);
            configurationPanel.add(addUserButton);
        }
        configurationPanel.add(closeButton);
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
        /*metaInfoPanel.add(avgRatingLabel);
        metaInfoPanel.add(labelPanel);
        metaInfoPanel.add(contributorPanel);*/
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

    public boolean isEditable(){
        return this.editable;
    }
}
