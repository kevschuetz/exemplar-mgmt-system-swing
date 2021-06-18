package controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.entities.Community;
import model.entities.Exemplar;
import model.entities.Label;
import model.entities.User;
import model.httpclients.CommunityClient;
import model.httpclients.ExemplarClient;
import model.httpclients.LabelClient;
import model.httpclients.UserClient;

import model.entities.*;
import model.httpclients.*;
import view.frames.mainFrame.*;
import view.listeners.mainframe.ActionWithComponentListener;
import view.listeners.mainframe.homeTab.NewTabListener;
import view.panels.mainFrame.CommunityTab;
import view.panels.mainFrame.ContributorLibraryTab;
import view.panels.mainFrame.ExemplarLibraryTab;
import view.panels.mainFrame.contributorTab.ContributorTab;
import view.panels.mainFrame.exemplarTab.ExemplarTab;
import view.panels.mainFrame.homeTab.HomeTab;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainController implements Runnable{
    public static void main(String[] args) {
        final MainController[] controller = {new MainController()};
        controller[0].setLogoutListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller[0].mainFrame.dispose();
                controller[0] = new MainController();
                controller[0].setLogoutListener(this);
            }
        });

    }
    private LoginController loginController;

    private User currentUser;
    private UserClient userClient = new UserClient();
    private ExemplarClient exemplarClient = new ExemplarClient();
    private CommunityClient communityClient = new CommunityClient();
    private LabelClient labelClient = new LabelClient();
    private RatingClient ratingClient = new RatingClient();

    private MainFrame mainFrame;
    private HomeTab homeTab;
    private NewExemplarPopupFrame newExemplarPopupFrame;
    private NewLabelPopupFrame newLabelPopupFrame;
    private NewCommunityPopupFrame newCommunityPopupFrame;
    private NewRatingPopupFrame newRatingPopupFrame;
    private AddUserrFrame addContributorFrame;
    private ExemplarLibraryTab initialExemplarLibraryTab;
    private ContributorLibraryTab initialContributorLibraryTab;
    boolean librarysLoaded = false;

    private ActionListener logoutListener;
    /**
     * Initializes the LoginController and starts the login process
     */
    public MainController(){
        initializeMainFrame();
        initializeNewExemplarFrame();
        initializeNewCommunityFrame();
        initializeNewLabelPopupFrame();
        initializeNewRatingPopupFrame();
        initializeAddContributorFrame();

        //login
       loginController = new LoginController();
       loginController.setLoginListener(x->{
           currentUser = loginController.getCurrentUser();
           loginSuccesfull();
       });
       Thread thread = new Thread(this);
       thread.start();
       loginController.startLoginProcess();
    }

    /**
     * Method is triggered by LoginController after succesfull login. Opens new HomeTab if User is not a guest.
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

        addInitialLibrarys();

    }

    void addInitialLibrarys(){
        if(librarysLoaded){
            mainFrame.addTab("Exemplar Library", initialExemplarLibraryTab);
            mainFrame.addTab("Contributor Library", initialContributorLibraryTab);
        }else{
            Thread waitingThread = new Thread(()-> {
                try {
                    Thread.sleep(5000);
                    addInitialLibrarys();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            waitingThread.start();
        }
    }

    void initializeMainFrame(){
        mainFrame = new MainFrame();
        mainFrame.setVisible(false);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(new Dimension(1300, 1000));
        mainFrame.setExemplarButtonListener(getOpenExemplarLibraryListener(false));
        mainFrame.setContributorButtonListener(getOpenContributorLibraryListener(false));
        mainFrame.setSearchExemplarListener(getOpenExemplarLibraryListener(true));
        mainFrame.setSearchContributorListener(getOpenContributorLibraryListener(true));
        mainFrame.setImportListener((path)->{
            Path pathTo = Path.of(path);
            try {
                String exemplarJson = Files.readString(pathTo);
                ObjectMapper mapper = new ObjectMapper();
                Exemplar importedExemplar = mapper.readValue(exemplarJson, Exemplar.class);
                importedExemplar.setCreator(currentUser);
                importedExemplar.setContributors(new ArrayList<User>());
                for (Label l : importedExemplar.getLabels()){
                    labelClient.add(l);
                }
                Exemplar response = exemplarClient.add(importedExemplar);
                if(response != null){
                    addExemplarTabToMainframe(response.getName());
                    refreshHomeTab();
                }else{
                    JOptionPane.showMessageDialog(mainFrame, "An Exemplar with the name " + importedExemplar.getName() + " already exists. Choose another one and try again.");
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });

        mainFrame.setCreateExemplarListener((e)->newExemplarPopupFrame.setVisible(true));
    }

    /**
     * Initializes the Frame where an Exemplar-Name can be entered for creating a given exemplar
     */
    void initializeNewExemplarFrame(){
        newExemplarPopupFrame = new NewExemplarPopupFrame();
        newExemplarPopupFrame.setVisible(false);
        newExemplarPopupFrame.setSize(new Dimension(350, 200));
        newExemplarPopupFrame.setLocationRelativeTo(mainFrame);
        /**
         * Sets the listener of the Frame to check if the entered name is available (verifyExemplarName())and if so,
         * creates the exemplar and opens it in a new Tab (createNewExemplarAndInitializeTab()).
         * Refreshes hometab afterwards to include newly created exemplar.
         */
        newExemplarPopupFrame.setListener((s)->{
            boolean ok = verifyExemplarName(s);
            if(ok){
                newExemplarPopupFrame.setVisible(false);
                newExemplarPopupFrame.clean();
                createNewExemplarAndInitializeTab(s);
                refreshHomeTab();
            }else{
                JOptionPane.showMessageDialog(newExemplarPopupFrame, "Name already taken");
            }
        });
    }

    void initializeNewCommunityFrame(){
        newCommunityPopupFrame = new NewCommunityPopupFrame();
        newCommunityPopupFrame.setVisible(false);
        newCommunityPopupFrame.setSize(new Dimension(350, 200));
        newCommunityPopupFrame.setLocationRelativeTo(mainFrame);
        /**
         * Sets the listener of the Frame to check if the entered name is available (verifyCommunityName())and if so,
         * creates the community and opens it in a new Tab (createNewCommunityAndInitializeTab()).
         * Refreshes hometab afterwards to include newly created exemplar.
         */
        newCommunityPopupFrame.setListener((s)->{
            boolean ok = verifyCommunityName(s);
            if(ok){
                newCommunityPopupFrame.setVisible(false);
                newCommunityPopupFrame.clean();
                createNewCommunityAndInitializeTab(s);
                refreshHomeTab();
            }else{
                JOptionPane.showMessageDialog(newCommunityPopupFrame, "Name already taken");
            }
        });
    }

    /**
     * Initializes the frame that can be used to enter a value for a label meant to be assigned to an exemplar.
     * If the add Label button is clicked, the tab gets referred inside the frame. (see addListenersExemplarTab())
     */
    void initializeNewLabelPopupFrame(){
        newLabelPopupFrame = new NewLabelPopupFrame();
        newLabelPopupFrame.setVisible(false);
        newLabelPopupFrame.setSize(new Dimension(350, 200));
        newLabelPopupFrame.setLocationRelativeTo(mainFrame);
        /**
         * Sets the listener to take the entered value, initialize a new Label with it, add it to the database and to the exemplar.
         * Updates the exemplar in the database afterwards to reflect the changes.
         * Refreshes the infoPanel of the tab afterwards.
         */
        newLabelPopupFrame.setListener((s)->{
            model.entities.Label label = new model.entities.Label();
            label.setValue(s.toLowerCase(Locale.ROOT));
            model.entities.Label added = labelClient.add(label);
            if(!newLabelPopupFrame.getTab().getExemplar().getLabels().contains(added)){
                newLabelPopupFrame.getTab().getExemplar().getLabels().add(added);
                exemplarClient.update(newLabelPopupFrame.getTab().getExemplar().getName(),newLabelPopupFrame.getTab().getExemplar());
                newLabelPopupFrame.getTab().refreshInfoPanel();
            }
            newLabelPopupFrame.setVisible(false);
            newLabelPopupFrame.clean();
        });
    }

    /**
     * Initializes the RatingFrame to assign Ratings to Exemplars.
     */
    void initializeNewRatingPopupFrame(){
        newRatingPopupFrame = new NewRatingPopupFrame();
        newRatingPopupFrame.setVisible(false);
        newRatingPopupFrame.setSize(new Dimension(350, 250));
        newRatingPopupFrame.setLocationRelativeTo(mainFrame);
        /**
         * Sets the listener of the frame to initialize a new Rating with the selected value i, the Exemplar according to
         * the ExemplarTab which gets referenced (see addListenersToExemplarTab()) when clicking the add Rating button
         * inside the Exemplar Tab and the current user. Persists the rating in the database and refreshes the InfoPanel
         * of the ExemplarTab to reflect the rating change.
         */
        newRatingPopupFrame.setListener((i)->{
            Rating r = new Rating();
            RatingPK key = new RatingPK();
            key.setExemplar(newRatingPopupFrame.getTab().getExemplar());
            key.setUser(currentUser);
            r.setKey(key);
            r.setRating(i);
            java.sql.Date sqlDate = java.sql.Date.valueOf(LocalDate.now());
            r.setSqlDate(sqlDate);
            try {
                Rating newRating = ratingClient.add(r);
                newRatingPopupFrame.setVisible(false);
                if(newRating != null){
                    JOptionPane.showMessageDialog(newRatingPopupFrame, "Thank you for Rating " + newRatingPopupFrame.getTab().getExemplar().getName());
                }
                newRatingPopupFrame.getTab().refreshInfoPanel();
                refreshHomeTab();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Initializes the Frame used to add Contributors to an Exemplar
     */
    void initializeAddContributorFrame(){
        addContributorFrame = new AddUserrFrame();
        addContributorFrame.setVisible(false);
        addContributorFrame.setSize(new Dimension(350, 500));
        addContributorFrame.setLocationRelativeTo(mainFrame);
        /**
         * Sets the listener of the frame to check if the selected user has contributor status,
         * adds the contributor to the exemplar and persists the update.
         * Refreshes the info panel from the ExemplarTab to reflect changes.
         */
        addContributorFrame.setListener((u)->{
            ExemplarTab tab = addContributorFrame.getTab();
            if(u.getIsContributor()==1){
                Exemplar e = tab.getExemplar();
                if(!e.getContributors().contains(u)){
                    e.getContributors().add(u);
                    Exemplar updated = exemplarClient.update(e.getName(), e);
                    tab.refreshInfoPanel();
                    if (updated != null) JOptionPane.showMessageDialog(tab, "Adding succesfull");
                }else JOptionPane.showMessageDialog(tab, "User already a contributor");
            }else{
                JOptionPane.showMessageDialog(tab, u.getUsername() + " does not have Contributor status");
            }

        });
    }

    /**
     * Creates an ActionListener that opens a new ExemplarLibrary in a new tab and selects that tab
     * @return the listener created
     */
    ActionListener getOpenExemplarLibraryListener(boolean searchableByMainframe){
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ExemplarLibraryTab exemplarLibrary;
                if(searchableByMainframe) {
                    exemplarLibrary = new ExemplarLibraryTab(mainFrame.getSearchTerm());
                    mainFrame.referenceOpenTab(exemplarLibrary);
                }
                else exemplarLibrary = new ExemplarLibraryTab("");

                addListenersToExemplarLibrary(exemplarLibrary);

                if (searchableByMainframe){
                    for(JComponent c : mainFrame.getOpenSearchTabs()){
                        mainFrame.removeTab(c);
                    }
                    java.util.List<JComponent> list = new ArrayList<>();
                    list.add(exemplarLibrary);
                    mainFrame.setOpenSearchTabs(list);
                    mainFrame.addTab("Search Exemplars", exemplarLibrary);
                }else mainFrame.addTab("Exemplar Library",exemplarLibrary);


                mainFrame.setLastTabSelected();
            }
        };
    }

    void addListenersToExemplarLibrary(ExemplarLibraryTab exemplarLibrary){
        exemplarLibrary.setCloseListener(c -> mainFrame.removeTab(c));
        exemplarLibrary.setExemplarListener(selectedEntities -> {
            for(String e1 : selectedEntities){
                addExemplarTabToMainframe(e1);
            }
        });
    }

    /**
     * Creates an ActionListener that opens a new contributorLibrary in a seperate tab and selects that tab
     * @return the listener created
     */
    ActionListener getOpenContributorLibraryListener(boolean searchableByMainFrame){
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ContributorLibraryTab contributorLibrary;

                if(searchableByMainFrame) {
                    contributorLibrary = new ContributorLibraryTab(mainFrame.getSearchTerm());
                }
                else contributorLibrary = new ContributorLibraryTab("");

                if(searchableByMainFrame){
                    for(JComponent c : mainFrame.getOpenSearchTabs()){
                        mainFrame.removeTab(c);
                    }
                    java.util.List<JComponent> list = new ArrayList<>();
                    list.add(contributorLibrary);
                    mainFrame.setOpenSearchTabs(list);
                    mainFrame.addTab("Search Contributors", contributorLibrary);
                } else mainFrame.addTab("Contributor Library", contributorLibrary);

                mainFrame.setLastTabSelected();
            }
        };
    }

    void addListenersToContributorLibrary(ContributorLibraryTab contributorLibrary){
        contributorLibrary.setCloseListener(c -> mainFrame.removeTab(c));
        contributorLibrary.setContributorListener(selectedEntities -> {
            for(String e1 : selectedEntities){
                try {
                    createNewContributorTab(e1);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        });
    }
    /**
     * Adds all the listeners required for the home tab
     */
    void addListenersToHomeTab(){
        /**
         * Takes the UserEvent created by the HomeTab and updates the information in the database
         */
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

        /**
         * Takes a list with exemplar names and opens a ExemplarTab for every exemplar in the list (identified by name)
         */
        homeTab.setOpenExemplarListener((list)->{
                for(String s : list){
                    addExemplarTabToMainframe(s);
                }
        });

        /**
         * Makes the NewExemplarPopupFrame visible to enter name
         */
        homeTab.setCreateExemplarListener((e)->{
            newExemplarPopupFrame.setVisible(true);
        });

        /**
         * Fetches all communities from the database according to their names in the list and opens
         * a CommunityTab for each of them
         */
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

        /**
         * Deletes the user from the database and calls logout().
         *
         */
        homeTab.setDeleteUserListener((user)->{
            try{
                userClient.delete(user.getUsername());
                logout();
            }catch(Exception e){
                e.printStackTrace();
            }
        });

        homeTab.setCreateExemplarLibraryListener(getOpenExemplarLibraryListener(false));

        homeTab.setCreateContributorLibraryListener(getOpenContributorLibraryListener(false));

    }

    void refreshHomeTab(){
        homeTab.refresh();
        addListenersToHomeTab();
    }

    /**
     *Creates a new exemplar with the given name (if available) and opens a tab with it
     */
    void createNewExemplarAndInitializeTab(String s){
        Exemplar e = new Exemplar();
        e.setName(s);
        e.setProblem("You can modify your exemplar by filling in the description and solution and clicking the update button or closing the tab. \n" +
                "Also consider adding labels so that interested users can find your exemplar more easily. \n\n" +
                "If you want to add contributors to your exemplar click the 'Add Contributor' button to search the User base. \n" +
                "Users must have the Contributor status if you want to add them. \nContributors to this exemplar have all rights. \n Have fun!");
        e.setCreator(currentUser);
        e.setLabels(new ArrayList<>());
        e.setContributors(new ArrayList<>());
        try {
            exemplarClient.add(e);
            ExemplarTab newExemplarTab = new ExemplarTab(e, true, currentUser);
            addListenersToExemplarTab(newExemplarTab);
            mainFrame.addTab(s,newExemplarTab);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
    }

    /**
     *Creates a new community with the given name (if available) and opens a tab with it
     */
    void createNewCommunityAndInitializeTab(String s){
        Community c = new Community();
        c.setName(s);
        c.setCreator(currentUser);
        c.setMembers(new ArrayList<>());
        try {
            communityClient.add(c);
            CommunityTab newCommunityTab = new CommunityTab(c);
            addListenersToCommunityTab(newCommunityTab);
            mainFrame.addTab(s,newCommunityTab);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
    }

    /**
     * Fetches the user identified by the username and opens a new contributor tab for the user
     * @param username the id of the user
     * @throws IOException
     * @throws InterruptedException
     */
    void createNewContributorTab(String username) throws IOException, InterruptedException {
        User contributor = userClient.get(username);
        ContributorTab newContributorTab = new ContributorTab(contributor);
        newContributorTab.setCloseListener(new ActionWithComponentListener(){
            @Override
            public void componentSubmitted(Component c){
                mainFrame.removeTab(c);
            }
        });
        newContributorTab.setExemplarListener(new NewTabListener() {
            @Override
            public void tabRequested(List<String> selectedEntities) {
                for(String e : selectedEntities){
                    addExemplarTabToMainframe(e);
                }
            }
        });
        mainFrame.addTab(username,newContributorTab);
    }

    /**
     * Fetches the exemplar with the given id, initializes a ExemplarTab with it and adds the tab to the mainFrame
     * @param s
     */
    void addExemplarTabToMainframe(String s){
        Exemplar e = null;
        try {
            e = exemplarClient.get(s);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
        if(e != null){
            boolean editable = false;
            if(e.getCreator() == null) editable = false;
            else editable = e.getCreator().equals(currentUser) ? true : false;
            if(!editable) if(e.getContributors().contains(currentUser)) editable = true;
            ExemplarTab newExemplarTab = new ExemplarTab(e, editable, currentUser);
            addListenersToExemplarTab(newExemplarTab);
            mainFrame.addTab(s,newExemplarTab);
        }
    }

    /**
     * Sets all required listeners for a given ExemplarTab
     * @param newExemplarTab the given tab to add the listeners
     */
    void addListenersToExemplarTab(ExemplarTab newExemplarTab){
        newExemplarTab.setCloseListener((c)->{
            ExemplarTab tab = (ExemplarTab)c;
            JButton updateButton = tab.getUpdateButton();
            if(tab.isEditable()) updateButton.doClick();
            mainFrame.removeTab(c);
        });
        newExemplarTab.setUpdateExemplarListener((exemplar)->{
            exemplarClient.update(exemplar.getName(), exemplar);
        });
        newExemplarTab.setDeleteExemplarListener((id, tab)->{
            try {
                exemplarClient.delete(id);
                mainFrame.removeTab(tab);
                refreshHomeTab();
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

        newExemplarTab.setExportListener((c)->{
            ExemplarTab tab = (ExemplarTab)c;
            JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            jfc.setDialogTitle("Choose a destination");
            jfc.setAcceptAllFileFilterUsed(false);
            String[] acceptedExtensions = {"txt","json"};
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Text and JSON Files", acceptedExtensions);
            jfc.addChoosableFileFilter(filter);

            int returnValue = jfc.showSaveDialog(null);

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = jfc.getSelectedFile();
                String path = selectedFile.getAbsolutePath();
                if(!path.contains(".txt") && !path.contains(".json")) path += ".json";
                exportExemplar(path, tab.getExemplar());
            }
        });
    }

    /**
     * Sets all required listeners for a given CommunityTab
     * @param newCommuintyTab the given tab to add the listeners
     */
    void addListenersToCommunityTab(CommunityTab newCommunityTab){
        newCommunityTab.setCloseListener((c)->{
            CommunityTab tab = (CommunityTab)c;
            JButton updateButton = tab.getUpdateButton();
            if(tab.isEditable()) updateButton.doClick();
            mainFrame.removeTab(c);
        });
        newCommunityTab.setUpdateCommunityListener((community)->{
            exemplarClient.update(community.getName(), community);
        });
        newExemplarTab.setDeleteCommunityListener((id, tab)->{
            try {
                communityClient.delete(id);
                mainFrame.removeTab(tab);
                refreshHomeTab();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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

    private void exportExemplar(String path, Exemplar exemplar) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(exemplar);
            BufferedWriter writer = new BufferedWriter(new FileWriter(path));
            writer.write(json);
            writer.close();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Verifies if an Exemplar with the given String as ID already exists and returns false if so.
     * @param s the given ExemplarName
     * @return true if the name is free, false if not
     */
    boolean verifyExemplarName(String s){
        try{
            Exemplar exists = exemplarClient.get(s);
            if(exists == null) return true;
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Verifies if an Community with the given String as ID already exists and returns false if so.
     * @param s the given CommunityName
     * @return true if the name is free, false if not
     */
    boolean verifyCommunityName(String s){
        try{
            Community exists = communityClient.get(s);
            if(exists == null) return true;
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
    /**
     * Sets the logoutlistener in the mainframe
     */
    void setLogoutListener(ActionListener listener){
        logoutListener = listener;
        mainFrame.setLogoutListener(e->{
            logout();
        });
    }

    private void logout() {
        addContributorFrame.dispose();
        newLabelPopupFrame.dispose();
        newRatingPopupFrame.dispose();
        newExemplarPopupFrame.dispose();
        // newCommunityPopupFrame.dispose();
        logoutListener.actionPerformed(null);
    }

    @Override
    public void run() {
        initialExemplarLibraryTab = new ExemplarLibraryTab("");
        initialContributorLibraryTab = new ContributorLibraryTab("");
        addListenersToContributorLibrary(initialContributorLibraryTab);
        addListenersToExemplarLibrary(initialExemplarLibraryTab);
        librarysLoaded = true;
        return;
    }
}
