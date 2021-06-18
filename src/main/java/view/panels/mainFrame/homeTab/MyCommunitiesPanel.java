package view.panels.mainFrame.homeTab;

import model.entities.Community;
import model.entities.User;
import model.httpclients.CommunityClient;
import view.listeners.mainframe.homeTab.NewTabListener;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionListener;
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
    Border border = BorderFactory.createBevelBorder(0);
    private NewTabListener newTabListener;
    JPanel buttonPanel;

    private ActionListener createCommunityListener;

    public MyCommunitiesPanel(User user){
        this.user=user;
        setLayout(new GridLayout(1,1));
        scrollPane = new JScrollPane(communityPanelParent);
        scrollPane.setLayout(new ScrollPaneLayout());

        fetchCommunites();

        communityPanelParent.setLayout(new GridLayout(myCommunities.size(), 1));
        initializeButtonPanel();
        addCommunites();
        add(scrollPane);
        addComponentsCommunity();
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
            panel.setLayout(new GridLayout(2,3));
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
            //if(i%2==0)panel.setBackground(Color.LIGHT_GRAY);
            communityPanelParent.add(panel);
            myCommunityPanels.add(panel);
            i++;
        }
    }

    void addComponentsCommunity(){
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        //adding scrollpane
        c.fill = GridBagConstraints.BOTH;
        c.weighty=0.95;
        c.weightx=1;
        c.gridx=0;
        c.gridy=0;
        add(scrollPane, c);

        //adding button
        c.weighty=0.05;
        c.gridx=0;
        c.gridy=1;
        add(buttonPanel, c);

    }

    void initializeButtonPanel(){
        buttonPanel= new JPanel();
        buttonPanel.setLayout(new GridLayout(1,2));
        JButton createCommunityButton = new JButton("Create New Community");
        buttonPanel.add(createCommunityButton);
        createCommunityButton.addActionListener(x->createCommunityListener.actionPerformed(x));
        //buttonPanel.add(searchAllButton);
        buttonPanel.setBorder(border);
    }
    public void setNewTabListener(NewTabListener newTabListener) {
        this.newTabListener = newTabListener;
    }

    public void setCommuniyListener(NewTabListener communiyListener) {
        this.newTabListener = communiyListener;
    }
    public void setCreateExemplarListener(ActionListener listener){this.createCommunityListener = listener;}
}
