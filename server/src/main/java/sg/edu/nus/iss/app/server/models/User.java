package sg.edu.nus.iss.app.server.models;

import org.springframework.jdbc.support.rowset.SqlRowSet;

public class User {
    private Integer userId;
    private String userName;
    private String userPassword;
    private String userEmail;
    private String gender;
    private Integer likes;
    private Integer checks;
    private String photo;

    public Integer getUserId() {
        return userId;
    }
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getUserPassword() {
        return userPassword;
    }
    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }
    public String getUserEmail() {
        return userEmail;
    }
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getLikes() {
        return likes;
    }
    public void setLikes(Integer likes) {
        this.likes = likes;
    }
    public Integer getChecks() {
        return checks;
    }
    public void setChecks(Integer checks) {
        this.checks = checks;
    }

    public String getPhoto() {
        return photo;
    }
    public void setPhoto(String photo) {
        this.photo = photo;
    }
    public static User populate(SqlRowSet rs){
        final User theUser = new User();
        theUser.setUserId(rs.getInt("user_id"));
        theUser.setUserName(rs.getString("user_name"));
        theUser.setUserEmail(rs.getString("user_email"));
        theUser.setUserPassword(rs.getString("user_password"));
        theUser.setGender(rs.getString("gender"));
        theUser.setLikes(rs.getInt("likes"));
        theUser.setChecks(rs.getInt("checks"));
        theUser.setPhoto(rs.getString("photo"));
        return theUser;
    }
}
