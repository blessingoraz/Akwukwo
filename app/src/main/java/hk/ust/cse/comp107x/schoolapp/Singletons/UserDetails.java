package hk.ust.cse.comp107x.schoolapp.Singletons;

/**
 * Created by blessingorazulume on 2/9/16.
 */
public class UserDetails {

    public String email;
    public String accessToken;
    public String id;
    public String name;
    public String password;
    public String index;

    public Boolean contain(String value) {
        if (value.equals(email) || value.equals(accessToken) || value.equals(id) || value.equals(name) || value.equals(password)) {
            return true;
        }
        return false;
    }

    public String schoolName;
    public String address;
    public String motto;
    public String schoolImage;
    public String vision;
    public String fees;
    public String level;
    public String phone;
    public String schoolEmail;
    public String detailedAddress;
    public String latitude;
    public String longitude;
    public String schoolId;
    public String allSchoolId;

    public UserDetails() {

    }

}
