package sg.edu.nus.iss.app.server.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
// import java.util.StringTokenizer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import sg.edu.nus.iss.app.server.repositories.UserRepository;

@Service
public class S3Service {
    @Autowired
    private AmazonS3 s3Client;

    @Value("${DO_STORAGE_BUCKETNAME}")
    private String bucketName;

    @Autowired
    private UserRepository userRepository;

    public String upload(MultipartFile file, String email) throws IOException{
        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("originalFilename", file.getOriginalFilename());
        userRepository.updatePhotoName(email, file.getOriginalFilename());
        // Integer profileId = userRepository.getExistingProfile(email).get().getProfileId();
        Integer userId = userRepository.getExistingUserEmail(email).get().getUserId();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        metadata.setUserMetadata(userData);
        System.out.println(">>>> " + file.getOriginalFilename());
        // StringTokenizer tk = new StringTokenizer(file.getOriginalFilename(), ".");
        // int count = 0;
        String filenameExt = "png";
        // while(tk.hasMoreTokens()){
        //     if(count == 1){
        //         filenameExt = tk.nextToken();
        //         break;
        //     }else{
        //         filenameExt = tk.nextToken();
        //         count++;
        //     }
        // }
        // if(filenameExt.equals("blob"))
        //     filenameExt = filenameExt + ".png";
        
        PutObjectRequest putRequest = 
            new PutObjectRequest(
                bucketName, "%s.%s".formatted(userId, filenameExt)
                        , file.getInputStream(), metadata);
        putRequest.withCannedAcl(CannedAccessControlList.PublicRead);
        s3Client.putObject(putRequest);
        return "%s.%s".formatted(userId, filenameExt);

    }

    public void delete(String email){
        Integer userId = userRepository.getExistingUserEmail(email).get().getUserId();
        // Integer profileId = userRepository.getExistingProfile(email).get().getProfileId();
        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucketName, userId + ".png");
        s3Client.deleteObject(deleteObjectRequest);

    }
}
