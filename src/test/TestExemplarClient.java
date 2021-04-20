import model.entities.Exemplar;
import model.entities.Label;
import model.entities.User;
import model.httpclients.ExemplarClient;
import model.httpclients.UserClient;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class TestExemplarClient {
    private ExemplarClient client;
    private Exemplar testEntity;
    boolean errorOccured;

    @Before
    public void initialize() throws IOException, InterruptedException {
        client = new ExemplarClient();
        testEntity = new Exemplar();
        testEntity.setName("testName");
        testEntity.setProblem("testProblem");
        testEntity.setSolution("testSolution");
        client.delete(testEntity.getName());
        errorOccured = false;
    }

    /**
     * When an entity is added to the database,
     * the returned entity is expected to be equal to the added one,
     * signaling that adding has been succesfull.
     */
    @Test
    public void Test_addExemplar(){
        try{
            Exemplar addedExemplar = client.add(testEntity);
            assertEquals(testEntity, addedExemplar);
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
    public void Test_getExemplar(){
        try {
            client.add(testEntity);
            Exemplar getExemplar = client.get(testEntity.getName());
            assertEquals(testEntity, getExemplar);
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
    public void Test_deleteExemplar(){
        try {
            client.add(testEntity);
            client.delete(testEntity.getName());
            Exemplar deletedExemplar = client.get(testEntity.getName());
            assertEquals(null, deletedExemplar);
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
    public void Test_getExemplars(){
        try {
            List<Exemplar> before = client.getAll();
            int sizeBefore = before.size();
            client.add(testEntity);
            List<Exemplar> after = client.getAll();
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
    public void Test_addDuplicateExemplar(){
        try {
            client.add(testEntity);
            testEntity.setProblem("newTestProblem");
            Exemplar duplicate = client.add(testEntity);
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
    public void Test_updateExistingUser(){
        try {
            client.add(testEntity);
            testEntity.setProblem("newTestProblem");
            Exemplar updated = client.update(testEntity.getName(), testEntity);
            assertEquals("newTestProblem", updated.getProblem());
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
    public void Test_updateNotExistingUser(){
        try {
            client.delete(testEntity.getName());
            Exemplar updated = client.update(testEntity.getName(), testEntity);
            assertEquals(null, updated);
        }catch(Exception e){
            errorOccured = true;
        }
        assertFalse(errorOccured);
    }

}
