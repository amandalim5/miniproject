package sg.edu.nus.iss.app.server.services;


import java.util.Date;
import java.util.TimerTask;
import java.util.UUID;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sg.edu.nus.iss.app.server.models.ChangePassword;
import sg.edu.nus.iss.app.server.models.PasswordToken;
import sg.edu.nus.iss.app.server.repositories.TokenRepository;
import sg.edu.nus.iss.app.server.repositories.UserRepository;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@Service
public class ResetService {
    @Autowired
    TokenRepository tokenRepository;
    @Autowired 
    JavaMailSender javaMailSender;
    @Autowired
    UserRepository userRepository;
    
    public void sendResetToken(String email){
        PasswordToken p = new PasswordToken();
        p.setEmail(email);
        p.setDateTime(new Date().getTime());
        String token = UUID.randomUUID().toString().substring(0, 8);
        p.setToken(token);
        String result  = tokenRepository.insertPasswordToken(p);
        sendToEmail(email, token);
        System.out.println("the id: " + result);

    }

    public void sendToEmail(String email, String token){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@cornfield.com");
        message.setTo(email);
        message.setSubject("Password reset for CornField App");
        message.setText("This is your password reset token " + token);
        javaMailSender.send(message);
        

    }

    public boolean changePassword(ChangePassword changePassword){
        // get the 
        Document doc = tokenRepository.retrieveToken(changePassword.getEmail());
        System.out.println(doc.getLong("dataTime"));
        String originaltoken = doc.getString("token");
        String incomingtoken = changePassword.getToken();
        if(originaltoken.equals(incomingtoken)){
            System.out.println("all is good with the tokens!");

            boolean b = userRepository.updatePassword( changePassword.getPassword(),changePassword.getEmail());
            if(b == true){
                System.out.println("the password was updated successfully");
            } else{
                System.out.println("the password was not updated....");
            }
            return true;
        } else{
            return false;
        }
    }

    public class ReportGenerator extends TimerTask {

		public void run() {
		  System.out.println("Generating report");
		  //TODO generate report
		}
	  
	  }
}
