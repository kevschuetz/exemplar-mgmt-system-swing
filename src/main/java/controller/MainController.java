package controller;



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
import org.apache.commons.codec.binary.StringUtils;
import view.frames.mainFrame.*;
import view.panels.mainFrame.CommunityLibraryTab;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * The Main Controller of the application contains most of the business logic
 */
public class MainController{

    /**
     * main Method that sets initializes a new MainController and sets the LogoutListener of it
     * @param args
     */
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
    private CommentClient commentClient = new CommentClient();

    private MainFrame mainFrame;
    private HomeTab homeTab;
    private NewExemplarPopupFrame newExemplarPopupFrame;
    private NewLabelPopupFrame newLabelPopupFrame;
    private NewCommunityPopupFrame newCommunityPopupFrame;
    private NewRatingPopupFrame newRatingPopupFrame;
    private AddUserFrame addContributorFrame;
    private ExemplarLibraryTab initialExemplarLibraryTab;
    private ContributorLibraryTab initialContributorLibraryTab;
    private CommunityLibraryTab initialCommunityLibraryTab;
    boolean librarysLoaded = false;

    private ActionListener logoutListener;

    public static List<Exemplar> exemplars;
    public static List<User> users;
    public static List<Community> communities;
    public static List<Label> labels;
    public static List<Rating> ratings;
    public static List<Comment> comments;
    boolean dataLoaded = false;

    /**
     * Starts a new Thread to fetch the data from the database and starts the login process
     */
    public MainController(){
        initializeMainFrame();
        Thread dataThread = new Thread(()->{
            asyncLoadData();
        });
        dataThread.start();

        //login
        loginController = new LoginController();
        loginController.setLoginListener(x->{
            currentUser = loginController.getCurrentUser();
            waitForDataAfterLogin();
        });
        loginController.startLoginProcess();

        checkDataStatus();
    }

    /**
     * Regularly checks if the data has already been fetched and continues with the initialization of the components if so
     */
    void checkDataStatus(){
        if(dataLoaded)startApplication();
        else {
            Thread waitingThread = new Thread(() -> {
                try {
                    Thread.sleep(500);
                    checkDataStatus();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            waitingThread.start();
        }
    }

    /**
     * Initializes components after data has been fetched
     */
    void startApplication(){
        initializeNewExemplarFrame();
        initializeNewCommunityFrame();
        initializeNewLabelPopupFrame();
        initializeNewRatingPopupFrame();
        initializeAddContributorFrame();
    }

    /**
     * Triggered after succesfull login - waits in a loop until the data has been fetched  and enters the application if so
     */
    void waitForDataAfterLogin(){
        if(dataLoaded) loginSuccesfull();
        else {
            Thread waitingThread = new Thread(()->{
                try {
                    Thread.sleep(2000);
                    waitForDataAfterLogin();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            waitingThread.start();
        }
    }
    /**
     * Sets MainFrame visible.
     * Opens new HomeTab if User is not a guest.
     */
   public void loginSuccesfull(){
       loginController.getLoginFrame().setVisible(false);
        mainFrame.setVisible(true);

        if(currentUser != null){
            mainFrame.setTitle("Welcome, "+currentUser.getUsername()+" !");
            homeTab = new HomeTab(currentUser);
            addListenersToHomeTab();
            mainFrame.addTab("Home", homeTab);

        }
        else mainFrame.setTitle("Welcome!");
    }

    /**
     * Method that updates all data avery 30 seconds
     */
    void asyncLoadData(){
        try {
            exemplars = exemplarClient.getAll();
            communities = communityClient.getAll();
            users = userClient.getAll();
            ratings = ratingClient.getAll();
            comments= commentClient.getAll();
            dataLoaded = true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(30000);
            asyncLoadData();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the MainFrame
     */
    void initializeMainFrame(){
        mainFrame = new MainFrame();
        mainFrame.setVisible(false);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(new Dimension(1300, 1000));
        mainFrame.setExemplarButtonListener(getOpenExemplarLibraryListener(false));
        mainFrame.setContributorButtonListener(getOpenContributorLibraryListener(false));
        mainFrame.setCommunityButtonListener(getOpenCommunityLibraryListener(false));
        mainFrame.setSearchExemplarListener(getOpenExemplarLibraryListener(true));
        mainFrame.setSearchContributorListener(getOpenContributorLibraryListener(true));
        mainFrame.setSearchCommunityListener(getOpenCommunityLibraryListener(true));
        /**
         * Implements the listener used to import an exemplar from the local file system
         */
        mainFrame.setImportListener(path->{
            Path pathTo = Path.of(path);
            try {
                String exemplarJson = Files.readString(pathTo);
                ObjectMapper mapper = new ObjectMapper();
                Exemplar importedExemplar = mapper.readValue(exemplarJson, Exemplar.class);
                importedExemplar.setCreator(currentUser);
                importedExemplar.setContributors(new ArrayList<>());
                for (Label l : importedExemplar.getLabels()){
                    labelClient.add(l);
                }
                addExemplar(importedExemplar);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        mainFrame.setCreateExemplarListener(e->newExemplarPopupFrame.setVisible(true));
    }

    /**
     * Adds an imported exemplar to the database.
     * @param exemplar the exemplar to be added
     */
    public void addExemplar(Exemplar exemplar){
        Exemplar response = null;
        try {
            response = exemplarClient.add(exemplar);
            if(response != null){
                addExemplarTabToMainframe(response.getName());
                exemplars.add(exemplar);
                refreshHomeTab();
            }else{
                String newName = JOptionPane.showInputDialog("An Exemplar with the name " + exemplar.getName() + " already exists. Choose another one and try again.");
                if(newName == null)return;
                exemplar.setName(newName);
                addExemplar(exemplar);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



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
        newExemplarPopupFrame.setListener(s->{
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

    /**
     * Initializes the Frame used to create a new Community
     */
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
        newCommunityPopupFrame.setListener(s->{
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
        newLabelPopupFrame.setListener(s->{
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
        newRatingPopupFrame.setListener(i->{
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
                ratings.add(r);
                newRatingPopupFrame.setVisible(false);
                if(newRating != null){
                    JOptionPane.showMessageDialog(newRatingPopupFrame, "Thank you for Rating " + newRatingPopupFrame.getTab().getExemplar().getName());
                }
                newRatingPopupFrame.getTab().refreshInfoPanel();
                refreshHomeTab();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Initializes the Frame used to add Contributors to an Exemplar
     */
    void initializeAddContributorFrame(){
        addContributorFrame = new AddUserFrame();
        addContributorFrame.setVisible(false);
        addContributorFrame.setSize(new Dimension(350, 500));
        addContributorFrame.setLocationRelativeTo(mainFrame);
        /**
         * Sets the listener of the frame to check if the selected user has contributor status,
         * adds the contributor to the exemplar and persists the update.
         * Refreshes the info panel from the ExemplarTab to reflect changes.
         */
        addContributorFrame.setListener(u->{
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
        return e -> {
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
                List<JComponent> list = new ArrayList<>();
                list.add(exemplarLibrary);
                mainFrame.setOpenSearchTabs(list);
                mainFrame.addTab("Search Exemplars", exemplarLibrary);
            }else mainFrame.addTab("Exemplar Library",exemplarLibrary);


            mainFrame.setLastTabSelected();
        };
    }

    /**
     * Adds the necessary listeners to an ExemplarLibrary instance
     * @param exemplarLibrary the library
     */
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
        return e -> {
            ContributorLibraryTab contributorLibrary;

            if(searchableByMainFrame) {
                contributorLibrary = new ContributorLibraryTab(mainFrame.getSearchTerm());
            }
            else contributorLibrary = new ContributorLibraryTab("");

            addListenersToContributorLibrary(contributorLibrary);

            if(searchableByMainFrame){
                for(JComponent c : mainFrame.getOpenSearchTabs()){
                    mainFrame.removeTab(c);
                }
                List<JComponent> list = new ArrayList<>();
                list.add(contributorLibrary);
                mainFrame.setOpenSearchTabs(list);
                mainFrame.addTab("Search Contributors", contributorLibrary);
            } else mainFrame.addTab("Contributor Library", contributorLibrary);

            mainFrame.setLastTabSelected();
        };
    }

    /**
     * Adds the listeners to a Contributor Library instance
     * @param contributorLibrary the library
     */
    void addListenersToContributorLibrary(ContributorLibraryTab contributorLibrary){
        contributorLibrary.setCloseListener(c -> mainFrame.removeTab(c));
        /**
         * Implements the listener used to open contributorTabs for selected contributors
         */
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
     * Creates an ActionListener that opens a new communityLibrary in a seperate tab and selects that tab
     * @return the listener created
    */
    ActionListener getOpenCommunityLibraryListener(boolean searchableByMainFrame){
        return e -> {
            CommunityLibraryTab communityLibrary;

            if(searchableByMainFrame) {
                communityLibrary = new CommunityLibraryTab(mainFrame.getSearchTerm());
            }
            else communityLibrary = new CommunityLibraryTab("");

            addListenersToCommunityLibrary(communityLibrary);

            if(searchableByMainFrame){
                for(JComponent c : mainFrame.getOpenSearchTabs()){
                    mainFrame.removeTab(c);
                }
                List<JComponent> list = new ArrayList<>();
                list.add(communityLibrary);
                mainFrame.setOpenSearchTabs(list);
                mainFrame.addTab("Search Communities", communityLibrary);
            } else mainFrame.addTab("Community Library", communityLibrary);

            mainFrame.setLastTabSelected();
        };
    }

    /**
     * Adds listeners to a CommunityLibrary instance
     * @param communityLibrary the library
     */
    void addListenersToCommunityLibrary(CommunityLibraryTab communityLibrary){

        communityLibrary.setCloseListener(c -> mainFrame.removeTab(c));
        /**
         * Implements the listener used to open communityTabs for selected communities
         */
        communityLibrary.setCommunityListener(selectedEntities -> {
            for(String e1 : selectedEntities){
                addCommunityTabToMainframe(e1);
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
        homeTab.setUpdateUserListener(u-> {
            try {
                User updated = userClient.update(u.getUsername(), u);
                if (updated != null) {
                    this.currentUser = updated;
                    homeTab.setUser(updated);
                    JOptionPane.showMessageDialog(homeTab.getProfilePanel(), "Update successfull");
                }else JOptionPane.showMessageDialog(homeTab.getProfilePanel(), "Update failed");
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });

        /**
         * Takes a list with exemplar names and opens a ExemplarTab for every exemplar in the list (identified by name)
         */
        homeTab.setOpenExemplarListener(list->{
                for(String s : list){
                    addExemplarTabToMainframe(s);
                }
        });

        /**
         * Makes the NewExemplarPopupFrame visible to enter name
         */
        homeTab.setCreateExemplarListener(e-> newExemplarPopupFrame.setVisible(true));

        /**
         * Makes the NewCommunityPopupFrame visible to enter name
         */
        homeTab.setCreateCommunityListener(c-> newCommunityPopupFrame.setVisible(true));

        /**
         * Fetches all communities from the database according to their names in the list and opens
         * a CommunityTab for each of them
         */
        homeTab.setOpenCommunityListener(list->{
            try {
                CommunityClient client = new CommunityClient();
                for(String s : list){
                    Community c = client.get(s);
                    if(c != null){
                        CommunityTab tab = new CommunityTab(c, currentUser);
                        addListenersToCommunityTab(tab);
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
        homeTab.setDeleteUserListener(user->{
            try{
                users.remove(user);
                userClient.delete(user.getUsername());
                logout();
            }catch(Exception e){
                e.printStackTrace();
            }
        });

        homeTab.setCreateExemplarLibraryListener(getOpenExemplarLibraryListener(false));
        homeTab.setCreateContributorLibraryListener(getOpenContributorLibraryListener(false));

    }

    /**
     * Calls the refresh method form the hometab
     */
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
            exemplars.add(e);
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
        List <User> members = new ArrayList<>();
        members.add(currentUser);
        c.setMembers(members);
        c.setExemplars(new ArrayList<>());
        try {
            communityClient.add(c);
            communities.add(c);
            CommunityTab newCommunityTab = new CommunityTab(c, currentUser);
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
        if(contributor.getIsContributor()==0){
            JOptionPane.showMessageDialog(mainFrame, "User "+username+" is not a Contributor");
            return;
        }
        ContributorTab newContributorTab = new ContributorTab(contributor);
        newContributorTab.setCloseListener(c -> mainFrame.removeTab(c));
        newContributorTab.setExemplarListener(selectedEntities -> {
            for(String e : selectedEntities){
                addExemplarTabToMainframe(e);
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
            else editable = e.getCreator().equals(currentUser);
            if(!editable && e.getContributors().contains(currentUser)) editable = true;
            ExemplarTab newExemplarTab = new ExemplarTab(e, editable, currentUser);
            addListenersToExemplarTab(newExemplarTab);
            mainFrame.addTab(s,newExemplarTab);
        }
    }
    /**
     * Fetches the community with the given id, initializes a CommunityTab with it and adds the tab to the mainFrame
     * @param s
     */
    void addCommunityTabToMainframe(String s){
        Community c = null;
        try {
            c = communityClient.get(s);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
        if(c != null){
            CommunityTab newCommunityTab = new CommunityTab(c, currentUser);
            addListenersToCommunityTab(newCommunityTab);
            mainFrame.addTab(s,newCommunityTab);
        }
    }


    /**
     * Sets all required listeners for a given ExemplarTab
     * @param newExemplarTab the given tab to add the listeners
     */
    void addListenersToExemplarTab(ExemplarTab newExemplarTab){
        newExemplarTab.setCloseListener(c->{
            ExemplarTab tab = (ExemplarTab)c;
            JButton updateButton = tab.getUpdateButton();
            if(tab.isEditable()) updateButton.doClick();
            mainFrame.removeTab(c);
        });
        /**
         * Implements the listener used to update a given exemplar
         */
        newExemplarTab.setUpdateExemplarListener(exemplar-> exemplarClient.update(exemplar.getName(), exemplar));
        newExemplarTab.setDeleteExemplarListener((id, tab)->{
            try {
                System.out.println(id);
                Exemplar deleted = exemplarClient.get(id);
                System.out.println(deleted.getName());
                exemplars.remove(deleted);
                exemplarClient.delete(id);
                mainFrame.removeTab(tab);
                refreshHomeTab();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        newExemplarTab.setAddLabelListener(tab->{
            newLabelPopupFrame.setVisible(true);
            newLabelPopupFrame.setTab(tab);
        });

        newExemplarTab.setRatingListener(t->{
            newRatingPopupFrame.setTab(t);
            newRatingPopupFrame.setTitle(t.getExemplar().getName());
            newRatingPopupFrame.setVisible(true);
        });

        newExemplarTab.setContributorListener(t->{
            addContributorFrame.setTab(t);
            addContributorFrame.setTitle(t.getExemplar().getName());
            addContributorFrame.setVisible(true);
        });
        /**
         * Implements the listener used to export an exemplar
         */
        newExemplarTab.setExportListener(c->{
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
        /**
         * Implements the listener used to add an exemplar to a community
         */
        newExemplarTab.setAddToCommunityListener(s -> {
            try {
                Community c = communityClient.get(s);
                if(c == null)return;
                c.getExemplars().add(newExemplarTab.getExemplar());
                communityClient.update(c.getName(), c);

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

        });


    }

    /**
     * Sets all required listeners for a given CommunityTab
     * @param newCommunityTab the given tab to add the listeners
     */
    void addListenersToCommunityTab(CommunityTab newCommunityTab){
        newCommunityTab.setCloseListener(c->mainFrame.removeTab(c));
        newCommunityTab.setUpdateCommunityListener(community-> communityClient.update(community.getName(), community));
        newCommunityTab.setDeleteCommunityListener((id, tab)->{
            try {
                communityClient.delete(id);
                communities.remove(communities.stream().filter(c->c.getName()==id).collect(Collectors.toList()).get(0));
                mainFrame.removeTab(tab);
                refreshHomeTab();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        /**
         * Implements the listener used to join a community
         */
        newCommunityTab.setJoinCommunityListener(u->{
            Community c = null;
            try {
                c = communityClient.get(newCommunityTab.getCommunity().getName());
                c.getMembers().add(u);
                c = communityClient.update(c.getName(), c);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        /**
         * Implements the listener used to leava a community
         */
        newCommunityTab.setLeaveListener(u->{
            Community c = null;
            try {
                c = communityClient.get(newCommunityTab.getCommunity().getName());
                c.getMembers().remove(u);
                c = communityClient.update(c.getName(), c);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        /**
         * Implements the listener that gets activated afer a community member has been double-clicked
         */
        newCommunityTab.setMemberClickedListener(s -> {
            try {
                createNewContributorTab(s);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        /**
         * Implements the listener used to remove an exemplar from the community
         */
        newCommunityTab.setRemoveExemplarListener(s -> {
            try {
                Community c = communityClient.get(newCommunityTab.getCommunity().getName());
                c.getExemplars().remove(exemplarClient.get(s));
                communityClient.update(c.getName(),c);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        /**
         * Impelemnts the listener used to open an exemplar from within the community
         */
        newCommunityTab.setShowExemplarListener(this::addExemplarTabToMainframe);
    }

    /**
     * Exports an exemplar to the users local file system
     * @param path the path where the exemplar needs to be saved
     * @param exemplar the exemplar to be saved
     */
    private void exportExemplar(String path, Exemplar exemplar) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(exemplar);
            BufferedWriter writer = new BufferedWriter(new FileWriter(path));
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Verifies if an Exemplar with the given String as ID already exists and returns false if so.
     * @param s the given ExemplarName
     * @return true if the name is free, false if not
     */
    public boolean verifyExemplarName(String s){
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
    public boolean verifyCommunityName(String s){
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
        mainFrame.setLogoutListener(e-> logout());
    }

    /**
     * disposes the frames before calling the logout method for memory performance reasons
     */
    private void logout() {
        addContributorFrame.dispose();
        newLabelPopupFrame.dispose();
        newRatingPopupFrame.dispose();
        newExemplarPopupFrame.dispose();
        logoutListener.actionPerformed(null);
    }


    public User getCurrentUser() {
        return currentUser;
    }

    public LoginController getLoginController() {
        return loginController;
    }

    public NewRatingPopupFrame getNewRatingPopupFrame() {
        return newRatingPopupFrame;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public void setHomeTab(HomeTab homeTab) {
        this.homeTab = homeTab;
    }
}
