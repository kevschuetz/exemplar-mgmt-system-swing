package view.panels.mainFrame;

import model.entities.Exemplar;
import model.httpclients.ExemplarClient;
import model.httpclients.RatingClient;
import view.listeners.mainframe.ActionWithComponentListener;
import view.listeners.mainframe.homeTab.NewTabListener;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;


public class ExemplarLibraryTab extends JPanel{

    JPanel exemplarPanelParent = new JPanel();
    private List<Exemplar> allExemplars;
    //private Map<Exemplar, Double> ratingMap = new HashMap<>();
    private Map<Exemplar, double []> ratingMap = new HashMap<>(); // [0] = average Rating [1] = number of ratings

    private Map<Exemplar, JPanel> exemplarJPanelMap = new HashMap<>();
    private Map<String, JCheckBox> selectedExemplarMap = new HashMap<>();

    private JScrollPane scrollPane;
    Border border = BorderFactory.createBevelBorder(0);
    private NewTabListener exemplarListener;
    JPanel buttonPanel;
    private ActionWithComponentListener closeListener;

    private ItemListener sortingListener;
    JComboBox sortingComboBox;
    JComboBox sortingComboBox2;

    public ExemplarLibraryTab(String searchTerm){
        fetchExemplars(searchTerm);
        initializeSortingListener();

        exemplarPanelParent.setLayout(new GridLayout(allExemplars.size()+1, 1));
        createExemplarPanels();
        addExemplarPanelsToParentPanel();

        scrollPane = new JScrollPane(exemplarPanelParent);
        scrollPane.setLayout(new ScrollPaneLayout());

        initializeButtonPanel();
        addComponents();
    }

    private void initializeSortingListener() {
        sortingListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent event) {
                if(sortingComboBox.getSelectedIndex() == 1) {
                    if(sortingComboBox2.getSelectedIndex() == 0) {
                        allExemplars = allExemplars.stream().
                                sorted(Comparator.comparingDouble(e -> ratingMap.get(e)[0])).collect(Collectors.toList());
                        Collections.reverse(allExemplars);
                    }else
                        allExemplars = allExemplars.stream().
                                sorted(Comparator.comparingDouble(e -> ratingMap.get(e)[0])).collect(Collectors.toList());
                }
                if(sortingComboBox.getSelectedIndex() == 2) {
                    if(sortingComboBox2.getSelectedIndex() == 0) {
                        allExemplars = allExemplars.stream().
                                sorted(Comparator.comparingDouble(e -> ratingMap.get(e)[1])).collect(Collectors.toList());
                        Collections.reverse(allExemplars);
                    }else
                        allExemplars = allExemplars.stream().
                                sorted(Comparator.comparingDouble(e -> ratingMap.get(e)[1])).collect(Collectors.toList());
                }
                updateTab();
            }
        };
    }

    public void fetchExemplars(String searchTerm){
        ExemplarClient exemplarClient = new ExemplarClient();
        RatingClient ratingClient = new RatingClient();
        allExemplars = exemplarClient.searchExemplars(searchTerm);
        for (Exemplar e : allExemplars){
            ratingMap.put(e, new double[]{ratingClient.getAvgRatingForExemplar(e.getName()),
                    ratingClient.getRatingsForExemplar(e.getName()).size()});
        }
    }

    public void createExemplarPanels(){
        RatingClient client = new RatingClient();
        for(Exemplar e : allExemplars){
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
            rating += ratingMap.get(e);
            panel.add(new JLabel(rating));
            panel.add(checkBox);
            panel.setBorder(border);
            panel.setPreferredSize(new Dimension(200, 50));
            selectedExemplarMap.put(e.getName(), checkBox);
            exemplarJPanelMap.put(e, panel);
        }
    }

    void addExemplarPanelsToParentPanel(){
        for(Exemplar e : allExemplars){
            exemplarPanelParent.add(exemplarJPanelMap.get(e));
        }
    }

    void addComponents(){
        setVisible(false);
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
        setVisible(true);
    }

    void initializeButtonPanel(){
        buttonPanel= new JPanel();
        buttonPanel.setLayout(new GridLayout(1,3));
        String [] sortingComboBoxList = {"Sort by creation date", "Sort by Rating", "Sort by Number of Users"};
        String [] sortingComboBoxList2 = {"descending", "ascending"};
        sortingComboBox = new JComboBox(sortingComboBoxList);
        sortingComboBox2 = new JComboBox(sortingComboBoxList2);
        JButton filterButton = new JButton("Filter by Label");
        JButton openExemplarsButton = new JButton("Open Selected");
        JButton closeLibraryButton = new JButton("Close Library");
        sortingComboBox.addItemListener(sortingListener);
        sortingComboBox2.addItemListener(sortingListener);

        closeLibraryButton.addActionListener((x)->closeListener.componentSubmitted(this));
        openExemplarsButton.addActionListener((x)->openExemplars());
        buttonPanel.add(sortingComboBox);
        buttonPanel.add(sortingComboBox2);
        buttonPanel.add(filterButton);
        buttonPanel.add(openExemplarsButton);
        buttonPanel.add(closeLibraryButton);
        buttonPanel.setBorder(border);
    }

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

    public void setCloseListener(ActionWithComponentListener closeListener) {
        this.closeListener = closeListener;
    }

    public void setExemplarListener(NewTabListener exemplarListener) {
        this.exemplarListener = exemplarListener;
    }

    public void updateTab (){
        removeAll();
        exemplarPanelParent.removeAll();
        addExemplarPanelsToParentPanel();
        scrollPane = new JScrollPane((exemplarPanelParent));
        scrollPane.setLayout(new ScrollPaneLayout());
        addComponents();
    }
}
