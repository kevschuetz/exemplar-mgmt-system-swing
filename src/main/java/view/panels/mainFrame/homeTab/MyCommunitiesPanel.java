package view.panels.mainFrame.homeTab;

import model.entities.Community;
import model.entities.User;
import model.httpclients.CommunityClient;
import view.listeners.mainframe.homeTab.NewTabListener;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class MyCommunitiesPanel extends JPanel {
    private User user;
    JPanel communityPanelParent = new JPanel();
    private java.util.List<Community> myCommunities;
    private List<JPanel> myCommunityPanels = new ArrayList<>();
    private JScrollPane scrollPane;
    Border border = BorderFactory.createEtchedBorder(Color.GRAY, Color.BLACK);
    private NewTabListener newTabListener;

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
        int i = 0;
        for(Community c : myCommunities){
            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(2,3));

            panel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                   if(e.getClickCount()==2 && e.getButton() == MouseEvent.BUTTON1){
                        List<String> community = new ArrayList<>();
                        community.add(c.getName());
                        newTabListener.tabRequested(community);
                   }
                }
            });

            JLabel name = new JLabel("Name: ");
            JLabel communityName = new JLabel(c.getName());
            JLabel randomlabel = new JLabel("");
            JCheckBox checkBox = new JCheckBox();
            panel.add(name);
            panel.add(communityName);
            panel.add(randomlabel);
            panel.add(randomlabel);
            panel.add(randomlabel);
            panel.add(randomlabel);
            panel.setBorder(border);
            panel.setPreferredSize(new Dimension(200, 50));
            if(i%2==0)panel.setBackground(Color.LIGHT_GRAY);
            communityPanelParent.add(panel);
            myCommunityPanels.add(panel);
            i++;
        }
    }

    public void setNewTabListener(NewTabListener newTabListener) {
        this.newTabListener = newTabListener;
    }
}
