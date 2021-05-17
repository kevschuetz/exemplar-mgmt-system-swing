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
    private JScrollPane scrollPane;
    Border border = BorderFactory.createEtchedBorder(Color.GRAY, Color.BLACK);
    private NewTabListener exemplarListener;
    private Map<String, JCheckBox> selectedExemplarMap = new HashMap<>();
    JPanel buttonPanel;
    private CloseTabListener closeListener;



    public ExemplarLibraryTab(){
        scrollPane = new JScrollPane(exemplarPanelParent);
        scrollPane.setLayout(new ScrollPaneLayout());

        fetchExemplars();

        exemplarPanelParent.setLayout(new GridLayout(allExemplars.size()+1, 1));
        addExemplars();

        initializeButtonPanel();
        addComponents();
    }

    public void fetchExemplars(){
        try {
            allExemplars = new ExemplarClient().getAll();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void addExemplars(){
        int i = 0;
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
            if(i%2==0)checkBox.setBackground(Color.LIGHT_GRAY);
            panel.add(name);
            panel.add(exemplarName);
            panel.add(new JLabel(""));
            panel.add(ratingLabel);
            String rating = "";
            rating += client.getAvgRatingForExemplar(e.getName());
            panel.add(new JLabel(rating));
            panel.add(checkBox);
            panel.setBorder(border);
            panel.setPreferredSize(new Dimension(200, 50));
            if(i%2==0)panel.setBackground(Color.LIGHT_GRAY);
            selectedExemplarMap.put(e.getName(), checkBox);
            exemplarPanelParent.add(panel);
            i++;
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
        JComboBox sortingComboBox = new JComboBox(sortingComboBoxList);
        JComboBox sortingComboBox2 = new JComboBox(sortingComboBoxList2);
        JButton filterButton = new JButton("Filter by Label");
        JButton openExemplarsButton = new JButton("Open Selected");
        JButton closeLibraryButton = new JButton("Close Library");
        sortingComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent event) {
                RatingClient ratingClient = new RatingClient();
                if(sortingComboBox.getSelectedIndex() == 1) {
                    if(sortingComboBox2.getSelectedIndex() == 0) {
                        allExemplars = allExemplars.stream().
                                sorted(Comparator.comparingDouble(e -> ratingClient.getAvgRatingForExemplar(e.getName()))).collect(Collectors.toList());
                    }else
                        allExemplars = allExemplars.stream().
                                sorted(Comparator.comparingDouble(e -> ratingClient.getAvgRatingForExemplar(e.getName()))).collect(Collectors.toList());
                }
                updateTab();

            }

        });

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
        addExemplars();
        scrollPane = new JScrollPane((exemplarPanelParent));
        scrollPane.setLayout(new ScrollPaneLayout());
        GridBagConstraints c = new GridBagConstraints();
        addComponents();
    }
}
