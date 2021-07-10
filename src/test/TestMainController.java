import com.sun.tools.javac.Main;
import controller.LoginController;
import controller.MainController;
import model.entities.Exemplar;
import model.entities.User;
import model.httpclients.CommunityClient;
import model.httpclients.ExemplarClient;
import model.httpclients.RatingClient;
import model.httpclients.UserClient;
import org.junit.Before;
import org.junit.Test;
import view.events.UserEvent;
import view.panels.mainFrame.exemplarTab.ExemplarTab;
import view.panels.mainFrame.homeTab.HomeTab;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class TestMainController {
  private MainController controller;
  private boolean errorOccured;
    @Before
    public void initialize(){
        controller = new MainController();
        errorOccured = false;
    }

    @Test
    public void whenLoginSuccesfull_thenCurrentUserIsLoggedInUser(){
        User user = new User();
        user.setUsername("logged_in");
        controller.getLoginController().setCurrentUser(user);
        controller.getLoginController().getLoginListener().actionPerformed(null);
        assertEquals(controller.getCurrentUser(), user);
    }

    @Test
    public void whenRegistration_thenUserPersisted(){
        try {
            UserEvent e = new UserEvent("test_main_controller", "name","password", 1 );
            User u = new User();
            u.setUsername("test_main_controller");
            controller.getLoginController().processRegistrationRequest(e);
            User getRegisteredUser = null;
            getRegisteredUser = new UserClient().get("test_main_controller");
            assertEquals(u, getRegisteredUser);
            new UserClient().delete("test_main_controller");
        } catch (IOException ioException) {
            ioException.printStackTrace();
            errorOccured = true;
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
            errorOccured = true;
        }
        assertFalse(errorOccured);
    }

    @Test
    public void whenRatingFrameListenerActivated_thanRatingPersisted(){
        try {
            User u = new User();
            u.setUsername("test_main_controller");
            new UserClient().add(u);
            controller.setCurrentUser(u);
            controller.setHomeTab(new HomeTab(u));

            Exemplar exemplar = new Exemplar();
            exemplar.setName("test_main_controller");
            exemplar.setLabels(new ArrayList<>());
            exemplar.setContributors(new ArrayList<>());
            new ExemplarClient().add(exemplar);

            ExemplarTab tab = new ExemplarTab(exemplar, true, controller.getCurrentUser());
            controller.getNewRatingPopupFrame().setTab(tab);
            controller.getNewRatingPopupFrame().getListener().RatingSubmitted(5);
            assertEquals(new RatingClient().getAvgRatingForExemplar(exemplar.getName()), 5, 0.01);

            new UserClient().delete("test_main_controller");
            new ExemplarClient().delete("test_main_controller");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void whenExemplarNameAvailable_thenVerifyTrue(){
        try {
            new ExemplarClient().delete("test_main_controller");
            assertTrue(controller.verifyExemplarName("test_main_controller"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void whenCommunityNameAvailable_thenVerifyTrue(){
        try {
            new CommunityClient().delete("test_main_controller");
            assertTrue(controller.verifyCommunityName("test_main_controller"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



}
