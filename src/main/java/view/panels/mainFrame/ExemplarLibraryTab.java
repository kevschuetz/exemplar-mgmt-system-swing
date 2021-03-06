package view.panels.mainFrame;

import controller.MainController;
import model.entities.Exemplar;
import model.entities.Rating;
import model.httpclients.ExemplarClient;
import model.httpclients.RatingClient;
import view.frames.mainFrame.FilterLabelPopupFrame;
import view.listeners.mainframe.ActionWithComponentListener;
import view.listeners.mainframe.homeTab.NewTabListener;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Panel that lists all the exemplars of the system and provides different sorting, filtering options
 */
public class ExemplarLibraryTab extends JPanel{
    private List<Exemplar> allExemplars;
    private List<Exemplar> filteredExemplars=new ArrayList<>();
    private Map<Exemplar, double []> exemplarAvgRatingNumberOfRatingsMap = new HashMap<>(); // [0] = average Rating [1] = number of ratings
    private Map<Exemplar, JPanel> exemplarJPanelMap = new HashMap<>();
    private Map<String, JCheckBox> selectedExemplarMap = new HashMap<>();
    private Set<model.entities.Label> allLabels = new HashSet<>();
    private List<String> filteredLabels = new ArrayList<>();
    private Map<Exemplar, List<Rating>> ratingsForExemplarLastWeek = new HashMap<>();
    private Map<Exemplar, List<Rating>> allRatingsForExemplar = new HashMap<>();
    private boolean filtered = false;


    private JScrollPane scrollPane;
    JPanel exemplarPanelParent = new JPanel();
    JPanel buttonPanel;
    JButton filterButton;
    JButton mostRated;
    Border border = BorderFactory.createBevelBorder(0);

    JComboBox sortingComboBox;
    JComboBox sortingComboBox2;

    private ActionWithComponentListener closeListener;
    private NewTabListener exemplarListener;
    private ItemListener sortingListener;

    private FilterLabelPopupFrame filterLabelPopupFrame;
    private boolean ratedLastWeekFilterIsActive=false;

    public ExemplarLibraryTab(String searchTerm){
        fetchExemplars(searchTerm);
        initializeSortingListener();

        exemplarPanelParent.setLayout(new GridLayout(allExemplars.size()+1, 1));
        createExemplarPanels();
        addExemplarPanelsToParentPanel();

        scrollPane = new JScrollPane(exemplarPanelParent);
        scrollPane.setLayout(new ScrollPaneLayout());

        initializeButtonPanel();
        initializeFilterLabelFrame();
        addComponents();
    }

    /**
     * Initializes sorting listener that sorts the exemplars according to the combo-boxes
     */
    private void initializeSortingListener() {
        sortingListener = event -> {
            /**
             * Sort alphabetically
             */
            if(sortingComboBox.getSelectedIndex() == 0) {
                allExemplars = allExemplars.stream().
                        sorted(Comparator.comparing(e -> e.getName().toLowerCase())).collect(Collectors.toList());
                filteredExemplars=filteredExemplars.stream().
                        sorted(Comparator.comparing(e -> e.getName().toLowerCase())).collect(Collectors.toList());
                if(sortingComboBox2.getSelectedIndex() == 1){
                    Collections.reverse(allExemplars);
                    Collections.reverse(filteredExemplars);
                }
            }


            /**
             * Sort by avg rating
             */
            if(sortingComboBox.getSelectedIndex() == 1) {
                    allExemplars = allExemplars.stream().
                            sorted(Comparator.comparingDouble(e -> exemplarAvgRatingNumberOfRatingsMap.get(e)[0])).collect(Collectors.toList());
                    filteredExemplars=filteredExemplars.stream().
                            sorted(Comparator.comparingDouble(e -> exemplarAvgRatingNumberOfRatingsMap.get(e)[0])).collect(Collectors.toList());
                if(sortingComboBox2.getSelectedIndex() == 1){
                    Collections.reverse(allExemplars);
                    Collections.reverse(filteredExemplars);
                }
            }

            /**
             * Sort by number of ratings
             */
            if(sortingComboBox.getSelectedIndex() == 2) {
                    allExemplars = allExemplars.stream().
                            sorted(Comparator.comparingDouble(e -> exemplarAvgRatingNumberOfRatingsMap.get(e)[1])).collect(Collectors.toList());
                filteredExemplars=filteredExemplars.stream().
                        sorted(Comparator.comparingDouble(e -> exemplarAvgRatingNumberOfRatingsMap.get(e)[1])).collect(Collectors.toList());

                if(sortingComboBox2.getSelectedIndex() == 1) {
                    Collections.reverse(allExemplars);
                    Collections.reverse(filteredExemplars);
                }
            }
            updateTab();
        };
    }

    /**
     * Fetches all the exemplars from the database
     * @param searchTerm a string used to search exemplars by a specific term
     */
    public void fetchExemplars(String searchTerm){
        allExemplars = MainController.exemplars
                .stream()
                .filter(e->e.getName().toLowerCase().contains(searchTerm.toLowerCase()))
                .collect(Collectors.toList());
        allExemplars = allExemplars.stream().sorted(Comparator.comparing(e -> e.getName().toLowerCase())).collect(Collectors.toList());

        for (Exemplar e : allExemplars){
            List<Rating> ratingsForExemplarTmp = MainController.ratings
                    .stream()
                    .filter(r->r.getKey().getExemplar().equals(e))
                    .collect(Collectors.toList());
            java.sql.Date oneWeekAgo = java.sql.Date.valueOf(LocalDate.now().minusDays(7));
            List<Rating> ratingsLastWeek =
                    ratingsForExemplarTmp
                            .stream()
                            .filter(r->r.getSqlDate()!=null)
                            .filter(r-> r.getSqlDate().after(oneWeekAgo))
                            .collect(Collectors.toList());
            if(!ratingsLastWeek.isEmpty()) ratingsForExemplarLastWeek.put(e, ratingsLastWeek);
            allRatingsForExemplar.put(e, ratingsForExemplarTmp);
            int numberOfRatingsForExemplar = ratingsForExemplarTmp.size();
            exemplarAvgRatingNumberOfRatingsMap.put(e, new double[]{MainController.ratings.stream().filter(r->r.getKey().getExemplar().equals(e)).mapToDouble(r->r.getRating()).average().orElse(0),
                    numberOfRatingsForExemplar});
            for(model.entities.Label l : e.getLabels()){
                allLabels.add(l);
            }
        }



    }

    /**
     * Creates a panel for every exemplar with different details and a listener that gets activated
     * by double-clicking the panel and calls the exemplarListener.tabRequested() method that gets processed by
     * the controller
     */
    public void createExemplarPanels(){
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
            panel.setLayout(new GridLayout(4,3));

            JLabel exemplarName = new JLabel(e.getName());
            exemplarName.setFont(new Font("Verdana", Font.BOLD, 14));
            JLabel ratingLabel = new JLabel("Rating:");
            JCheckBox checkBox = new JCheckBox();
            panel.add(exemplarName);
            panel.add(new JLabel(""));
            panel.add(new JLabel(""));
            panel.add(ratingLabel);
            String rating = "";
            rating += exemplarAvgRatingNumberOfRatingsMap.get(e)[0];
            panel.add(new JLabel(rating));
            panel.add(checkBox);
            panel.add(new JLabel("Number of Ratings: "));
            String numberOfRatings =  "";
            numberOfRatings += exemplarAvgRatingNumberOfRatingsMap.get(e)[1];
            panel.add(new JLabel(numberOfRatings));
            panel.add(new JPanel());
            panel.add(new JLabel("Labels: "));
            String labels = "";
            for(model.entities.Label l : e.getLabels()){
                labels +=l.getValue()+", ";
            }
            panel.add(new JLabel(labels));

            panel.setBorder(border);
            panel.setPreferredSize(new Dimension(200, 75));
            selectedExemplarMap.put(e.getName(), checkBox);
            exemplarJPanelMap.put(e, panel);
        }
    }

    void addExemplarPanelsToParentPanel(){
        List<Exemplar> exemplars = allExemplars;
        if(filtered) exemplars = filteredExemplars;
        for(Exemplar e : exemplars){
            exemplarPanelParent.add(exemplarJPanelMap.get(e));
        }
    }
    /**
     * Adds all the components to the panel
     */
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
    /**
     * Initializes the panel with all the buttons used to interact with the exemplar
     */
    void initializeButtonPanel(){
        buttonPanel= new JPanel();
        buttonPanel.setLayout(new GridLayout(1,3));
        String [] sortingComboBoxList = {"Sort alphabetically", "Sort by Rating", "Sort by Number of Ratings"};
        String [] sortingComboBoxList2 = {"ascending", "descending"};
        sortingComboBox = new JComboBox(sortingComboBoxList);
        sortingComboBox2 = new JComboBox(sortingComboBoxList2);
        filterButton = new JButton("Filter by Label");
        JButton openExemplarsButton = new JButton("Open Selected");
        JButton closeLibraryButton = new JButton("Close Library");
        mostRated = new JButton("Most Rated");
        sortingComboBox.addItemListener(sortingListener);
        sortingComboBox2.addItemListener(sortingListener);

        closeLibraryButton.addActionListener(x->closeListener.componentSubmitted(this));
        openExemplarsButton.addActionListener(x->openExemplars());
        mostRated.addActionListener(e->{
            if(ratedLastWeekFilterIsActive) {
                filtered=false;
                ratedLastWeekFilterIsActive = !ratedLastWeekFilterIsActive;
                mostRated.setText("Most Rated");
                updateTab();
                return;
            }
            filteredExemplars = ratingsForExemplarLastWeek.entrySet()
                    .stream()
                    .sorted((e1,e2)-> e1.getValue().size() > e2.getValue().size() ? 1 :0)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
            filtered = true;
            ratedLastWeekFilterIsActive = !ratedLastWeekFilterIsActive;
            mostRated.setText("Reset");
            updateTab();
        });
        filterButton.addActionListener(e->filterLabelPopupFrame.setVisible(true));
        buttonPanel.add(sortingComboBox);
        buttonPanel.add(sortingComboBox2);
        buttonPanel.add(filterButton);
        buttonPanel.add(mostRated);
        buttonPanel.add(openExemplarsButton);
        buttonPanel.add(closeLibraryButton);
        buttonPanel.setBorder(border);
    }

    /**
     * Initializes the frame used to filter the exemplars by label
     */
    void initializeFilterLabelFrame(){
        filterLabelPopupFrame = new FilterLabelPopupFrame(allLabels, "Filter Exemplars");
        filterLabelPopupFrame.setVisible(false);
        filterLabelPopupFrame.setSize(new Dimension(350, 400));
        filterLabelPopupFrame.setLocationRelativeTo(this);

        filterLabelPopupFrame.setListener(labels -> {
            filteredLabels = labels;
            if(filteredLabels.isEmpty()) filtered = false;
            else filterExemplars();
            updateTab();
            filterLabelPopupFrame.setVisible(false);
        });
    }

    /**
     * Filters the exemplars according to the filtered labels
     */
    private void filterExemplars() {
        filteredExemplars = allExemplars.stream().
                filter(e->{
                    List <String> allLabelsOfExemplar = e.getLabels().stream().
                            map(l -> l.getValue().toLowerCase()).collect(Collectors.toList());
                    int i = filteredLabels.size();
                    int j = 0;
                    for(String s: filteredLabels){
                        if(allLabelsOfExemplar.contains(s.toLowerCase())) j++;
                    }
                    return i==j;
                }).collect(Collectors.toList());
        filtered=true;
    }

    /**
     * calls the tabRequested() method of the exemplarListener defined by the controller
     * to open exemplars selected via the exemplar panels checkboxes
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

    public void setCloseListener(ActionWithComponentListener closeListener) {
        this.closeListener = closeListener;
    }

    public void setExemplarListener(NewTabListener exemplarListener) {
        this.exemplarListener = exemplarListener;
    }

    /**
     * Updates the tab after a sorting/filtering action has been performed
     */
    public void updateTab (){
        removeAll();
        exemplarPanelParent.removeAll();
        addExemplarPanelsToParentPanel();
        scrollPane = new JScrollPane((exemplarPanelParent));
        scrollPane.setLayout(new ScrollPaneLayout());
        addComponents();
    }
}
