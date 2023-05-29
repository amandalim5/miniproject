package sg.edu.nus.iss.app.server.controllers;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import sg.edu.nus.iss.app.server.models.ChangePassword;
import sg.edu.nus.iss.app.server.models.Login;
import sg.edu.nus.iss.app.server.models.Registration;
import sg.edu.nus.iss.app.server.models.TokenVerification;
import sg.edu.nus.iss.app.server.models.UpdatePassword;
import sg.edu.nus.iss.app.server.models.User;
import sg.edu.nus.iss.app.server.repositories.UserRepository;
import sg.edu.nus.iss.app.server.services.LoginService;
import sg.edu.nus.iss.app.server.services.ResetService;

@Controller
@CrossOrigin(origins = "*")
@RequestMapping
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    LoginService loginService;

    @Autowired 
    ResetService resetService;

    @PostMapping(path = "/register", consumes = "application/x-www-form-urlencoded")
    @CrossOrigin(origins = "*")
	@ResponseBody
    public ResponseEntity<String> registerNewUser(@ModelAttribute Registration registration){
        Optional<User> opt = userRepository.getExistingUserEmail(registration.getUserEmail());
        if(opt.isPresent()){
            JsonObject badResponse = Json.createObjectBuilder()
                .add("result", false)
                .add("message", "The email exists.")
                .build();
            return ResponseEntity.ok(badResponse.toString());
        } else{
            System.out.println(registration.getUserEmail() + "=========email");
            Boolean saved = userRepository.createUser(registration);
            if(saved){
                JsonObject response = Json.createObjectBuilder()
                    .add("result", true)
                    .add("message", "You registered successfully")
                    .build();
                return ResponseEntity.ok(response.toString());
            }
            JsonObject badResponse = Json.createObjectBuilder()
                .add("result", false)
                .add("message", "Registration was unsuccessful.")
                .build();
            return ResponseEntity.ok(badResponse.toString());

        }

    }
    
    @GetMapping(path = "/searchEmails")
    @CrossOrigin(origins = "*")
    @ResponseBody
    public ResponseEntity<String> checkIfEmailExists(@RequestParam(name = "email") String email){
        Optional<User> opt = userRepository.getExistingUserEmail(email);
        System.out.println("to search this email:" + email);
        if(opt.isPresent()){
            System.out.println("the email is in existence. we should not register.");
            JsonObject badResponse = Json.createObjectBuilder()
                .add("result", false)
                .build();
            return ResponseEntity.ok(badResponse.toString());
        }
        System.out.println("the email is not existing, we are good to register/");
        JsonObject response = Json.createObjectBuilder()
            .add("result", true)
            .build();
        return ResponseEntity.ok(response.toString());
    }

    @PostMapping(path = "/login", consumes = "application/x-www-form-urlencoded")
    @CrossOrigin(origins = "*")
    @ResponseBody
    public ResponseEntity<String> login(@ModelAttribute Login login){
        System.out.println(login.getPassword());
        System.out.println("the email is " + login.getEmail() + " and the password is " + login.getPassword());
        String email = login.getEmail();
        Optional<User> opt = userRepository.getExistingUserEmail(email);
        if(!opt.isPresent()){
            JsonObject response = Json.createObjectBuilder()
                .add("result", false)
                .add("message", "Email not found. Please register first.")
                .add("timestamp", (new Date().toString()))
                .build();
            return ResponseEntity.ok(response.toString());
        }
        String dbPassword = opt.get().getUserPassword();
        String givenPassword = login.getPassword();
        Boolean matchingPasswords = loginService.comparePasswords(givenPassword, dbPassword);
        if(matchingPasswords){
            System.out.println("the passwords matched!");
            String token = loginService.createToken(opt.get().getUserEmail());
            System.out.println("the token is: " + token);
            loginService.verifyJWTToken(token);
            JsonObject goodResponse = Json.createObjectBuilder()
                .add("token",token.toString())
                .add("result", true)
                .add("message", "Login successful.")
                .add("timestamp", (new Date().toString()))
                .build();
            return ResponseEntity.ok(goodResponse.toString());
        } else{
            System.out.println("the passwords did not match?!");

            JsonObject badResponse = Json.createObjectBuilder()
                .add("result", false)
                .add("message", "Login unsuccessful.")
                .add("timestamp", (new Date().toString()))
                .build();
            return ResponseEntity.ok(badResponse.toString());
        }
        
    }

    @GetMapping(path = "/token")
    @CrossOrigin(origins = "*")
    @ResponseBody
    public ResponseEntity<String> checkTokenValid(@RequestParam(name = "cornToken") String token){
        TokenVerification t  = loginService.verifyJWTToken(token);
        String newToken = "null";
        String email = "null";
        String username = "null";

        if(t.getResult()){
            newToken = loginService.createToken(t.getEmail());
            System.out.println("created a new token to replace: " + newToken);
            email = t.getEmail();
            Optional<User> opt = userRepository.getExistingUserEmail(email);
            username = opt.get().getUserName();
        }
    
        JsonObject response = Json.createObjectBuilder()
            .add("result", t.getResult())
            .add("useremail", email)
            .add("newToken", newToken)
            .add("username", username)
            .build();
        return ResponseEntity.ok(response.toString());
        
    }

    @GetMapping(path = "/sendResetToken")
    @CrossOrigin(origins = "*")
    @ResponseBody
    public ResponseEntity<String> sendTokenForReset(@RequestParam(name = "email") String email){
        Optional<User> opt = userRepository.getExistingUserEmail(email);
        if(opt.isPresent()){
            resetService.sendResetToken(email);
            JsonObject response = Json.createObjectBuilder()
            .add("result", true)
            .add("message", "We sent the new token.")
            .build();
            return ResponseEntity.ok(response.toString());
        } else{
            JsonObject badResponse = Json.createObjectBuilder()
                .add("result", false)
                .add("message", "There is no such email in the cornfield.")
                .build();
            return ResponseEntity.ok(badResponse.toString());
        }

    }

    @PostMapping(path="/changePassword", consumes = "application/x-www-form-urlencoded")
    @CrossOrigin(origins = "*")
    @ResponseBody
    public ResponseEntity<String> resetPassword(@ModelAttribute ChangePassword changePassword){
        boolean b = resetService.changePassword(changePassword);
        if(b){
            JsonObject response = Json.createObjectBuilder()
            .add("result", true)
            .add("message", "We changed the password.")
            .build();
            return ResponseEntity.ok(response.toString());
        } else{
            JsonObject badResponse = Json.createObjectBuilder()
                .add("result", false)
                .add("message", "The token was wrong.")
                .build();
            return ResponseEntity.ok(badResponse.toString());
        }
    }
    
    @PutMapping(path="/updatePassword", consumes = {"application/x-www-form-urlencoded","application/json"})
    @CrossOrigin(origins = "*")
    @ResponseBody
    public ResponseEntity<String> updatePassword(@ModelAttribute UpdatePassword updatePassword){
        // get the email address
        System.out.println("_____________" + updatePassword.getToken() + "-----------------");
        TokenVerification t = loginService.verifyJWTToken(updatePassword.getToken());
        if(!t.getResult()){
            JsonObject badResponse = Json.createObjectBuilder()
                .add("result", false)
                .add("message", "The token was not valid.")
                .build();
            return ResponseEntity.ok(badResponse.toString());
        }
        // update password
        Boolean b = userRepository.updatePassword(updatePassword.getNewpassword(), t.getEmail());
        if(b){
            JsonObject response = Json.createObjectBuilder()
                .add("result", true)
                .add("message", "Successfully changed password!")
                .build();
            return ResponseEntity.ok(response.toString());   
        } else{
            JsonObject badResponse = Json.createObjectBuilder()
                .add("result", false)
                .add("message", "The password was not changed.")
                .build();
            return ResponseEntity.ok(badResponse.toString());
        }
    }

    @GetMapping(path = "/getUserDetails")
    @CrossOrigin(origins = "*")
    @ResponseBody
    public ResponseEntity<String> getUser(@RequestParam(name = "email") String email){
        Optional<User> opt = userRepository.getExistingUserEmail(email);
        if(opt.isPresent()){
            User u = opt.get();
            JsonObject response = Json.createObjectBuilder()
                .add("likes", u.getLikes())
                .add("checks", u.getChecks())
                .add("email", u.getUserEmail())
                .build();
            return ResponseEntity.ok(response.toString());
        } else{
            return ResponseEntity.ok(null);
        }
        
        
    }
}
