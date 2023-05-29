package sg.edu.nus.iss.app.server.models;

public class TokenVerification {
    private String email;
    private Boolean result;
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public Boolean getResult() {
        return result;
    }
    public void setResult(Boolean result) {
        this.result = result;
    }
}
