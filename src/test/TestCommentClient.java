import model.entities.*;
import model.httpclients.CommentClient;
import model.httpclients.ExemplarClient;
import model.httpclients.RatingClient;
import model.httpclients.UserClient;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class TestCommentClient {
    private CommentClient client;
    private Comment testEntity;
    boolean errorOccured;

    @Before
    public void initialize() throws IOException, InterruptedException {
        client = new CommentClient();
        testEntity = new Comment();
        User user = new User("testUserForRating", "fullName", "password", 1);
        Exemplar exemplar = new Exemplar();
        exemplar.setName("testExemplarForRating");
        new ExemplarClient().add(exemplar);
        new UserClient().add(user);
        testEntity.setCreator(user);
        testEntity.setExemplar(exemplar);
        testEntity.setValue("First");


        client.delete("" + testEntity.getId());
        errorOccured = false;
    }

    /**
     * When an entity is added to the database,
     * the returned entity is expected to be equal to the added one,
     * signaling that adding has been succesfull.
     */
    @Test
    public void Test_addEntity() {
        try {
            Comment added = client.add(testEntity);
            assertEquals(testEntity.getValue(), added.getValue());
        } catch (Exception e) {
            e.printStackTrace();
            errorOccured = true;
        }
        assertFalse(errorOccured);
    }

    /**
     * When a entity is added to the database,
     * the entity returned by the get()-Method is expected to be equal
     * to the entity added before
     */
    @Test
    public void Test_getEntity() {
        try {
            testEntity = client.add(testEntity);
            Comment get = client.get(testEntity.getId() + "");
            assertEquals(testEntity.getValue(), get.getValue());
        } catch (Exception e) {
            errorOccured = true;
        }
        assertFalse(errorOccured);
    }

    /**
     * When a entity is deleted from the database,
     * the entity returned by the get()-method is expected to be null
     */
    @Test
    public void Test_deleteEntity() {
        try {
            testEntity = client.add(testEntity);
            client.delete(testEntity.getId() + "");
            Comment deleted = client.get(testEntity.getId() + "");
            assertEquals(null, deleted);
        } catch (Exception e) {
            errorOccured = true;
        }
        assertFalse(errorOccured);
    }

    /**
     * When a entity
     * is added to the database, the list of all entities
     * fetched from the database is expected to be +1 in size compared to the
     * list  before adding
     */
    @Test
    public void Test_getAllEntites() {
        try {
            client.delete(testEntity.getId() + "");
            List<Comment> before = client.getAll();
            int sizeBefore = before.size();
            client.add(testEntity);
            List<Comment> after = client.getAll();
            int sizeAfter = after.size();
            assertEquals(sizeBefore + 1, sizeAfter);
        } catch (Exception e) {
            errorOccured = true;
        }
        assertFalse(errorOccured);
    }

    /**
     * When an already existing entity is updated via the add()-method
     * the returned entity is expected to reflect those changes
     */
    @Test
    public void Test_updateEntityViaAdd() {
        try {
            testEntity = client.add(testEntity);
            testEntity.setValue("Second");
            Comment updated = client.add(testEntity);
            Comment comment = client.get(testEntity.getId() + "");
            assertEquals(comment.getValue(), "Second");
        } catch (Exception e) {
            errorOccured = true;
        }
        assertFalse(errorOccured);
    }

    /**
     * When a comment is added, the method getCommentsForExemplar
     * is expected to contain this comment
     */
    @Test
    public void Test_getCommentsForExemplar(){

        try {
            testEntity = client.add(testEntity);
            List<Comment> forExemplar = client.findCommentsForExemplar(testEntity.getExemplar().getName());
            assertTrue(forExemplar.contains(testEntity));
            assertEquals(forExemplar.get(forExemplar.indexOf(testEntity)).getValue(), "First");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}