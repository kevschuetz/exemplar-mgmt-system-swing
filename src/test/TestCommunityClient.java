import model.entities.Community;
import model.entities.Exemplar;
import model.httpclients.CommunityClient;
import model.httpclients.ExemplarClient;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@Ignore
public class TestCommunityClient {
    private CommunityClient client;
    private Community testEntity;
    boolean errorOccured;

    @Before
    public void initialize() throws IOException, InterruptedException {
        client = new CommunityClient();
        testEntity = new Community();
        testEntity.setName("testName");
        client.delete(testEntity.getName());
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
            Community added = client.add(testEntity);
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
            Community get = client.get(testEntity.getName());
            assertEquals(testEntity, get);
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
            client.delete(testEntity.getName());
            Community deleted = client.get(testEntity.getName());
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
            List<Community> before = client.getAll();
            int sizeBefore = before.size();
            client.add(testEntity);
            List<Community> after = client.getAll();
            int sizeAfter = after.size();
            assertEquals(sizeBefore+1, sizeAfter);
        }catch(Exception e){
            errorOccured = true;
        }
        assertFalse(errorOccured);
    }

    /**
     * When an already existing entity is added to the database via the add()-method,
     * the returned entity is expected to be null,
     * signaling that no update has been performed.
     */
    @Test
    public void Test_addDuplicate(){
        try {
            client.add(testEntity);
            Community duplicate = client.add(testEntity);
            assertEquals(null, duplicate);
        }catch(Exception e){
            errorOccured = true;
        }
        assertFalse(errorOccured);
    }
    /**
     * When an existing entity is updated with a different parameter,
     * the returned entity is expected to reflect those changes
     */
    @Test
    public void Test_updateExistingEntity(){
        try {
            String oldName = testEntity.getName();
            client.add(testEntity);
            testEntity.setName("newTestName");
            Community updated = client.update(oldName, testEntity);
            assertEquals("newTestName", updated.getName());
        }catch(Exception e){
            errorOccured = true;
        }
        assertFalse(errorOccured);
    }
    /**
     * When a not existing entity is updated,
     * the returned value is expected to be null,
     * signaling that no update/adding has been performed
     */
    @Test
    public void Test_updateNotExistingEntity(){
        try {
            client.delete(testEntity.getName());
            Community updated = client.update(testEntity.getName(), testEntity);
            assertEquals(null, updated);
        }catch(Exception e){
            errorOccured = true;
        }
        assertFalse(errorOccured);
    }

}
