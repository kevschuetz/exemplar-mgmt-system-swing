import model.entities.User;
import model.httpclients.UserClient;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;


public class TestUserClient {
    private UserClient client;
    private User testUser;
    private  boolean errorOccured;

    @Before
    public void initialize() throws IOException, InterruptedException {
        client = new UserClient();
        testUser = new User("testUser", "fullNameOfTestUser", "password", 0);
        client.delete(testUser.getUsername());
        errorOccured = false;
    }

    /**
     * When a user is added to the database, the returned User is expected to be equal to the user being added
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void Test_addUser() throws IOException, InterruptedException {
        try {
            User addedUser = client.add(testUser);
            assertEquals(addedUser, testUser);
        }catch(Exception e){
            errorOccured = true;
        }
        assertFalse(errorOccured);
    }

    /**
     * When a user is added to the database,
     * the user returned by the get()-Method is expected to be equal
     * to the user added before
     */
    @Test
    public void Test_getUser(){
        try {
            client.add(testUser);
            User getUser = client.get(testUser.getUsername());
            assertEquals(getUser, testUser);
        }catch(Exception e){
            errorOccured = true;
        }
        assertFalse(errorOccured);
    }

    /**
     * When a user is deleted from the database, the user returned by the get()-method is expected to be null
     */
    @Test
    public void Test_deleteUser(){
        try {
            client.add(testUser);
            client.delete(testUser.getUsername());
            User deletedUser = client.get(testUser.getUsername());
            assertEquals(null, deletedUser);
        }catch(Exception e){
            errorOccured = true;
        }
        assertFalse(errorOccured);
    }

    /**
     * When a user is added to the database, the list of all users fetched from the database is expected to be +1 in size than the list was before adding
     */
    @Test
    public void Test_getUsers(){
        try {
            List<User> usersBefore = client.getAll();
            int sizeBefore = usersBefore.size();
            client.add(testUser);
            List<User> usersAfter = client.getAll();
            int sizeAfter = usersAfter.size();
            assertEquals(sizeBefore+1, sizeAfter);
        }catch(Exception e){
            errorOccured = true;
        }
        assertFalse(errorOccured);
    }

    /**
     * When an already existing user is added to the database via the add()-method, the returned user is expected to be null, signaling that no update has been performed.
     */
    @Test
    public void Test_addDuplicateUser(){
        try {
            client.add(testUser);
            testUser.setFullName("newFullNameOfTestuser");
            User duplicateUser = client.add(testUser);
            assertEquals(null, duplicateUser);
        }catch(Exception e){
            errorOccured = true;
        }
        assertFalse(errorOccured);
    }

    /**
     * When an existing user is updated with a different fullName, the returned user is expected to reflect those changes
     */
    @Test
    public void Test_updateExistingUser(){
        try {
            client.add(testUser);
            testUser.setFullName("newFullNameOfTestuser");
            User updatedUser = client.update(testUser.getUsername(), testUser);
            assertEquals("newFullNameOfTestuser", updatedUser.getFullName());
        }catch(Exception e){
            errorOccured = true;
        }
        assertFalse(errorOccured);
    }

    /**
     * When a not existing user is updated, the return value is expected to be null, signaling that no update has been performed
     */
    @Test
    public void Test_updateNotExistingUser(){
        try {
            client.delete(testUser.getUsername());
            User updatedUserNotExisting = client.update(testUser.getUsername(), testUser);
            assertEquals(null, updatedUserNotExisting);
        }catch(Exception e){
            errorOccured = true;
        }
        assertFalse(errorOccured);
    }
}
