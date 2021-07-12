package view.frames.mainFrame;


import model.entities.Label;
import view.listeners.mainframe.FilterByLabelListener;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Set;

/**
 * Lists all the labels with checkboxes used to filter the exemplars by labels
 */
public class FilterLabelPopupFrame extends JFrame {
    private JButton button = new JButton("Filter");

    private JPanel parentPanel = new JPanel();
    private JScrollPane scrollPane;

    private Set<model.entities.Label> labelSet;
    private java.util.List<LabelPanel> labelPanels = new ArrayList<>();
    private java.util.List<String> selectedLabels = new ArrayList<>();
    private FilterByLabelListener listener;

    public FilterLabelPopupFrame (Set<model.entities.Label> labelSet, String title){
        setTitle(title);
        this.labelSet=labelSet;

        initializeLabelPanels();
        addLabelPanelsToParentPanel();
        scrollPane = new JScrollPane((parentPanel));

        parentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Filter by label"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        addComponents();

        button.addActionListener(x -> {
            selectedLabels = new ArrayList<>();
            for(LabelPanel panel : labelPanels){
                if(panel.isSelected()) selectedLabels.add(panel.getLabel().getValue());
            }
            listener.filter(selectedLabels);
        });
        getRootPane().setDefaultButton(button);
    }

    private void addComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 0.9;
        c.gridx = 0;
        c.gridy = 0;
        add(scrollPane, c);

        c.gridy=1;
        c.weighty = 0.1;
        add(button,c);
    }

    void initializeLabelPanels(){
        for(model.entities.Label l : labelSet){
            labelPanels.add(new LabelPanel(l));
        }
    }

    void addLabelPanelsToParentPanel(){
        parentPanel.setLayout(new GridLayout(labelPanels.size(), 1));
        for(LabelPanel l : labelPanels){
            parentPanel.add(l);
        }
    }

    public void setListener(FilterByLabelListener listener) {
        this.listener = listener;
    }
}

class LabelPanel extends JPanel{
    private JCheckBox checkBox = new JCheckBox();
    private model.entities.Label label;

    public LabelPanel(model.entities.Label label){
        this.label = label;
        setLayout(new GridLayout(1,2));
        add(new JLabel(label.getValue()));
        add(checkBox);
    }

    public JCheckBox getCheckBox() {
        return checkBox;
    }

    public Label getLabel() {
        return label;
    }

    public boolean isSelected(){
        return checkBox.isSelected();
    }
}


