package view.panels.mainFrame;

import model.entities.Exemplar;
import model.httpclients.ExemplarClient;
import model.httpclients.RatingClient;
import view.listeners.mainframe.CloseTabListener;
import view.listeners.mainframe.homeTab.NewTabListener;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;


public class ExemplarLibraryTab extends JPanel{

    JPanel exemplarPanelParent = new JPanel();
    private List<Exemplar> allExemplars;
    private Map<Exemplar, Double> ratingMap = new HashMap<>();
    private Map<Exemplar, JPanel> exemplarJPanelMap = new HashMap<>();
    private Map<String, JCheckBox> selectedExemplarMap = new HashMap<>();

    private JScrollPane scrollPane;
    Border border = BorderFactory.createEtchedBorder(Color.GRAY, Color.BLACK);
    private NewTabListener exemplarListener;
    JPanel buttonPanel;
    private CloseTabListener closeListener;

    private ItemListener sortingListener;
    JComboBox sortingComboBox;
    JComboBox sortingComboBox2;

    public ExemplarLibraryTab(){
        fetchExemplars();
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
                RatingClient ratingClient = new RatingClient();
                if(sortingComboBox.getSelectedIndex() == 1) {
                    if(sortingComboBox2.getSelectedIndex() == 0) {
                        allExemplars = allExemplars.stream().
                                sorted(Comparator.comparingDouble(e -> ratingMap.get(e))).collect(Collectors.toList());
                        Collections.reverse(allExemplars);
                    }else
                        allExemplars = allExemplars.stream().
                                sorted(Comparator.comparingDouble(e -> ratingMap.get(e))).collect(Collectors.toList());
                }
                updateTab();
            }
        };
    }

    public void fetchExemplars(){
        try {
            ExemplarClient exemplarClient = new ExemplarClient();
            RatingClient ratingClient = new RatingClient();
            allExemplars = exemplarClient.getAll();
            for (Exemplar e : allExemplars){
                ratingMap.put(e, ratingClient.getAvgRatingForExemplar(e.getName()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
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

        closeLibraryButton.addActionListener((x)->closeListener.shutdownRequested(this));
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

    public void setCloseListener(CloseTabListener closeListener) {
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
