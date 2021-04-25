package view.listeners.mainframe.homeTab;


import model.entities.User;
import view.events.UserEvent;

public interface UpdateUserListener {
    public void updateRequested(User u);
}
