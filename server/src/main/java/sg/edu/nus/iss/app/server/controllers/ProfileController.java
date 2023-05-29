package sg.edu.nus.iss.app.server.controllers;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import sg.edu.nus.iss.app.server.exception.DeleteAccountException;
import sg.edu.nus.iss.app.server.models.DistanceMatrix;
import sg.edu.nus.iss.app.server.models.UserProfile;
import sg.edu.nus.iss.app.server.repositories.LikeRepository;
import sg.edu.nus.iss.app.server.repositories.UserRepository;
import sg.edu.nus.iss.app.server.services.ChatService;
import sg.edu.nus.iss.app.server.services.DistanceService;
import sg.edu.nus.iss.app.server.services.LikeService;
import sg.edu.nus.iss.app.server.services.LoginService;
import sg.edu.nus.iss.app.server.services.ResetService;
import sg.edu.nus.iss.app.server.services.UserService;

@Controller
@CrossOrigin(origins = "*")
@RequestMapping
public class ProfileController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    LoginService loginService;

    @Autowired
    DistanceService distanceService;

    @Autowired 
    ResetService resetService;

    @Autowired
    LikeService likeService;

    @Autowired
    LikeRepository likeRepository;

    @Autowired
    UserService userService;

    @Autowired
    ChatService chatService;

    @Value("${DO_STORAGE_BUCKETNAME}")
    private String bucketName;
    @Value("${DO_STORAGE_ENDPOINT}")
    private String endpoint;

    @PostMapping(path = "/updateProfile", consumes = "application/x-www-form-urlencoded" )
    @CrossOrigin(origins = "*")
    @ResponseBody
    public ResponseEntity<String> updateProfile(@ModelAttribute UserProfile userProfile){
        String gender = userRepository.getExistingUserEmail(userProfile.getEmail()).get().getGender();
        System.out.println("the user's gender is +++++++++++++++++++++++++ " + gender);
        Optional<UserProfile> result = userRepository.getExistingProfile(userProfile.getEmail());
        Boolean r = null;
        String message = null;

        if(!result.isPresent()){
            r = userRepository.createProfile(userProfile.getEmail(), userProfile, gender);
            message = "New profile was created!";
        } else{
            r = userRepository.updateProfile(userProfile.getEmail(), userProfile);
            message = "The user's profile was updated!";
        }

        if(r){
            JsonObject response = Json.createObjectBuilder()
                .add("result", r)
                .add("message", message)
                .build();
            return ResponseEntity.ok(response.toString());   
        } else{
            JsonObject badResponse = Json.createObjectBuilder()
                .add("result", r)
                .add("message", "Failed to create/update the profile.")
                .build();
            return ResponseEntity.ok(badResponse.toString());
        }
    }

    @GetMapping(path = "/getProfile")
    @CrossOrigin(origins = "*")
    @ResponseBody
    public ResponseEntity<String> getProfile(@RequestParam(name = "email") String email){
        Optional<UserProfile> result = userRepository.getExistingProfile(email);
        if(!result.isPresent()){
            JsonObject badResponse = Json.createObjectBuilder()
                .add("result", false)
                .add("message", "The profile is not found. Please create and save a profile.")
                .build();
            return ResponseEntity.ok(badResponse.toString());
        } else{
            UserProfile p = result.get();
            Calendar c = Calendar.getInstance();
            c.set(p.getBirthyear(), p.getBirthmonth() - 1, p.getBirthday());
            Calendar today = Calendar.getInstance();
            int age = today.get(Calendar.YEAR) - c.get(Calendar.YEAR);
            int monthdiff = today.get(Calendar.MONTH) - c.get(Calendar.MONTH);
            if(monthdiff < 0 || (monthdiff == 0  && today.get(Calendar.DATE) < c.get(Calendar.DATE))){
                age--;
            }
            System.out.println("the age is: " + age);

            String photoName = userRepository.getExistingUserEmail(email).get().getPhoto();
            String photoUrl = "";
            if(photoName == null){
               photoName = ""; 
            } else{
                photoUrl = "https://" + bucketName + "." + endpoint + "/" + p.getProfileId() + ".png";
            }
            // todo: get the age and put into the result - done
            // todo: get the distance in the server with google map and add to the result (use workshop 17!)
            JsonObject response = Json.createObjectBuilder()
                .add("result", true)
                .add("message", "There was an existing profile.")
                .add("profileId", p.getProfileId())
                .add("email", p.getEmail())
                .add("profileIsPublic", p.getProfileIsPublic())
                .add("displayName", p.getDisplayName())
                .add("summary", p.getSummary())
                .add("birthday", p.getBirthday())
                .add("birthmonth", p.getBirthmonth())
                .add("birthyear", p.getBirthyear())
                .add("height", p.getHeight())
                .add("weight", p.getWeight())
                .add("isSmoking", p.getIsSmoking())
                .add("gender", p.getGender())
                .add("age",age)
                .add("postalCode", p.getPostalCode())
                .add("mail", p.getMail())
                .add("photo",photoName)
                .add("photoUrl", photoUrl)
                .build();
            return ResponseEntity.ok(response.toString());   
        }
    }

    @GetMapping(path = "/getDistance")
    @CrossOrigin(origins = "*")
    @ResponseBody
    public ResponseEntity<String> getDistance(@RequestParam(name = "userprofileId") Integer userprofileId, @RequestParam(name = "otherprofileId") Integer otherprofileId) throws IOException{

        Optional<UserProfile> result = userRepository.getExistingProfileUsingId(userprofileId);
        String postalOne = result.get().getPostalCode();
        String postalTwo;
        if(otherprofileId < 0){
            postalTwo = "467360";
        } else{
            Optional<UserProfile> r = userRepository.getExistingProfileUsingId(otherprofileId);
            postalTwo = r.get().getPostalCode();
        }
        Optional<DistanceMatrix> d = distanceService.getDistance(postalOne, postalTwo);
        JsonObject response = Json.createObjectBuilder()
            .add("distance", d.get().getDistance())
            .add("status", d.get().getStatus())
            .build();
        return ResponseEntity.ok(response.toString()); 
        
    }



    @DeleteMapping(path = "/deleteProfile")
    @CrossOrigin(origins = "*")
    @ResponseBody
    public ResponseEntity<String> deleteProfile(@RequestParam(name = "email") String email){
        Boolean b = userRepository.deleteProfile(email);
        if(b){
            JsonObject response = Json.createObjectBuilder()
                .add("result", b)
                .add("message", "Profile is cleared.")
                .build();
            return ResponseEntity.ok(response.toString());  
        } else{
            JsonObject badResponse = Json.createObjectBuilder()
                .add("result", b)
                .add("message", "Profile was not deleted.")
                .build();
            return ResponseEntity.ok(badResponse.toString());
        }
    }

    @DeleteMapping(path = "/deleteAccount")
    @CrossOrigin(origins = "*")
    @ResponseBody
    public ResponseEntity<String> deleteAccount(@RequestParam(name = "email") String email) throws DeleteAccountException{
        Boolean deleted = userService.deleteEntireAccount(email);
        if(deleted){
            JsonObject response = Json.createObjectBuilder()
                        .add("result", true)
                        .add("message", "Account is deleted.")
                        .build();
                    return ResponseEntity.ok(response.toString());  
        } else{
            JsonObject badResponse = Json.createObjectBuilder()
                            .add("result", false)
                            .add("message", "Account was not deleted.")
                            .build();
                        return ResponseEntity.ok(badResponse.toString());
        }
        // check if there is a profile
        // Optional<UserProfile> profile = userRepository.getExistingProfile(email);
        // if(profile.isPresent()){
        //     // delete the profile
        //     Boolean p = userRepository.deleteProfile(email);
        //     if(!p){
        //         JsonObject badresponse = Json.createObjectBuilder()
        //             .add("result", p)
        //             .add("message", "Please try again, delete of profile failed, hence account was not deleted.")
        //             .build();
        //         return ResponseEntity.ok(badresponse.toString());  
        //     } else{
        //         Boolean b = userRepository.deleteAccount(email);
        //         if(b){
        //             JsonObject response = Json.createObjectBuilder()
        //                 .add("result", b)
        //                 .add("message", "Account is deleted.")
        //                 .build();
        //             return ResponseEntity.ok(response.toString());  
        //         } else{
        //             JsonObject badResponse = Json.createObjectBuilder()
        //                 .add("result", b)
        //                 .add("message", "Account was not deleted.")
        //                 .build();
        //             return ResponseEntity.ok(badResponse.toString());
        //         } 
        //     }
        // } else{
        //     Boolean b = userRepository.deleteAccount(email);
        //         if(b){
        //             JsonObject response = Json.createObjectBuilder()
        //                 .add("result", b)
        //                 .add("message", "Account is deleted.")
        //                 .build();
        //             return ResponseEntity.ok(response.toString());  
        //         } else{
        //             JsonObject badResponse = Json.createObjectBuilder()
        //                 .add("result", b)
        //                 .add("message", "Account was not deleted.")
        //                 .build();
        //             return ResponseEntity.ok(badResponse.toString());
        //         } 
        // }
        
       
    }

    @GetMapping(path = "/getProfileIdOpp")
    @CrossOrigin(origins = "*")
    @ResponseBody
    public ResponseEntity<String> getListOfProfileId(@RequestParam(name = "email") String email){
        String usergender = userRepository.getExistingUserEmail(email).get().getGender();
        String gender;
        if(usergender.equals("male")){
            gender = "female";
            System.out.println("looking for females");
        } else if(usergender.equals("female")){
            gender = "male";
            System.out.println("looking for males");
        } else{
            gender = "null";
            System.out.println("There was no gender! this should not happen....");
        }
        Optional<List<UserProfile>> result = userRepository.getListOfProfiles(gender);
        if(result.isPresent() && result.get().size() > 0){
            System.out.println("the result had something" + result.get().size());
            JsonArray theResult = null;
            List<UserProfile> r = result.get();
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            for(UserProfile u: r){
                arrayBuilder.add(UserProfile.toJsonId(u));
            }
            theResult = arrayBuilder.build();
            return ResponseEntity.ok(theResult.toString());

        }
        System.out.println("the list was empty");
        return ResponseEntity.ok(null);
    
    }

    @GetMapping(path = "/getProfileIdByName")
    @CrossOrigin(origins = "*")
    @ResponseBody
    public ResponseEntity<String> getListOfProfileIdByName(@RequestParam(name = "displayName") String displayName, @RequestParam(name = "email") String email){
        String usergender = userRepository.getExistingUserEmail(email).get().getGender();
        System.out.println("this is the name we are searching for........................... " + displayName);
        String gender;
        if(usergender.equals("male")){
            gender = "female";
            System.out.println("looking for females");
        } else if(usergender.equals("female")){
            gender = "male";
            System.out.println("looking for males");
        } else{
            gender = "null";
            System.out.println("There was no gender! this should not happen....");
        }

        Optional<List<UserProfile>> result = userRepository.getListOfProfilesByDisplayName(gender, displayName);
        if(result.isPresent() && result.get().size() > 0){
            System.out.println("the result had something" + result.get().size());
            JsonArray theResult = null;
            List<UserProfile> r = result.get();
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            for(UserProfile u: r){
                arrayBuilder.add(UserProfile.toJsonId(u));
            }
            theResult = arrayBuilder.build();
            return ResponseEntity.ok(theResult.toString());

        }
        System.out.println("the list was empty");
        return ResponseEntity.ok(null);
    }


    @GetMapping(path = "/getProfileBySearchTerm")
    @CrossOrigin(origins = "*")
    @ResponseBody
    public ResponseEntity<String> getListOfProfileIdBySearchTerm(@RequestParam(name = "searchTerm") String searchTerm, @RequestParam(name = "email") String email){
        String usergender = userRepository.getExistingUserEmail(email).get().getGender();
        String gender;
        if(usergender.equals("male")){
            gender = "female";
            System.out.println("looking for females");
        } else if(usergender.equals("female")){
            gender = "male";
            System.out.println("looking for males");
        } else{
            gender = "null";
            System.out.println("There was no gender! this should not happen....");
        }

        Optional<List<UserProfile>> result = userRepository.getListOfProfilesBySearchTerm(gender, searchTerm);
        if(result.isPresent() && result.get().size() > 0){
            System.out.println("the result had something" + result.get().size());
            JsonArray theResult = null;
            List<UserProfile> r = result.get();
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            for(UserProfile u: r){
                arrayBuilder.add(UserProfile.toJsonId(u));
            }
            theResult = arrayBuilder.build();
            return ResponseEntity.ok(theResult.toString());

        }
        System.out.println("the list was empty");
        return ResponseEntity.ok(null);
    }

    @GetMapping(path = "/getProfileById")
    @CrossOrigin(origins = "*")
    @ResponseBody
    public ResponseEntity<String> getProfileById(@RequestParam(name = "profileId") String profileId, @RequestParam(name = "email") String email){
        UserProfile p = userRepository.getExistingProfileUsingId(Integer.parseInt(profileId)).get();
        Calendar c = Calendar.getInstance();
        c.set(p.getBirthyear(), p.getBirthmonth() - 1, p.getBirthday());
        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - c.get(Calendar.YEAR);
        int monthdiff = today.get(Calendar.MONTH) - c.get(Calendar.MONTH);
        if(monthdiff < 0 || (monthdiff == 0  && today.get(Calendar.DATE) < c.get(Calendar.DATE))){
            age--;
        }
        System.out.println("the age is: " + age);
        String newToken = loginService.createToken(email); 
        String photoName = userRepository.getExistingUserEmail(p.getEmail()).get().getPhoto();
        String photoUrl = "";
        if(photoName == null){
           photoName = ""; 
        } else{
            photoUrl = "https://" + bucketName + "." + endpoint + "/" + p.getProfileId() + ".png";
        }
        JsonObject response = Json.createObjectBuilder()
                .add("profileId", p.getProfileId())
                .add("email", p.getEmail())
                .add("profileIsPublic", p.getProfileIsPublic())
                .add("displayName", p.getDisplayName())
                .add("summary", p.getSummary())
                .add("birthday", p.getBirthday())
                .add("birthmonth", p.getBirthmonth())
                .add("birthyear", p.getBirthyear())
                .add("height", p.getHeight())
                .add("weight", p.getWeight())
                .add("isSmoking", p.getIsSmoking())
                .add("gender", p.getGender())
                .add("age",age)
                .add("postalCode", p.getPostalCode())
                .add("token", newToken)
                .add("photo",photoName)
                .add("photoUrl", photoUrl)
                .build();
            return ResponseEntity.ok(response.toString());   
    }

    @GetMapping(path = "/getDistanceUsingEmail")
    @CrossOrigin(origins = "*")
    @ResponseBody
    public ResponseEntity<String> getDistanceUsingEmail(@RequestParam(name = "email") String email, @RequestParam(name = "otherprofileId") Integer otherprofileId) throws IOException{
        Optional<UserProfile> opt = userRepository.getExistingProfile(email);
        if(!opt.isPresent()){
            JsonObject badresponse = Json.createObjectBuilder()
                .add("distance", "null")
                .add("status", "User has no profile")
                .build();
            return ResponseEntity.ok(badresponse.toString()); 
        } else{
            // Optional<UserProfile> result = userRepository.getExistingProfileUsingId(userprofileId);
            String postalOne = opt.get().getPostalCode();
            if(postalOne == null || postalOne.equals("")){
                JsonObject badresponse = Json.createObjectBuilder()
                .add("distance", "null")
                .add("status", "User has no postal code in profile")
                .build();
            return ResponseEntity.ok(badresponse.toString()); 
            } 
            String postalTwo;
            if(otherprofileId < 0){
                postalTwo = "467360";
            } else{
                Optional<UserProfile> r = userRepository.getExistingProfileUsingId(otherprofileId);
                userRepository.removeOneCheck(email);
                
                postalTwo = r.get().getPostalCode();
            }
            Optional<DistanceMatrix> d = distanceService.getDistance(postalOne, postalTwo);
            JsonObject response = Json.createObjectBuilder()
                .add("distance", d.get().getDistance())
                .add("status", d.get().getStatus())
                .build();
            return ResponseEntity.ok(response.toString());            
        }
    }

    @GetMapping(path = "/likeAPerson")
    @CrossOrigin(origins = "*")
    @ResponseBody
    public ResponseEntity<String> likeAPerson(@RequestParam(name = "email") String email, @RequestParam(name = "otherprofileId") Integer otherprofileId){
        Optional<UserProfile> opt = userRepository.getExistingProfile(email);
        if(!opt.isPresent()){
            JsonObject badresponse = Json.createObjectBuilder()
                .add("message", "You have no profile yet. Please create one first!")
                .build();
            return ResponseEntity.ok(badresponse.toString()); 
        }
        if(!opt.get().getProfileIsPublic()){
            JsonObject badresponse = Json.createObjectBuilder()
                .add("message", "Your profile is not public.")
                .build();
            return ResponseEntity.ok(badresponse.toString()); 
        }
        Integer userProfile = opt.get().getProfileId();
        Boolean b = likeRepository.checkIfCombiExists(userProfile, otherprofileId);
        if(b){
            JsonObject badresponse = Json.createObjectBuilder()
                .add("message", "You already liked this profile!")
                .build();
            return ResponseEntity.ok(badresponse.toString()); 
        } else{
            //remove a like from likes count in user repo
            Boolean removed = userRepository.removeOneLike(email);
            if(!removed){
                System.out.println("this should not happen....");
            }

            // add the combi
            Boolean added = likeRepository.addTheCombi(userProfile, otherprofileId);
            if(added){
                // check if the reverse combi exists
                Boolean bo = likeRepository.checkIfCombiExists(otherprofileId, userProfile);
                if(bo){
                    // send the email
                    likeService.sendToBothSides(otherprofileId, userProfile);
                    // create a chat id
                    String genderOfUser = opt.get().getGender();
                    Integer chatId;
                    if(genderOfUser.equals("female")){
                        chatId = chatService.createChatId(userProfile, otherprofileId);
                    } else{
                        chatId = chatService.createChatId(otherprofileId, userProfile);
                    }
                    System.out.println("create chat id: " + chatId);
                    JsonObject response = Json.createObjectBuilder()
                        .add("message", "This user liked you too!")
                        .build();
                    return ResponseEntity.ok(response.toString()); 

                } else{
                    // send a notification email to the liked one...
                    likeService.sendToOneSide(userProfile, otherprofileId);
                    JsonObject response = Json.createObjectBuilder()
                        .add("message", "Liked the user!")
                        .build();
                    return ResponseEntity.ok(response.toString()); 

                }
            } else{
                System.out.println("Did not manage to add the combi, this should not happen...");
                JsonObject badresponse = Json.createObjectBuilder()
                    .add("message", "The Like was not added. This should not happen.")
                    .build();
                return ResponseEntity.ok(badresponse.toString()); 
            }
           

        }

    }

    @GetMapping(path = "/likeAPersonBack")
    @CrossOrigin(origins = "*")
    @ResponseBody
    public ResponseEntity<String> likeAPersonWhoLikedAlready(@RequestParam(name = "email") String email, @RequestParam(name = "otherprofileId") Integer otherprofileId){
        Optional<UserProfile> opt = userRepository.getExistingProfile(email);
        if(!opt.isPresent()){
            JsonObject badresponse = Json.createObjectBuilder()
                .add("message", "You have no profile yet. Please create one first!")
                .build();
            return ResponseEntity.ok(badresponse.toString()); 
        }
        if(!opt.get().getProfileIsPublic()){
            JsonObject badresponse = Json.createObjectBuilder()
                .add("message", "Your profile is not public.")
                .build();
            return ResponseEntity.ok(badresponse.toString()); 
        }
        Integer userProfile = opt.get().getProfileId();
        Boolean b = likeRepository.checkIfCombiExists(userProfile, otherprofileId);
        if(b){
            JsonObject badresponse = Json.createObjectBuilder()
                .add("message", "You already liked this profile!")
                .build();
            return ResponseEntity.ok(badresponse.toString()); 
        } else{

            // add the combi
            Boolean added = likeRepository.addTheCombi(userProfile, otherprofileId);
            if(added){
                // check if the reverse combi exists
                Boolean bo = likeRepository.checkIfCombiExists(otherprofileId, userProfile);
                if(bo){
                    // send the email
                    likeService.sendToBothSides(otherprofileId, userProfile);
                    // create a chat id
                    String genderOfUser = opt.get().getGender();
                    Integer chatId;
                    if(genderOfUser.equals("female")){
                        chatId = chatService.createChatId(userProfile, otherprofileId);
                    } else{
                        chatId = chatService.createChatId(otherprofileId, userProfile);
                    }
                    System.out.println("create chat id: " + chatId);
                    JsonObject response = Json.createObjectBuilder()
                        .add("message", "You are now matched!")
                        .build();
                    return ResponseEntity.ok(response.toString()); 

                } else{
                    // send a notification email to the liked one...
                    likeService.sendToOneSide(userProfile, otherprofileId);
                    JsonObject response = Json.createObjectBuilder()
                        .add("message", "Liked the user!")
                        .build();
                    return ResponseEntity.ok(response.toString()); 

                }
            } else{
                System.out.println("Did not manage to add the combi, this should not happen...");
                JsonObject badresponse = Json.createObjectBuilder()
                    .add("message", "The Like was not added. This should not happen.")
                    .build();
                return ResponseEntity.ok(badresponse.toString()); 
            }
           

        }

    }
    @GetMapping(path = "/getProfilesWhoLikeUser")
    @CrossOrigin(origins = "*")
    @ResponseBody
    public ResponseEntity<String> getListOfPeopleLikeUser(@RequestParam(name = "email") String email){
        Integer userProfileId = userRepository.getExistingProfile(email).get().getProfileId();
        Optional<List<UserProfile>> result = likeRepository.getThePeopleWhoLikeUser(userProfileId);
        if(result.isPresent() && result.get().size() > 0){
            System.out.println("the result had something" + result.get().size());
            JsonArray theResult = null;
            List<UserProfile> r = result.get();
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            for(UserProfile p: r){
                Boolean liked = likeRepository.checkIfCombiExists(userProfileId, p.getProfileId());
                Calendar c = Calendar.getInstance();
                c.set(p.getBirthyear(), p.getBirthmonth() - 1, p.getBirthday());
                Calendar today = Calendar.getInstance();
                int age = today.get(Calendar.YEAR) - c.get(Calendar.YEAR);
                int monthdiff = today.get(Calendar.MONTH) - c.get(Calendar.MONTH);
                if(monthdiff < 0 || (monthdiff == 0  && today.get(Calendar.DATE) < c.get(Calendar.DATE))){
                    age--;
                }
                System.out.println("the age is: " + age);
                String photoName = userRepository.getExistingUserEmail(p.getEmail()).get().getPhoto();
                String photoUrl = "";
                if(photoName == null){
                photoName = ""; 
                } else{
                    photoUrl = "https://" + bucketName + "." + endpoint + "/" + p.getProfileId() + ".png";
                }
                arrayBuilder.add(UserProfile.toJson(p, age, photoName, photoUrl, liked));
            }
            theResult = arrayBuilder.build();
            return ResponseEntity.ok(theResult.toString());

        }
        System.out.println("the list was empty");
        return ResponseEntity.ok(null);
    
    }

    @GetMapping(path = "getProfileThatLikedUser")
    @CrossOrigin(origins = "*")
    @ResponseBody
    public ResponseEntity<String> getProfileThatLikedUser(@RequestParam(name = "email") String email, @RequestParam(name = "otherProfileId") String otherProfileId){
        // check that the combi exists (in case the user changed the url manually...)
        Integer userProfileId = userRepository.getExistingProfile(email).get().getProfileId();
        Boolean validrequest = likeRepository.checkIfCombiExists(Integer.parseInt(otherProfileId), userProfileId);
        if(validrequest){
            Boolean mutual = likeRepository.checkIfCombiExists(userProfileId, Integer.parseInt(otherProfileId));
            if(mutual){
                JsonObject badRequest = Json.createObjectBuilder()
                .add("result", false)
                .add("message", "The user was already matched.")
                .build();
            return ResponseEntity.ok(badRequest.toString());
            } else{
                UserProfile p = userRepository.getExistingProfileUsingId(Integer.parseInt(otherProfileId)).get();
                Calendar c = Calendar.getInstance();
                c.set(p.getBirthyear(), p.getBirthmonth() - 1, p.getBirthday());
                Calendar today = Calendar.getInstance();
                int age = today.get(Calendar.YEAR) - c.get(Calendar.YEAR);
                int monthdiff = today.get(Calendar.MONTH) - c.get(Calendar.MONTH);
                if(monthdiff < 0 || (monthdiff == 0  && today.get(Calendar.DATE) < c.get(Calendar.DATE))){
                    age--;
                }
                System.out.println("the age is: " + age);
                String newToken = loginService.createToken(email); 
                String photoName = userRepository.getExistingUserEmail(p.getEmail()).get().getPhoto();
                String photoUrl = "";
                if(photoName == null){
                   photoName = ""; 
                } else{
                    photoUrl = "https://" + bucketName + "." + endpoint + "/" + p.getProfileId() + ".png";
                }
                JsonObject response = Json.createObjectBuilder()
                        .add("result", true)
                        .add("profileId", p.getProfileId())
                        .add("email", p.getEmail())
                        .add("profileIsPublic", p.getProfileIsPublic())
                        .add("displayName", p.getDisplayName())
                        .add("summary", p.getSummary())
                        .add("birthday", p.getBirthday())
                        .add("birthmonth", p.getBirthmonth())
                        .add("birthyear", p.getBirthyear())
                        .add("height", p.getHeight())
                        .add("weight", p.getWeight())
                        .add("isSmoking", p.getIsSmoking())
                        .add("gender", p.getGender())
                        .add("age",age)
                        .add("postalCode", p.getPostalCode())
                        .add("token", newToken)
                        .add("photo",photoName)
                        .add("photoUrl", photoUrl)
                        .build();
                    return ResponseEntity.ok(response.toString());  
            }
             
        } else{
            JsonObject badRequest = Json.createObjectBuilder()
                .add("result", false)
                .add("message", "This was not a valid request.")
                .build();
            return ResponseEntity.ok(badRequest.toString());
        }
        

    }
}
