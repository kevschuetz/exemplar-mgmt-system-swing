package controller;

import model.entities.*;
import model.httpclients.*;
import view.frames.mainFrame.*;
import view.panels.mainFrame.CommunityTab;
import view.panels.mainFrame.exemplarTab.ExemplarTab;
import view.panels.mainFrame.homeTab.HomeTab;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class MainController {
    public static void main(String[] args) {
        new MainController();
    }
    private LoginController loginController;

    private User currentUser;
    private UserClient userClient = new UserClient();
    private ExemplarClient exemplarClient = new ExemplarClient();
    private LabelClient labelClient = new LabelClient();
    private RatingClient ratingClient = new RatingClient();

    private MainFrame mainFrame;
    private HomeTab homeTab;
    private NewExemplarPopupFrame newExemplarPopupFrame;
    private NewLabelPopupFrame newLabelPopupFrame;
    private NewCommunityPopupFrame newCommunityPopupFrame;
    private NewRatingPopupFrame newRatingPopupFrame;
    private AddUserrFrame addContributorFrame;
    /**
     * Initializes the LoginController and starts the login process
     */
    public MainController(){
        initializeMainFrame();
        initializeNewExemplarFrame();
        initializeNewLabelPopupFrame();
        initializeNewRatingPopupFrame();
        initializeAddContributorFrame();

        //login
       loginController = new LoginController();
       loginController.setLoginListener(x->{
           currentUser = loginController.getCurrentUser();
           loginSuccesfull();
       });
       loginController.startLoginProcess();
    }

    /**
     * Method is triggered by LoginController after succesfull login
     */
    void loginSuccesfull(){
        mainFrame.setVisible(true);

        if(currentUser != null){
            mainFrame.setTitle("Welcome, "+currentUser.getUsername()+" !");
            homeTab = new HomeTab(currentUser);
            addListenersToHomeTab();
            mainFrame.addTab("Home", homeTab);

        }
        else mainFrame.setTitle("Welcome!");
    }

    void initializeMainFrame(){
        mainFrame = new MainFrame();
        mainFrame.setVisible(false);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(new Dimension(1300, 1000));
    }
    void initializeNewExemplarFrame(){
        newExemplarPopupFrame = new NewExemplarPopupFrame();
        newExemplarPopupFrame.setVisible(false);
        newExemplarPopupFrame.setSize(new Dimension(350, 200));
        newExemplarPopupFrame.setLocationRelativeTo(mainFrame);
        newExemplarPopupFrame.setListener((s)->{
            boolean ok = verifyExemplarName(s);
            if(ok){
                newExemplarPopupFrame.setVisible(false);
                newExemplarPopupFrame.clean();
                createNewExemplarTab(s);
                homeTab.refresh();
                addListenersToHomeTab();
            }else{
                JOptionPane.showMessageDialog(newExemplarPopupFrame, "Name already taken");
            }
        });
    }

    void initializeNewLabelPopupFrame(){
        newLabelPopupFrame = new NewLabelPopupFrame();
        newLabelPopupFrame.setVisible(false);
        newLabelPopupFrame.setSize(new Dimension(350, 200));
        newLabelPopupFrame.setLocationRelativeTo(mainFrame);
        newLabelPopupFrame.setListener((s)->{
            model.entities.Label label = new model.entities.Label();
            label.setValue(s);
            model.entities.Label added = labelClient.add(label);
            newLabelPopupFrame.getTab().getExemplar().getLabels().add(added);
            exemplarClient.update(newLabelPopupFrame.getTab().getExemplar().getName(),newLabelPopupFrame.getTab().getExemplar());
            newLabelPopupFrame.getTab().refreshInfoPanel();
            newLabelPopupFrame.setVisible(false);
            newLabelPopupFrame.clean();
        });
    }

    void initializeNewRatingPopupFrame(){
        newRatingPopupFrame = new NewRatingPopupFrame();
        newRatingPopupFrame.setVisible(false);
        newRatingPopupFrame.setSize(new Dimension(350, 250));
        newRatingPopupFrame.setLocationRelativeTo(mainFrame);
        newRatingPopupFrame.setListener((i)->{
            Rating r = new Rating();
            RatingPK key = new RatingPK();
            key.setExemplar(newRatingPopupFrame.getTab().getExemplar());
            key.setUser(currentUser);
            r.setKey(key);
            r.setRating(i);
            try {
                Rating newRating = ratingClient.add(r);
                newRatingPopupFrame.setVisible(false);
                if(newRating != null){
                    JOptionPane.showMessageDialog(newRatingPopupFrame, "Thank you for Rating " + newRatingPopupFrame.getTab().getExemplar().getName());
                }
                newRatingPopupFrame.getTab().refreshInfoPanel();
                homeTab.refresh();
                addListenersToHomeTab();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    void initializeAddContributorFrame(){
        addContributorFrame = new AddUserrFrame();
        addContributorFrame.setVisible(false);
        addContributorFrame.setSize(new Dimension(350, 500));
        addContributorFrame.setLocationRelativeTo(mainFrame);
        addContributorFrame.setListener((u)->{
            ExemplarTab tab = addContributorFrame.getTab();
            Exemplar e = tab.getExemplar();
            e.getContributors().add(u);
            Exemplar updated = exemplarClient.update(e.getName(), e);
            tab.refreshInfoPanel();
            if (updated != null) JOptionPane.showMessageDialog(tab, "Adding succesfull");
        });
    }
    void addListenersToHomeTab(){
        homeTab.setUpdateUserListener((u)-> {
            try {
                User updated = userClient.update(u.getUsername(), u);
                if (updated != null) {
                    this.currentUser = updated;
                    homeTab.setUser(updated);
                    JOptionPane.showMessageDialog(homeTab.getProfilePanel(), "Update successfull");
                }else JOptionPane.showMessageDialog(homeTab.getProfilePanel(), "Update failed");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        homeTab.setOpenExemplarListener((list)->{
            try {
                for(String s : list){
                    Exemplar e = exemplarClient.get(s);
                    if(e != null){
                        boolean editable = false;
                        if(e.getCreator() == null) editable = false;
                        else editable = e.getCreator().equals(currentUser) ? true : false;
                        if(!editable) if(e.getContributors().contains(currentUser)) editable = true;
                        ExemplarTab newExemplarTab = new ExemplarTab(e, editable);
                        addListenersToExemplarTab(newExemplarTab);
                        mainFrame.addTab(s,newExemplarTab);
                    }
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        });

        homeTab.setCreateExemplarListener((e)->{
            newExemplarPopupFrame.setVisible(true);
        });

        homeTab.setOpenCommunityListener((list)->{
            try {
                CommunityClient client = new CommunityClient();
                for(String s : list){
                    Community c = client.get(s);
                    if(c != null){
                        CommunityTab tab = new CommunityTab(c);
                        tab.setCloseListener((x)->mainFrame.removeTab(x));
                        mainFrame.addTab(s,tab);
                    }
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        });

        homeTab.setDeleteUserListener((user)->{
            try{
                userClient.delete(user.getUsername());
                mainFrame.setVisible(false);
                loginController.startLoginProcess();
            }catch(Exception e){
                e.printStackTrace();
            }
        });
    }

    void createNewExemplarTab(String s){
        Exemplar e = new Exemplar();
        e.setName(s);
        e.setCreator(currentUser);
        e.setLabels(new ArrayList<>());
        e.setContributors(new ArrayList<>());
        try {
            exemplarClient.add(e);
            ExemplarTab newExemplarTab = new ExemplarTab(e, true);
            addListenersToExemplarTab(newExemplarTab);
            mainFrame.addTab(s,newExemplarTab);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
    }

    void addListenersToExemplarTab(ExemplarTab newExemplarTab){
        newExemplarTab.setCloseListener((c)->{
            ExemplarTab tab = (ExemplarTab)c;
            JButton updateButton = tab.getUpdateButton();
            updateButton.doClick();
            mainFrame.removeTab(c);
        });
        newExemplarTab.setUpdateExemplarListener((exemplar)->{
            exemplarClient.update(exemplar.getName(), exemplar);
        });
        newExemplarTab.setDeleteExemplarListener((id, tab)->{
            try {
                exemplarClient.delete(id);
                mainFrame.removeTab(tab);
                homeTab.refresh();
                addListenersToHomeTab();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        newExemplarTab.setAddLabelListener((tab)->{
            newLabelPopupFrame.setVisible(true);
            newLabelPopupFrame.setTab(tab);
        });

        newExemplarTab.setRatingListener((t)->{
            newRatingPopupFrame.setTab(t);
            newRatingPopupFrame.setTitle(t.getExemplar().getName());
            newRatingPopupFrame.setVisible(true);
        });

        newExemplarTab.setContributorListener((t)->{
            addContributorFrame.setTab(t);
            addContributorFrame.setTitle(t.getExemplar().getName());
            addContributorFrame.setVisible(true);
        });
    }



    boolean verifyExemplarName(String s){
        try{
            Exemplar exists = exemplarClient.get(s);
            if(exists == null) return true;
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }



}
