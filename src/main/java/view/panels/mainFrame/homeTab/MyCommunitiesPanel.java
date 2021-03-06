package view.panels.mainFrame.homeTab;

import controller.MainController;
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
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Panel that lists all the communites of a given user
 */
public class MyCommunitiesPanel extends JPanel {
    private User user;
    JPanel communityPanelParent = new JPanel();
    private java.util.List<Community> myCommunities;
    private List<JPanel> myCommunityPanels = new ArrayList<>();
    private JScrollPane scrollPane;
    Border border = BorderFactory.createBevelBorder(0);
    private NewTabListener newTabListener;
    private Map<String, JCheckBox> selectedCommunityMap = new HashMap<>();
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
    /**
     * Fetches all communities in which the current User is a member from the database
     */
    void fetchCommunites(){
        myCommunities= MainController.communities
                .stream()
                .filter(c->(c.getCreator() != null && c.getCreator().equals(user)) || (c.getMembers() != null && c.getMembers().contains(user)))
                .collect(Collectors.toList());
    }
    /**
     * Creates panels for all of the communities of the current User and adds them to a separate panel
     */
    void addCommunites(){
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
            panel.add(checkBox);
            panel.setBorder(border);
            panel.setPreferredSize(new Dimension(200, 50));
            communityPanelParent.add(panel);
            selectedCommunityMap.put(c.getName(), checkBox);
            myCommunityPanels.add(panel);
        }
    }
    /**
     * Adds the main components to the panel
     */
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
    /**
     * Initializes all the buttons of this panel
     */
    void initializeButtonPanel(){
        buttonPanel= new JPanel();
        buttonPanel.setLayout(new GridLayout(1,2));
        JButton createCommunityButton = new JButton("Create New Community");
        JButton openCommunityButton = new JButton("Open Selected");
        buttonPanel.add(createCommunityButton);
        createCommunityButton.addActionListener(x->createCommunityListener.actionPerformed(x));
        buttonPanel.add(openCommunityButton);
        openCommunityButton.addActionListener(x->openCommunity());
        buttonPanel.setBorder(border);
    }
    /**
     * Opens new tabs for the communities which were requested by the current User
     */
    void openCommunity(){
        Set<Map.Entry<String, JCheckBox>> entrySet = selectedCommunityMap.entrySet();
        List<String> selectedCommunities = new ArrayList<>();
        for(Map.Entry<String, JCheckBox> c: entrySet){
            if(c.getValue().isSelected()) {
                selectedCommunities.add(c.getKey());
                c.getValue().doClick();
            }
        }
        newTabListener.tabRequested(selectedCommunities);
    }

    public void setNewTabListener(NewTabListener newTabListener) {
        this.newTabListener = newTabListener;
    }
    public void setCreateExemplarListener(ActionListener listener){this.createCommunityListener = listener;}
}
