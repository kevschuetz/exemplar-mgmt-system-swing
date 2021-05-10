package view.panels.mainFrame;

import model.entities.Exemplar;
import model.httpclients.ExemplarClient;

import javax.swing.*;
import java.io.IOException;
import java.util.List;

public class AllExemplarsPanel extends JPanel {
    private JScrollPane scrollPane;
    private JList exemplarList;
    DefaultListModel<String> listModel;


    private List<Exemplar> exemplars;
    private JButton showExemplarButton;
    private ExemplarClient exemplarClient;

    private JButton closeButton = new JButton("Close Library");

    public AllExemplarsPanel()  {

        exemplarClient = new ExemplarClient();

        try {
            exemplars = exemplarClient.getAll();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        listModel = new DefaultListModel<>();
        for(Exemplar e : exemplars){
            listModel.addElement(e.getName());
        }
        exemplarList = new JList(listModel);

        scrollPane = new JScrollPane(exemplarList);
        add(scrollPane);

        showExemplarButton = new JButton("show");
        add(showExemplarButton);
    }

}
