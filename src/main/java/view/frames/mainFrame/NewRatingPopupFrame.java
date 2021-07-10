package view.frames.mainFrame;

import model.entities.Exemplar;
import view.listeners.mainframe.NewRatingListener;
import view.panels.mainFrame.exemplarTab.ExemplarTab;

import javax.swing.*;
import java.awt.*;

import static javax.swing.SwingConstants.HORIZONTAL;

/**
 * Frame that has a slider to adjust the rating of an exemplar
 */
public class NewRatingPopupFrame extends JFrame {
    private Exemplar exemplar;
    private ExemplarTab tab;
    private JLabel label = new JLabel("Please adjust the Rating");
    private JPanel parentPanel = new JPanel();

    private final static int RATING_MIN = 0;
    private final static int RATING_MAX = 5;
    private final static int RATING_INI = 3;
    private JSlider slider = new JSlider(HORIZONTAL, RATING_MIN, RATING_MAX, RATING_INI);

    private JButton button = new JButton("Submit Rating");

    private NewRatingListener listener;

    public NewRatingPopupFrame(){
        slider.setMajorTickSpacing(1);
        setLayout(new GridLayout(1,1));

        button.addActionListener(x->listener.RatingSubmitted(slider.getValue()));

        slider.setPaintTicks(true);
        slider.setPaintLabels(true);

        parentPanel.setLayout(new GridLayout(3,1));
        parentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("New Rating"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        parentPanel.add(label);
        parentPanel.add(slider);
        parentPanel.add(button);

        add(parentPanel);
    }

    public Exemplar getExemplar() {
        return exemplar;
    }

    public void setExemplar(Exemplar exemplar) {
        this.exemplar = exemplar;
    }

    public void setListener(NewRatingListener listener) {
        this.listener = listener;
    }

    public ExemplarTab getTab() {
        return tab;
    }

    public void setTab(ExemplarTab tab) {
        this.tab = tab;
    }
}
