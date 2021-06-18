package view.listeners.mainframe.communityTap;

import model.entities.Community;

import java.io.IOException;

public interface UpdateCommunityListener {

    void updateRequested(Community c) throws IOException, InterruptedException;

}
