package sg.edu.nus.iss.app.server.models;

public class PasswordToken {
    private String email;
    private String token;
    private Long dateTime;

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public Long getDateTime() {
        return dateTime;
    }
    public void setDateTime(Long dateTime) {
        this.dateTime = dateTime;
    }
}
