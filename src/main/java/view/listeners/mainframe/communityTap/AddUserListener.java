package view.listeners.mainframe.communityTap;

import model.entities.User;
import view.panels.mainFrame.CommunityTab;

public interface AddUserListener {
    public void buttonClicked(CommunityTab tab);

    void addingRequested(User u);
}
