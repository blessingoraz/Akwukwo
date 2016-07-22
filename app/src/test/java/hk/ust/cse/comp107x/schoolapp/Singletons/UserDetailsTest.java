package hk.ust.cse.comp107x.schoolapp.Singletons;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by blessingorazulume on 7/19/16.
 */
public class UserDetailsTest {
    UserDetails userDetails;

    @Before
    public void setUp() {
        userDetails = new UserDetails();
    }

    @Test
    public void testSettersAndGetters() {
        userDetails.setEmail("random@email.com");
        userDetails.setName("random");
        userDetails.setId("1234");

        assertEquals("random@email.com", userDetails.getEmail());
        assertEquals("random", userDetails.getName());
        assertEquals("1234", userDetails.getId());
    }
}
