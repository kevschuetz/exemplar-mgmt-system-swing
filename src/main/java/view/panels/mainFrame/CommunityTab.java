package view.panels.mainFrame;

import model.entities.*;
import model.entities.Label;
import view.frames.mainFrame.AddCommentPopupFrame;
import view.frames.mainFrame.AddUserrFrame;
import view.listeners.mainframe.ActionWithComponentListener;
import view.listeners.mainframe.communityTap.DeleteCommunityListener;
import view.listeners.mainframe.communityTap.UpdateCommunityListener;
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
    private JPanel exemplarsPanel= new JPanel();

    JButton updateButton = new JButton ("Update");
    JButton deleteButton = new JButton("Delete");
    JButton addUserButton = new JButton("add User");

    private AddUserrFrame userPopup;
    private List<Comment> users;

    private UpdateCommunityListener updateCommunityListener;
    private DeleteCommunityListener deleteCommunityListener;



    boolean editable = false;

    public CommunityTab(Community community){
        this.community=community;
        JLabel label = new JLabel("community details");
        add(label);
        addComponents();
        addActionListener();
    }

    void addComponents(){
        GridBagConstraints c = new GridBagConstraints();
        c.weighty = 0.3;
        c.weightx=1;
        c.gridy = 0;
        c.gridx = 0;
        c.fill= GridBagConstraints.BOTH;
        initializeComponents();

        add(closeButton);
    }

    Border getBorder (String s){
        return BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(s),
                BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    void initializeComponents(){
        initializeMetaInfoPanel();
       /*initializeAddUserPopupFrame();
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
        configurationPanel.add(closeButton);*/
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


        /*labelPanel = initializeLabelPanel();
        contributorPanel = initializeContributorPanel();*/


        metaInfoPanel.add(nameLabel);
        metaInfoPanel.add(creatorLabel);
        /*metaInfoPanel.add(avgRatingLabel);
        metaInfoPanel.add(labelPanel);
        metaInfoPanel.add(contributorPanel);*/
    }




    void addActionListener(){
        closeButton.addActionListener((x)->closeListener.componentSubmitted(this));
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
