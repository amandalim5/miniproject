package sg.edu.nus.iss.app.server.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import sg.edu.nus.iss.app.server.repositories.UserRepository;
import sg.edu.nus.iss.app.server.services.S3Service;

@Controller
public class FileUploadController {
    @Autowired
    private S3Service s3Svc;

    @Autowired
    UserRepository userRepository;

    @PostMapping(path="/upload",
            consumes=MediaType.MULTIPART_FORM_DATA_VALUE,
            produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> upload(
        @RequestPart MultipartFile file,
        @RequestPart String email
    ){
        String key = "";
        try{
            key = s3Svc.upload(file,email);
        }catch(IOException e){
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.getMessage());
        }
        JsonObject payload = Json.createObjectBuilder()
            .add("imageKey", key)
            .build();
            
        return ResponseEntity.ok(payload.toString());
    }

    @PutMapping(path="/deletepic")
    @CrossOrigin(origins = "*")
    @ResponseBody
    public ResponseEntity<String> deletePic(@RequestParam(name = "email") String email){
        Boolean b = userRepository.updatePhotoName(email, "");
        s3Svc.delete(email);
        if(b){
            JsonObject response = Json.createObjectBuilder()
                .add("result", b)
                .add("message", "Photo is deleted.")
                .build();
            return ResponseEntity.ok(response.toString());  
        } else{
            JsonObject badResponse = Json.createObjectBuilder()
                .add("result", b)
                .add("message", "Photo was not deleted.")
                .build();
            return ResponseEntity.ok(badResponse.toString());
        }
    }

    

}
