package view.panels.mainFrame.homeTab;

import controller.MainController;
import model.entities.Exemplar;
import model.entities.User;
import model.httpclients.ExemplarClient;
import model.httpclients.RatingClient;
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
 * Panel that lists all the exemplars of a given user as part of the home tab
 */
public class MyExemplarsPanel extends JPanel {
    private User user;
    JPanel exemplarPanelParent = new JPanel();
    private List<Exemplar> myExemplars;
    private JScrollPane scrollPane;
    Border border = BorderFactory.createBevelBorder(0);
    private NewTabListener exemplarListener;
    private Map<String, JCheckBox> selectedExemplarMap = new HashMap<>();
    JPanel buttonPanel;

    ActionListener exemplarLibraryListener;
    ActionListener contributorLibraryListener;

    private ActionListener createExemplarListener;

    public MyExemplarsPanel(User user){
        this.user=user;
        scrollPane = new JScrollPane(exemplarPanelParent);
        scrollPane.setLayout(new ScrollPaneLayout());

        fetchExemplars();

        exemplarPanelParent.setLayout(new GridLayout(myExemplars.size()+1, 1));
        addExemplarsToScrollPane();

        initializeButtonPanel();
        addComponents();
    }
    /**
     * Fetches all the Exemplars of the current User from the database
     */
    public void fetchExemplars(){
        myExemplars = MainController.exemplars
                .stream()
                .filter(e->e.getCreator() != null && e.getCreator().equals(user) || e.getContributors() != null && e.getContributors().contains(user))
                .collect(Collectors.toList());
    }
    /**
     * Adds all the Exemplars of the current User to a scroll pane
     */
    public void addExemplarsToScrollPane(){
        RatingClient client = new RatingClient();
        for(Exemplar e : myExemplars){
            JPanel panel = new JPanel();
            panel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent event) {
                    if(event.getClickCount()==2 && event.getButton() == MouseEvent.BUTTON1){
                        List<String> exemplar = new ArrayList<>();
                        exemplar.add(e.getName());
                        exemplarListener.tabRequested(exemplar);
                    }
                }
            });
            panel.setLayout(new GridLayout(2,3));
            JLabel name = new JLabel("Name: ");
            JLabel exemplarName = new JLabel(e.getName());
            JLabel ratingLabel = new JLabel("Rating:");
            JCheckBox checkBox = new JCheckBox();
            panel.add(name);
            panel.add(exemplarName);
            panel.add(new JLabel(""));
            panel.add(ratingLabel);
            String rating = "";
            rating += MainController.ratings.stream().filter(r->r.getKey().getExemplar().equals(e)).mapToDouble(r-> r.getRating()).average().orElse(0);
            panel.add(new JLabel(rating));
            panel.add(checkBox);
            panel.setBorder(border);
            panel.setPreferredSize(new Dimension(200, 50));
            selectedExemplarMap.put(e.getName(), checkBox);
            exemplarPanelParent.add(panel);
        }

    }
    /**
     * Initializes all the buttons of this panel
     */
    void initializeButtonPanel(){
        buttonPanel= new JPanel();
        buttonPanel.setLayout(new GridLayout(1,3));
        JButton openExemplarsButton = new JButton("Open Selected");
        JButton createExemplarButton = new JButton("Create New");
        JButton exemplarLibraryButton =  new JButton("All Exemplars");
        JButton contributorLibraryButton =  new JButton("All Contributors");

        buttonPanel.add(openExemplarsButton);
        openExemplarsButton.addActionListener(x->openExemplars());
        if(user.getIsContributor()==1){
            buttonPanel.add(createExemplarButton);
            createExemplarButton.addActionListener(x-> createExemplarListener.actionPerformed(x));
        }

        exemplarLibraryButton.addActionListener(x -> exemplarLibraryListener.actionPerformed(x));
        contributorLibraryButton.addActionListener(x -> contributorLibraryListener.actionPerformed(x));
        buttonPanel.setBorder(border);
    }
    /**
     * Adds the main components to the panel
     */
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
    /**
     * Creates new tabs for the Exemplars which were requested by the current User
     */
    void openExemplars(){
        Set<Map.Entry<String, JCheckBox>> entrySet = selectedExemplarMap.entrySet();
        List<String> selectedExemplars = new ArrayList<>();
        for(Map.Entry<String, JCheckBox> e: entrySet){
            if(e.getValue().isSelected()) {
                selectedExemplars.add(e.getKey());
                e.getValue().doClick();
            }
        }
        exemplarListener.tabRequested(selectedExemplars);
    }

    public void setExemplarListener(NewTabListener exemplarListener) {
        this.exemplarListener = exemplarListener;
    }
    public void setCreateExemplarListener(ActionListener listener){this.createExemplarListener = listener;}


    public void setCreateExemplarLibraryListener(ActionListener exemplarLibraryListener){this.exemplarLibraryListener = exemplarLibraryListener;}
    public void setCreateContributorLibraryListener(ActionListener contributorLibraryListener){this.contributorLibraryListener = contributorLibraryListener;}
}

