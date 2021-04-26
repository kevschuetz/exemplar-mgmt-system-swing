package view.panels.mainFrame;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.entities.Exemplar;
import model.entities.Label;
import model.entities.User;
import view.listeners.mainframe.CloseTabListener;

import javax.swing.*;

public class ExemplarTab extends JPanel {
    private Exemplar exemplar;
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
    private JButton closeButton = new JButton("Close Tab");
    private CloseTabListener closeListener;

    public ExemplarTab(Exemplar exemplar){
        //only temporary
        this.exemplar = exemplar;
        JLabel label = new JLabel("exemplar details");
        add(label);

        addComponents();
        addActionListener();


    }

    void addComponents(){
        add(closeButton);
    }
    void addActionListener(){
        closeButton.addActionListener((x)->closeListener.shutdownRequested(this));
    }

    public void setCloseListener(CloseTabListener closeListener) {
        this.closeListener = closeListener;
    }
}
