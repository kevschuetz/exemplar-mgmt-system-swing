import model.entities.User;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestUser {
    private User user;

    @Before
    public void initialize(){
        user = new User();
    }

    @Test
    public void Test_getSetUsername(){
        user.setUsername("testUser");
        assertEquals("testUser", user.getUsername());
    }

    @Test
    public void Test_getSetIsContributor(){
        user.setIsContributor(1);
        assertTrue(user.getIsContributor() == 1);
    }

    @Test
    public void Test_getSetFullName(){
        user.setFullName("Max Mustermann");
        assertEquals("Max Mustermann", user.getFullName());
    }

    @Test
    public void Test_getSetPassword(){
        user.setPassword("MyPassword");
        assertEquals("MyPassword", user.getPassword());
    }

    @Test
    public void Test_hashCode(){
        User other = new User();
        user.setUsername("MyUser");
        other.setUsername("AnotherUser");
        assertTrue(user.hashCode() != other.hashCode());
    }

    @Test
    public void Test_equals(){
        User other = new User();
        user.setUsername("MyUser");
        other.setUsername("AnotherUser");
        assertFalse(user.equals(other));
    }

    @Test
    public void Test_toString(){
        user.setUsername("MyUser");
        assertEquals("MyUser", user.toString());
    }
}
