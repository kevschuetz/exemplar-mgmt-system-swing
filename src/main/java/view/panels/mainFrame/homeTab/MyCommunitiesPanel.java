package view.panels.mainFrame.homeTab;

import model.entities.Community;
import model.entities.Exemplar;
import model.entities.User;
import model.httpclients.CommunityClient;
import view.listeners.mainframe.homeTab.OpenExemplarListener;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MyCommunitiesPanel extends JPanel {
    private User user;
    JPanel communityPanelParent = new JPanel();
    private java.util.List<Community> myCommunities;
    private List<JPanel> myCommunityPanels = new ArrayList<>();
    private JScrollPane scrollPane;
    Border border = BorderFactory.createEtchedBorder(Color.GRAY, Color.BLACK);


    public MyCommunitiesPanel(User user){
        this.user=user;
        setLayout(new GridLayout(1,1));
        scrollPane = new JScrollPane(communityPanelParent);
        scrollPane.setLayout(new ScrollPaneLayout());

        fetchCommunites();

        communityPanelParent.setLayout(new GridLayout(myCommunities.size(), 1));

        addCommunites();
        add(scrollPane);
    }

    void fetchCommunites(){
        myCommunities= new CommunityClient().getCommunitiesForUser(user.getUsername());
    }

    void addCommunites(){
        for(Community c : myCommunities){
            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(2,3));
            JLabel name = new JLabel("Name: ");
            JLabel communityName = new JLabel(c.getName());
            JButton showButton = new JButton("OPEN");
            JLabel randomlabel = new JLabel("");
            panel.add(name);
            panel.add(communityName);
            panel.add(new JLabel(""));
            panel.add(randomlabel);
            panel.add(new JLabel(""));
            panel.add(showButton);
            //showButton.addActionListener((x)->exemplarListener.exemplarRequested(e.getName()));
            panel.setBorder(border);
            panel.setPreferredSize(new Dimension(200, 50));
            communityPanelParent.add(panel);
            myCommunityPanels.add(panel);
        }
    }
}
