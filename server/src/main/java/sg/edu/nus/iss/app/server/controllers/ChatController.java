package sg.edu.nus.iss.app.server.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import sg.edu.nus.iss.app.server.models.ChatMessage;
import sg.edu.nus.iss.app.server.models.UserProfile;
import sg.edu.nus.iss.app.server.repositories.UserRepository;
import sg.edu.nus.iss.app.server.services.ChatService;
import sg.edu.nus.iss.app.server.services.LoginService;

@Controller
@CrossOrigin(origins = "*")
@RequestMapping
public class ChatController {
    @Autowired
    ChatService chatService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    LoginService loginService;

    

    @Value("${DO_STORAGE_BUCKETNAME}")
    private String bucketName;
    @Value("${DO_STORAGE_ENDPOINT}")
    private String endpoint;

    @GetMapping(path = "/getMessages")
    @CrossOrigin(origins = "*")
    @ResponseBody
    public ResponseEntity<String> getMessages(@RequestParam(name = "chatId") String chatId){
        List<ChatMessage> result = chatService.getChatMessages(Integer.parseInt(chatId));
        if(result.size() > 0){
            JsonArray theResult = null;
            JsonArrayBuilder messageArrayBuilder = Json.createArrayBuilder();
            for(ChatMessage c:result){
                messageArrayBuilder.add(ChatMessage.toJson(c));
            }
            theResult = messageArrayBuilder.build();
            return ResponseEntity.ok(theResult.toString());
        } else{
            return ResponseEntity.ok(null);
        }
        
    }

    @GetMapping(path = "/getChatId")
    @CrossOrigin(origins = "*")
    @ResponseBody
    public ResponseEntity<String> getChatId(@RequestParam(name = "email") String email, @RequestParam(name = "otherProfileId") String otherProfileId){
        UserProfile p = userRepository.getExistingProfile(email).get();
        Integer chatId;
        if(p.getGender().equals("male")){
            chatId = chatService.getChatId(Integer.parseInt(otherProfileId), p.getProfileId());
        } else{
            chatId = chatService.getChatId( p.getProfileId(), Integer.parseInt(otherProfileId));
        }
        if(chatId == -1){
            JsonObject badResponse = Json.createObjectBuilder()
                .add("result", false)
                .add("chatId", chatId)
                .build();
            return ResponseEntity.ok(badResponse.toString());  
        }
        UserProfile other = userRepository.getExistingProfileUsingId(Integer.parseInt(otherProfileId)).get();
        String photoName = userRepository.getExistingUserEmail(other.getEmail()).get().getPhoto();
        System.out.println("Checking the photo Name in get chat id: " + photoName);
        String photoUrl = "";
        if(photoName == null || photoName.equals("")){
            photoName = ""; 
        } else{
            photoUrl = "https://" + bucketName + "." + endpoint + "/" + other.getProfileId() + ".png";
        }

        JsonObject response = Json.createObjectBuilder()
            .add("result", true)
            .add("chatId", chatId)
            .add("otherDisplayName", other.getDisplayName())
            .add("photoUrl", photoUrl)
            .build();
        return ResponseEntity.ok(response.toString());   
    }

    @PostMapping(path = "/saveMessage")
    @CrossOrigin(origins = "*")
    @ResponseBody
    public ResponseEntity<String> insertMessage
    (@RequestParam(name = "email") String email, 
    @RequestParam(name = "message") String message, 
    @RequestParam(name = "chatId") Integer chatId){
        System.out.println("=========================> did we get here to save the message?");

        String author = userRepository.getExistingProfile(email).get().getDisplayName();
        chatService.createMessage(author, chatId, message);
        JsonObject response = Json.createObjectBuilder()
            .add("result", true)
            .add("message", loginService.createToken(email))
            .build();
        return ResponseEntity.ok(response.toString());   
    }
    
}
