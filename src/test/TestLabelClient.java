import model.entities.*;
import model.httpclients.ExemplarClient;
import model.httpclients.LabelClient;
import model.httpclients.RatingClient;
import model.httpclients.UserClient;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class TestLabelClient {
    private LabelClient client;
    private Label testEntity;
    boolean errorOccured;

    @Before
    public void initialize() throws IOException, InterruptedException {
        client = new LabelClient();
        testEntity = new Label();
        testEntity.setValue("testLabel");
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
            Label added = client.add(testEntity);
            assertEquals(testEntity, added);
        }catch(Exception e) {
            errorOccured = true;
        }
        assertFalse(errorOccured);
    }


}
