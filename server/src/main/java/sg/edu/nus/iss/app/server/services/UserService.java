package sg.edu.nus.iss.app.server.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sg.edu.nus.iss.app.server.exception.DeleteAccountException;
import sg.edu.nus.iss.app.server.models.UserProfile;
import sg.edu.nus.iss.app.server.repositories.ChatRepository;
import sg.edu.nus.iss.app.server.repositories.LikeRepository;
import sg.edu.nus.iss.app.server.repositories.UserRepository;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    S3Service s3Service;

    @Autowired
    LikeRepository likeRepository;

    @Autowired
    ChatRepository chatRepository;

    @Transactional(rollbackFor = DeleteAccountException.class)
    public Boolean deleteEntireAccount(String email) throws DeleteAccountException{
        // check if there is a photo
        String photo = userRepository.getExistingUserEmail(email).get().getPhoto();
        System.out.println("==========================> this is the photo: " + photo);
        if(photo != null){
            // delete photo if any
            s3Service.delete(email);
        }
        // check if user profile exists
        Optional<UserProfile> p = userRepository.getExistingProfile(email);
        if(p.isPresent()){
            // get the profile id
            Integer profileId = p.get().getProfileId();
            // delete likes
            likeRepository.deleteLike(profileId);
            // delete chats
            chatRepository.deleteChatId(profileId);
            // delete profile 
            userRepository.deleteProfile(email);
        }

        // delete useraccount
        userRepository.deleteAccount(email);
        return true;

    }
}
