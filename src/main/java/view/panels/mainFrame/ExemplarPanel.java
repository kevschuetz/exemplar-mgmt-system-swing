package view.panels.mainFrame;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.entities.Exemplar;
import model.entities.Label;
import model.entities.User;

import javax.swing.*;

public class ExemplarPanel extends JPanel {
    private Exemplar currentExemplar;
    private int avgRating;
    private Label[] labels;
    private User[] contributors;
    private ObjectMapper mapper;

    private JLabel nameLabel;
    private JLabel descriptionLabel;
    private JTextArea descriptionArea;
    private JLabel solutionLabel;
    private JTextArea solutionArea;
    private JLabel avgRatingView;
    private Label[] labelView;
    private JSlider ratingSlider;
    private JButton submittRatingButton;

    public ExemplarPanel(String exemplarId){
        //currentExemplar = mapper.readValue(ExemplarClient.get(id), Exemplar.class);
        if (currentExemplar != null) fetchData(exemplarId);

    }

    public void fetchData(String exemplarId){

    }

    public void paint(){

    }


}
