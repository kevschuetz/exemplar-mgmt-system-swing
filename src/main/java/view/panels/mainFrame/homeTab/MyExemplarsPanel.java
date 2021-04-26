package view.panels.mainFrame.homeTab;

import model.entities.Exemplar;
import model.entities.User;
import model.httpclients.ExemplarClient;
import view.listeners.mainframe.homeTab.OpenExemplarListener;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.*;
import java.util.List;


public class MyExemplarsPanel extends JPanel {
    private User user;
    JPanel exemplarPanelParent = new JPanel();
    private List<Exemplar> myExemplars;
    private JScrollPane scrollPane;
    Border border = BorderFactory.createEtchedBorder(Color.GRAY, Color.BLACK);
    private OpenExemplarListener exemplarListener;
    private Map<String, JCheckBox> selectedExemplarMap = new HashMap<>();
    JPanel buttonPanel;

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


    public void fetchExemplars(){
        myExemplars = new ExemplarClient().getExemplarForCreator(user.getUsername());
    }

    public void addExemplarsToScrollPane(){
        for(Exemplar e : myExemplars){
            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(2,3));
            JLabel name = new JLabel("Name: ");
            JLabel exemplarName = new JLabel(e.getName());
            JLabel ratingLabel = new JLabel("Rating:");
            JCheckBox checkBox = new JCheckBox("select");
            panel.add(name);
            panel.add(exemplarName);
            panel.add(new JLabel(""));
            panel.add(ratingLabel);
            panel.add(new JLabel(""));
            panel.add(checkBox);
            panel.setBorder(border);
            panel.setPreferredSize(new Dimension(200, 50));
            selectedExemplarMap.put(e.getName(), checkBox);
            exemplarPanelParent.add(panel);
        }

    }

    void initializeButtonPanel(){
        buttonPanel= new JPanel();
        buttonPanel.setLayout(new GridLayout(1,3));
        JButton openExemplarsButton = new JButton("Open Selected");
        JButton createExemplarButton = new JButton("Create New");
        JButton searchAllButton = new JButton("Search All");
        buttonPanel.add(openExemplarsButton);
        openExemplarsButton.addActionListener((x)->openExemplars());
        buttonPanel.add(createExemplarButton);
        buttonPanel.add(searchAllButton);
        buttonPanel.setBorder(border);
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

    void openExemplars(){
        Set<Map.Entry<String, JCheckBox>> entrySet = selectedExemplarMap.entrySet();
        List<String> selectedExemplars = new ArrayList<>();
        for(Map.Entry<String, JCheckBox> e: entrySet){
            if(e.getValue().isSelected()) {
                selectedExemplars.add(e.getKey());
                e.getValue().doClick();
            }
        }
        exemplarListener.exemplarRequested(selectedExemplars);
    }

    public void setExemplarListener(OpenExemplarListener exemplarListener) {
        this.exemplarListener = exemplarListener;
    }
}
