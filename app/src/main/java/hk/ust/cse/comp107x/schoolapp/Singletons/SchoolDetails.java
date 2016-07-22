package hk.ust.cse.comp107x.schoolapp.Singletons;

/**
 * Created by blessingorazulume on 2/25/16.
 */
public class SchoolDetails {

    public String schoolName;
    public String address;
    public String motto;
    public String schoolImage;
    public String vision;
    public String fees;
    public String level;
    public String phone;
    public String schoolEmail;

    public SchoolDetails(UserDetails details) {

        System.out.println("Getting here ======>" + details+"");

//        setSchoolName(details.toString().replace("{", "").replace("}",""));

    }
    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMotto() {
        return motto;
    }

    public void setMotto(String motto) {
        this.motto = motto;
    }

    public String getSchoolImage() {
        return schoolImage;
    }

    public void setSchoolImage(String schoolImage) {
        this.schoolImage = schoolImage;
    }

    public String getVision() {
        return vision;
    }

    public void setVision(String vision) {
        this.vision = vision;
    }

    public String getFees() {
        return fees;
    }

    public void setFees(String fees) {
        this.fees = fees;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSchoolEmail() {
        return schoolEmail;
    }

    public void setSchoolEmail(String schoolEmail) {
        this.schoolEmail = schoolEmail;
    }

    public String getDetailedAddress() {
        return detailedAddress;
    }

    public void setDetailedAddress(String detailedAddress) {
        this.detailedAddress = detailedAddress;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String detailedAddress;
    public String latitude;
    public String longitude;

    @Override
    public String toString(){
        return schoolName+"\n"+schoolEmail+"\n"+address+"\n"+vision;
    }
}
