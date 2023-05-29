package sg.edu.nus.iss.app.server.models;

import org.springframework.jdbc.support.rowset.SqlRowSet;

import jakarta.json.Json;
import jakarta.json.JsonObject;

public class UserProfile {
    private Integer profileId;
    private String email;
    private Boolean profileIsPublic;
    private String displayName;
    private String summary;
    private Integer birthday;
    private Integer birthmonth;
    private Integer birthyear;
    private Integer height;
    private Integer weight;
    private String isSmoking;
    private String gender;
    private String postalCode;
    private String mail;

    public Integer getProfileId() {
        return profileId;
    }
    public void setProfileId(Integer profileId) {
        this.profileId = profileId;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public Boolean getProfileIsPublic() {
        return profileIsPublic;
    }
    public void setProfileIsPublic(Boolean profileIsPublic) {
        this.profileIsPublic = profileIsPublic;
    }
    public String getDisplayName() {
        return displayName;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    public String getSummary() {
        return summary;
    }
    public void setSummary(String summary) {
        this.summary = summary;
    }
    public Integer getBirthday() {
        return birthday;
    }
    public void setBirthday(Integer birthday) {
        this.birthday = birthday;
    }
    public Integer getBirthmonth() {
        return birthmonth;
    }
    public void setBirthmonth(Integer birthmonth) {
        this.birthmonth = birthmonth;
    }
    public Integer getBirthyear() {
        return birthyear;
    }
    public void setBirthyear(Integer birthyear) {
        this.birthyear = birthyear;
    }
    public Integer getHeight() {
        return height;
    }
    public void setHeight(Integer height) {
        this.height = height;
    }
    public Integer getWeight() {
        return weight;
    }
    public void setWeight(Integer weight) {
        this.weight = weight;
    }
    public String getIsSmoking() {
        return isSmoking;
    }
    public void setIsSmoking(String isSmoking) {
        this.isSmoking = isSmoking;
    }
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public String getPostalCode() {
        return postalCode;
    }
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getMail() {
        return mail;
    }
    public void setMail(String mail) {
        this.mail = mail;
    }
    
    public static UserProfile populate(SqlRowSet rs){
        final UserProfile theUser = new UserProfile();
        theUser.setProfileId(rs.getInt("profile_id"));
        theUser.setEmail(rs.getString("email"));
        theUser.setProfileIsPublic(rs.getBoolean("profile_is_public"));
        theUser.setDisplayName(rs.getString("display_name"));
        theUser.setSummary(rs.getString("summary"));
        theUser.setBirthday(rs.getInt("birthday"));
        theUser.setBirthmonth(rs.getInt("birthmonth"));
        theUser.setBirthyear(rs.getInt("birthyear"));
        theUser.setHeight(rs.getInt("height"));
        theUser.setWeight(rs.getInt("weight"));
        theUser.setIsSmoking(rs.getString("is_smoking"));
        theUser.setGender(rs.getString("gender"));
        theUser.setPostalCode(rs.getString("postal_code"));
        theUser.setMail(rs.getString("mail"));
        return theUser;
    }

    public static JsonObject toJsonId(UserProfile profile){
        return Json.createObjectBuilder()
                    .add("profileId", profile.getProfileId())
                    .build();

    }

    public static JsonObject toJson(UserProfile profile, Integer age, String photo, String photoUrl, Boolean liked){
        return Json.createObjectBuilder()
                    .add("profileId", profile.getProfileId())
                    .add("displayName", profile.getDisplayName())
                    .add("summary", profile.getSummary())
                    .add("height", profile.getHeight())
                    .add("weight", profile.getWeight())
                    .add("isSmoking", profile.getIsSmoking())
                    .add("age", age)
                    .add("photo", photo)
                    .add("photoUrl",photoUrl)
                    .add("liked", liked)
                    .build();
    }
    
}
