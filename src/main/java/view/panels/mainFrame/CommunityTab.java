package view.panels.mainFrame;

import model.entities.Community;
import model.entities.Exemplar;
import model.entities.Label;
import view.listeners.mainframe.ActionWithComponentListener;
import view.listeners.mainframe.communityTap.DeleteCommunityListener;
import view.listeners.mainframe.communityTap.UpdateCommunityListener;
import view.listeners.mainframe.exemplarTab.DeleteExemplarListener;
import view.listeners.mainframe.exemplarTab.UpdateExemplarListener;

import javax.swing.*;

public class CommunityTab extends JPanel {
    private Community community;
    private JButton closeButton = new JButton("Close");
    private ActionWithComponentListener closeListener;
    private Exemplar[] referenceExemplars;

    private UpdateCommunityListener updateCommuniyListener;
    private DeleteCommunityListener deleteCommunityListener;

    JButton updateButton = new JButton ("Update");
    JButton deleteButton = new JButton("Delete");

    boolean editable = false;

    public CommunityTab(Community community){
        this.community=community;
        JLabel label = new JLabel("community details");
        add(label);
        addComponents();
        addActionListener();
    }

    void addComponents(){
        add(closeButton);
    }

    void addActionListener(){
        closeButton.addActionListener((x)->closeListener.componentSubmitted(this));
    }

    public JButton getUpdateButton() {
        return updateButton;
    }

    public void setUpdateCommunityListener(UpdateCommunityListener updateCommunityListener) {
        this.updateCommunityListener = updateExemplarListener;
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
