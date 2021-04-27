package view.listeners.mainframe.homeTab;


import model.entities.User;
import view.events.UserEvent;

public interface ProfilePanelListener {
    public void updateRequested(User u);
}
