import model.entities.*;
import model.httpclients.CommunityClient;
import model.httpclients.ExemplarClient;
import model.httpclients.RatingClient;
import model.httpclients.UserClient;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
@Ignore
public class TestRatingClient {
    private RatingClient client;
    private Rating testEntity;
    boolean errorOccured;

    @Before
    public void initialize() throws IOException, InterruptedException {
        client = new RatingClient();
        testEntity = new Rating();
        RatingPK key = new RatingPK();
        User user = new User("testUserForRating", "fullName", "password", 1);
        Exemplar exemplar = new Exemplar();
        exemplar.setName("testExemplarForRating");
        new ExemplarClient().add(exemplar);
        new UserClient().add(user);
        key.setExemplar(exemplar);
        key.setUser(user);
        testEntity.setKey(key);
        testEntity.setRating(5.0);


        client.delete(testEntity.getKey().toString());
        errorOccured = false;
    }
    /**
     * When an entity is added to the database,
     * the returned entity is expected to be equal to the added one,
     * signaling that adding has been succesfull.
     */
    @Test
    public void Test_addEntity(){
        try{
            Rating added = client.add(testEntity);
            assertEquals(testEntity, added);
        }catch(Exception e) {
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
    public void Test_getEntity(){
        try {
            client.add(testEntity);
            Rating get = client.get(testEntity.getKey().toString());
            assertEquals(testEntity, get);
            assertEquals(testEntity.getRating(), get.getRating(), 0.0001);
        }catch(Exception e){
            errorOccured = true;
        }
        assertFalse(errorOccured);
    }
    /**
     * When a entity is deleted from the database,
     * the entity returned by the get()-method is expected to be null
     */
    @Test
    public void Test_deleteEntity(){
        try {
            client.add(testEntity);
            client.delete(testEntity.getKey().toString());
            Rating deleted = client.get(testEntity.getKey().toString());
            assertEquals(null, deleted);
        }catch(Exception e){
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
    public void Test_getAllEntites(){
        try {
            List<Rating> before = client.getAll();
            int sizeBefore = before.size();
            client.add(testEntity);
            List<Rating> after = client.getAll();
            int sizeAfter = after.size();
            assertEquals(sizeBefore+1, sizeAfter);
        }catch(Exception e){
            errorOccured = true;
        }
        assertFalse(errorOccured);
    }
    /**
     * When an already existing entity is updated via the add()-method
     * the returned entity is expected to reflect those changes
     */
    @Test
    public void Test_updateEntityViaAdd(){
        try {
            client.add(testEntity);
            testEntity.setRating(2);
            Rating updated = client.add(testEntity);
            Rating rating = client.get(testEntity.getKey().toString());
            assertEquals(rating.getRating(), 2.0, 0.01);
        }catch(Exception e){
            errorOccured = true;
        }
        assertFalse(errorOccured);
    }

    /**
     * When an already existing entity is updated via the update()-method
     * the returned entity is expected to reflect those changes
     */
    @Test
    public void Test_updateEntityViaUpdate(){
        try {
            client.add(testEntity);
            testEntity.setRating(2);
            Rating updated = client.add(testEntity);
            Rating rating = client.get(testEntity.getKey().toString());
            assertEquals(rating.getRating(), 2.0, 0.01);
        }catch(Exception e){
            errorOccured = true;
        }
        assertFalse(errorOccured);
    }

}
