package hk.ust.cse.comp107x.schoolapp;

import android.content.Context;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import hk.ust.cse.comp107x.schoolapp.Singletons.SchoolDetails;
import hk.ust.cse.comp107x.schoolapp.Singletons.UserDetails;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
@RunWith(MockitoJUnitRunner.class)
public class ExampleUnitTest {
    Context context;
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }
    @Test
    public void testSchoolDetails(){
        UserDetails userDetails = new UserDetails();
        SchoolDetails schoolDetails = new SchoolDetails(userDetails);
        schoolDetails.setSchoolName("God Gift Memorial College");
        schoolDetails.setAddress("Lagos Nigeria");
        schoolDetails.setVision("Wisdom is the principal thing");
        schoolDetails.setSchoolEmail("info@godgiftschools.com");
        assertEquals(schoolDetails.getSchoolName(),"God Gift Memorial College");
        assertEquals(schoolDetails.getAddress(),"Lagos Nigeria");
        assertNotNull(schoolDetails.getSchoolEmail());
        System.out.println(schoolDetails.toString());
        assertNotNull(schoolDetails.toString());
    }
    @Test
    public void testListOfMySchools(){
        ListOfMySchools listOfMySchools = mock(ListOfMySchools.class);
        when(listOfMySchools.getApplicationContext()).thenReturn(context);
        listOfMySchools.getSchoolId();
        verify(listOfMySchools,atLeastOnce()).getSchoolId();
        assertNotNull(listOfMySchools);
    }
}