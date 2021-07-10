package view.panels.mainFrame.homeTab;

import model.entities.User;
import view.listeners.mainframe.homeTab.NewTabListener;
import view.listeners.mainframe.homeTab.ProfilePanelListener;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Default tab that gets opened at login displaying user-related information (users exemplars, communities, details)
 */
public class HomeTab extends JPanel {
    User user;
    JPanel communitiesAndProfile;
    ProfilePanel profilePanel;
    MyExemplarsPanel myExemplarsPanel;
    MyCommunitiesPanel myCommunitiesPanel;

    public HomeTab(User user){
        this.user = user;
        setLayout(new GridLayout(1,2));
        initializePanels();
        add(myExemplarsPanel);
        add(communitiesAndProfile);
    }

    void initializePanels(){
        profilePanel = new ProfilePanel(user);
        myExemplarsPanel = new MyExemplarsPanel(user);
        myCommunitiesPanel = new MyCommunitiesPanel(user);
        myExemplarsPanel.setBorder(createBorder("Exemplars"));
        myCommunitiesPanel.setBorder(createBorder("My Communities"));
        profilePanel.setBorder(createBorder("Profile"));

        communitiesAndProfile = new JPanel();
        communitiesAndProfile.setLayout(new GridLayout(2,1));
        communitiesAndProfile.add(profilePanel);
        communitiesAndProfile.add(myCommunitiesPanel);
    }

    Border createBorder(String s){
        return BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(s),
                BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    public void refresh(){
        removeAll();
        initializePanels();
        add(myExemplarsPanel);
        add(communitiesAndProfile);
    }

    public void setUpdateUserListener(ProfilePanelListener listener){
        profilePanel.setUpdateUserListener(listener);
    }

    public void setDeleteUserListener(ProfilePanelListener listener){profilePanel.setDeleteListener(listener);}

    public void setOpenExemplarListener(NewTabListener listener){myExemplarsPanel.setExemplarListener(listener);}

    public void setOpenCommunityListener(NewTabListener listener){myCommunitiesPanel.setNewTabListener(listener);}

    public void setCreateExemplarListener(ActionListener createExemplarListener) { myExemplarsPanel.setCreateExemplarListener(createExemplarListener);}

    public void setCreateCommunityListener(ActionListener createCommunityListener) { myCommunitiesPanel.setCreateExemplarListener(createCommunityListener);}

    public void setUser(User user) {
        this.user = user;
        profilePanel.setUser(user);
    }
    public ProfilePanel getProfilePanel() {
        return profilePanel;
    }

    public void setCreateExemplarLibraryListener(ActionListener exemplarLibraryListener) { myExemplarsPanel.setCreateExemplarLibraryListener(exemplarLibraryListener);}

    public void setCreateContributorLibraryListener(ActionListener contributorLibraryListener) {myExemplarsPanel.setCreateContributorLibraryListener(contributorLibraryListener);}
}
