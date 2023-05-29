package sg.edu.nus.iss.app.server.models;

public class UpdatePassword {
    private String newpassword;
    private String token;
    public String getNewpassword() {
        return newpassword;
    }
    public void setNewpassword(String newpassword) {
        this.newpassword = newpassword;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
}
