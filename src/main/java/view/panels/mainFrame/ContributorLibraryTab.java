package view.panels.mainFrame;

import model.entities.Exemplar;
import model.entities.User;
import model.httpclients.ExemplarClient;
import model.httpclients.RatingClient;
import model.httpclients.UserClient;
import view.listeners.mainframe.CloseTabListener;
import view.listeners.mainframe.homeTab.NewTabListener;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class ContributorLibraryTab extends JPanel {
    JPanel contributorPanelParent = new JPanel();
    private List<User> allContributors;
    private JScrollPane scrollPane;
    Border border = BorderFactory.createBevelBorder(0);
    private NewTabListener contributorListener;
    private Map<String, JCheckBox> selectedContributorMap = new HashMap<>();
    JPanel buttonPanel;
    private CloseTabListener closeListener;



    public ContributorLibraryTab(String searchTerm){
        scrollPane = new JScrollPane(contributorPanelParent);
        scrollPane.setLayout(new ScrollPaneLayout());

        fetchContributors(searchTerm);

        contributorPanelParent.setLayout(new GridLayout(allContributors.size()+1, 1));
        addContributorsToScrollPane();

        initializeButtonPanel();
        addComponents();
    }

    public void fetchContributors(String searchTerm){
        allContributors = new UserClient().searchUsers(searchTerm);
        allContributors = allContributors
                .stream()
                .filter(u->u.getIsContributor()==1)
                .collect(Collectors.toList());
    }

    public void addContributorsToScrollPane(){
        int i = 0;
        for(User u : allContributors){
            if(u.getIsContributor() ==1) {
                JPanel panel = new JPanel();
                panel.setLayout(new GridLayout(2, 3));
                panel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent event) {
                        if(event.getClickCount()==2 && event.getButton() == MouseEvent.BUTTON1){
                            List<String> contributors = new ArrayList<>();
                            contributors.add(u.getUsername());
                            contributorListener.tabRequested(contributors);
                        }
                    }
                });
                JLabel name = new JLabel("Name: ");
                JLabel userName = new JLabel(u.getUsername());
                JCheckBox checkBox = new JCheckBox();
               // if (i % 2 == 0) checkBox.setBackground(Color.LIGHT_GRAY);
                panel.add(name);
                panel.add(userName);
                panel.add(new JLabel(""));
                panel.add(checkBox);
                panel.setBorder(border);
                panel.setPreferredSize(new Dimension(200, 50));
                //if (i % 2 == 0) panel.setBackground(Color.LIGHT_GRAY);
                selectedContributorMap.put(u.getUsername(), checkBox);
                contributorPanelParent.add(panel);
                i++;
            }
        }

    }

    void addComponents(){
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
        buttonPanel.setLayout(new GridLayout(1,3));
        String [] sortingComboBoxList = {"Sort by average Rating of Exemplars", "Sort by Number of Exemplars"};
        String [] sortingComboBoxList2 = {"descending", "ascending"};
        JComboBox sortingComboBox = new JComboBox(sortingComboBoxList);
        JComboBox sortingComboBox2 = new JComboBox(sortingComboBoxList2);
        JButton filterButton = new JButton("Filter by Label");
        JButton openContributorsButton = new JButton("Open Selected");
        JButton closeLibraryButton = new JButton("Close Library");
        sortingComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent event) {
                // do something
            }

        });

        openContributorsButton.addActionListener((x)->openContributors());
        closeLibraryButton.addActionListener((x)->closeListener.shutdownRequested(this));
        buttonPanel.add(sortingComboBox);
        buttonPanel.add(sortingComboBox2);
        buttonPanel.add(filterButton);
        buttonPanel.add(openContributorsButton);
        buttonPanel.add(closeLibraryButton);
        buttonPanel.setBorder(border);
    }

    void openContributors(){
        Set<Map.Entry<String, JCheckBox>> entrySet = selectedContributorMap.entrySet();
        List<String> selectedContributors = new ArrayList<>();
        for(Map.Entry<String, JCheckBox> e: entrySet){
            if(e.getValue().isSelected()) {
                selectedContributors.add(e.getKey());
                e.getValue().doClick();
            }
        }
        contributorListener.tabRequested(selectedContributors);
    }

    public void setCloseListener(CloseTabListener closeListener) {
        this.closeListener = closeListener;
    }

    public void setContributorListener(NewTabListener contributorListener) {
        this.contributorListener = contributorListener;
    }
}
