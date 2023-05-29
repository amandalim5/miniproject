package sg.edu.nus.iss.app.server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import sg.edu.nus.iss.app.server.models.User;
import sg.edu.nus.iss.app.server.models.UserProfile;
import sg.edu.nus.iss.app.server.repositories.UserRepository;

@Service
public class LikeService {
    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    UserRepository userRepository;

    public void sendToBothSides(Integer profileOne, Integer profileTwo){
        UserProfile pOne = userRepository.getExistingProfileUsingId(profileOne).get();
        // get the message for profile One
        String profileOneMessage = pOne.getMail();
        // get the email address for profile One
        String profileOneEmail = pOne.getEmail();
        // get displayName of profile One
        String profileOneDisplayName = pOne.getDisplayName();
        // get profileOne's real user name
        String profileOneName = userRepository.getExistingUserEmail(profileOneEmail).get().getUserName();

        UserProfile pTwo = userRepository.getExistingProfileUsingId(profileTwo).get();
        // get the message for profile two
        String profileTwoMessage = pTwo.getMail();
        // get the email address for profile Two
        String profileTwoEmail = pTwo.getEmail();
        // get displayName of profile tWO
        String profileTwoDisplayName = pTwo.getDisplayName();
        // get profileTwo's real user name
        String profileTwoName = userRepository.getExistingUserEmail(profileTwoEmail).get().getUserName();

        // send profileOne's email message to profileTwo's email
        SimpleMailMessage messageOne = new SimpleMailMessage();
        messageOne.setFrom("noreply@cornfield.com");
        messageOne.setTo(profileTwoEmail);
        messageOne.setSubject("Notification from CornField App");
        messageOne.setText("Dear " + profileTwoName + ",\n\n" + profileOneDisplayName + " liked you too. \n\nA message for you: \n\t" + profileOneMessage + "\n\nPlease do not reply to this email, thank you!");
        javaMailSender.send(messageOne);

        // send profileTwo's email message to profileOne's email
        SimpleMailMessage messageTwo = new SimpleMailMessage();
        messageTwo.setFrom("noreply@cornfield.com");
        System.out.println(profileOneEmail + " =========================== this is the problem....?");
        messageTwo.setTo(profileOneEmail);
        messageTwo.setSubject("Notification from CornField App");
        messageTwo.setText("Dear " + profileOneName + ",\n\n" + profileTwoDisplayName + " liked you too. \n\nA message for you: \n\t" + profileTwoMessage  + "\n\nPlease do not reply to this email, thank you!");
        javaMailSender.send(messageTwo);
    }

    public void sendToOneSide(Integer profileLikes, Integer profileLiked){
        
        UserProfile pliked = userRepository.getExistingProfileUsingId(profileLiked).get();
        // get profileLiked's email address
        String likedEmail = pliked.getEmail();
        // get profileLiked's User Name
        User uLiked = userRepository.getExistingUserEmail(likedEmail).get();
        String likedUserName = uLiked.getUserName();
        // get profileLikes's DisplayName
        UserProfile pLikes = userRepository.getExistingProfileUsingId(profileLikes).get();
        String likesDisplayName = pLikes.getDisplayName();
        // get profileLikes's Summary
        String likesSummary = pLikes.getSummary();

        // send mail to liked's email address.
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@cornfield.com");
        message.setTo(likedEmail);
        message.setSubject("Notification from CornField App");
        message.setText("Dear " + likedUserName + ",\n\n" + likesDisplayName + " likes you!\n\nHere's a little more about " + likesDisplayName + ":\n\t" + likesSummary + "\n\nLog into CornField to see more!" + "\n\nPlease do not reply to this email, thank you!");
        javaMailSender.send(message);
    }
}
