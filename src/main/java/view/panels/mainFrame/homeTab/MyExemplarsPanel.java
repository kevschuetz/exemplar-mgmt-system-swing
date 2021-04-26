package view.panels.mainFrame.homeTab;

import model.entities.Exemplar;
import model.entities.User;
import model.httpclients.ExemplarClient;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class MyExemplarsPanel extends JPanel {
    private User user;
    JPanel exemplarPanelParent = new JPanel();
    private List<Exemplar> myExemplars;
    private List<JPanel> myExemplarPanels = new ArrayList<>();
    private JScrollPane scrollPane;
    Border border = BorderFactory.createEtchedBorder(Color.GRAY, Color.BLACK);

    public MyExemplarsPanel(User user){
        this.user=user;
        setLayout(new GridLayout(1,1));
        scrollPane = new JScrollPane(exemplarPanelParent);
        scrollPane.setLayout(new ScrollPaneLayout());

        fetchExemplars();

        exemplarPanelParent.setLayout(new GridLayout(myExemplars.size(), 1));

        addExemplars();
        add(scrollPane);
    }


    public void fetchExemplars(){
        myExemplars = new ExemplarClient().getExemplarForCreator(user.getUsername());
    }

    public void addExemplars(){
        for(Exemplar e : myExemplars){
            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(2,3));
            JLabel name = new JLabel("Name: ");
            JLabel exemplarName = new JLabel(e.getName());
            JButton showButton = new JButton("OPEN");
            JLabel ratingLabel = new JLabel("Rating:");
            panel.add(name);
            panel.add(exemplarName);
            panel.add(new JLabel(""));
            panel.add(ratingLabel);
            panel.add(new JLabel(""));
            panel.add(showButton);
            panel.setBorder(border);
            panel.setPreferredSize(new Dimension(200, 50));
            exemplarPanelParent.add(panel);
            myExemplarPanels.add(panel);
        }
    }
}
