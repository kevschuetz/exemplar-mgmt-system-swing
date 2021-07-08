import model.entities.Exemplar;
import model.entities.Label;
import model.entities.User;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class TestExemplar {
    Exemplar exemplar;
    User ourTestUser;

    @Before
    public void initialize(){
        exemplar = new Exemplar();
        ourTestUser = new User();
    }

    @Test
    public void Test_getSetName(){
        exemplar.setName("MyTestExemplar");
        assertEquals("MyTestExemplar", exemplar.getName());
    }

    @Test
    public void Test_getSetProblem(){
        exemplar.setProblem("SomeProblem");
        assertEquals("SomeProblem", exemplar.getProblem());
    }

    @Test
    public void Test_getSetSolution(){
        exemplar.setSolution("SomeSolution");
        assertEquals("SomeSolution", exemplar.getSolution());
    }

    @Test
    public void Test_getSetCreator(){
        exemplar.setCreator(ourTestUser);
        assertEquals(ourTestUser, exemplar.getCreator());
    }

    @Test
    public void Test_getSetContributors(){
        List<User> allContributors = new ArrayList<>();
        User user1 = new User();
        User user2 = new User();
        User user3 = new User();
        allContributors.add(user1);
        allContributors.add(user2);
        allContributors.add(user3);
        exemplar.setContributors(allContributors);
        assertEquals(allContributors, exemplar.getContributors());
    }

    @Test
    public void Test_getSetLabels(){
        List<Label> allLabels= new ArrayList<>();
        Label label1 = new Label();
        Label label2 = new Label();
        Label label3 = new Label();
        allLabels.add(label1);
        allLabels.add(label2);
        allLabels.add(label3);
        exemplar.setLabels(allLabels);
        assertEquals(allLabels, exemplar.getLabels());
    }

    @Test
    public void Test_equals(){
        Exemplar other = new Exemplar();
        exemplar.setName("MyVeryFirstExemplar");
        other.setName("AnotherExemplar");
        assertFalse(exemplar.equals(other));
    }

    @Test
    public void Test_hashCode(){
        Exemplar other = new Exemplar();
        exemplar.setName("MyVeryFirstExemplar");
        other.setName("AnotherExemplar");
        assertFalse(exemplar.hashCode() == other.hashCode());
    }

}
