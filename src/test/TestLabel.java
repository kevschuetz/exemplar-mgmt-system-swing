import model.entities.Label;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestLabel {
    private Label label;

    @Before
    public void initialize(){
        label = new Label();
    }

    @Test
    public void Test_getSetValue(){
        label.setValue("JUnit");
        assertEquals("JUnit", label.getValue());
    }

    @Test
    public void Test_equals(){
        label.setValue("JUnit");
        Label other = new Label();
        other.setValue("AnotherLabel");
        assertFalse(label.equals(other));
    }

    @Test
    public void Test_hashCode(){
        label.setValue("JUnit");
        Label other = new Label();
        other.setValue("AnotherLabel");
        assertFalse(label.hashCode() == other.hashCode());
    }
}
