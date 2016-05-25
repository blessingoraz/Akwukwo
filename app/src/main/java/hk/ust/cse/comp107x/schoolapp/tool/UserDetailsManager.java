package hk.ust.cse.comp107x.schoolapp.tool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hk.ust.cse.comp107x.schoolapp.Singletons.UserDetails;

/**
 * Created by blessingorazulume on 3/20/16.
 */
public class UserDetailsManager {
    public UserDetails[] schools;
    int size;
    int count = 0;

    public UserDetailsManager(int size) {
        this.size = size;
        schools = new UserDetails[size];
    }

    public void addDetails (UserDetails details) {

        doubleSize(schools, size *2);

        if (size == schools.length) {
            schools[count++] = details;
        }
         else {
            schools[count++] = details;
        }
    }

    public List<UserDetails> getUserDetails () {
        return Arrays.asList(schools);
    }

    public void display () {
        for (int i = 0; i < schools.length; i++) {
            System.out.println("Address of School " + schools[i].address);
        }
    }

    public UserDetails[] doubleSize (UserDetails[] array, int size) {
        UserDetails[] modified = new UserDetails[size * 2];
        for (int i = 0; i < array.length; i++ ) {
            modified[i] = array[i];
        }
        array = modified;
        return array;
    }

}
