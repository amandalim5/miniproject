package sg.edu.nus.iss.app.server.repositories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import sg.edu.nus.iss.app.server.models.Registration;
import sg.edu.nus.iss.app.server.models.User;
import sg.edu.nus.iss.app.server.models.UserProfile;

@Repository
public class UserRepository {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JdbcTemplate template;
    
    public Optional<User> getExistingUserEmail(final String email){
      final SqlRowSet rs = template.queryForRowSet("select * from users where user_email = ?",email);
      if(rs.first()){
         return Optional.of(User.populate(rs));
      }
      return Optional.empty();
   }  
   
   
   public Boolean createUser(Registration registration){
      String hiddenPassword = passwordEncoder.encode(registration.getUserPassword());
      System.out.println("executing the bcrypt " + hiddenPassword);
      int added = template.update("insert into users (user_name, user_email, user_password, gender, likes, checks) values (?, ?, ?, ?, 1, 3)"
         , registration.getUserName(), registration.getUserEmail(), hiddenPassword, registration.getGender());
      return added > 0;
   }
   
   
   public Boolean updatePassword(String password, String email){
      String hiddenPassword = passwordEncoder.encode(password);
      int updated = template.update("update users set user_password = ? where user_email = ?", hiddenPassword, email);
      return updated > 0;
   }
   
   
   public Boolean createProfile(String email, UserProfile userProfile, String gender){
      int added = template.update("insert into profiles (email, profile_is_public, display_name, summary, birthday, birthmonth, birthyear, height, weight, is_smoking, gender, postal_code, mail) values (?,?,?,?,?,?,?,?,?,?,?,?,?)", 
         email, 
         userProfile.getProfileIsPublic(), 
         userProfile.getDisplayName(), 
         userProfile.getSummary(), 
         userProfile.getBirthday(),
         userProfile.getBirthmonth(),
         userProfile.getBirthyear(),
         userProfile.getHeight(), 
         userProfile.getWeight(), 
         userProfile.getIsSmoking(),
         gender,
         userProfile.getPostalCode(),
         userProfile.getMail() );
         
      return added > 0;
   }
   
   
   public Boolean updateProfile(String email, UserProfile userProfile){
      int updated = template.update("update profiles set profile_is_public = ?, display_name = ?, summary = ?, birthday = ?, birthmonth = ?, birthyear = ?, height = ?, weight = ?, is_smoking = ?, postal_code = ?, mail = ? where email = ?", 
         userProfile.getProfileIsPublic(), 
         userProfile.getDisplayName(), 
         userProfile.getSummary(), 
         userProfile.getBirthday(),
         userProfile.getBirthmonth(),
         userProfile.getBirthyear(),
         userProfile.getHeight(), 
         userProfile.getWeight(), 
         userProfile.getIsSmoking(),
         userProfile.getPostalCode(),
         userProfile.getMail(),
         email );
         
      return updated > 0;
   }

   public Boolean updatePhotoName(String email, String photoName){
      int updated = template.update("update users set photo = ? where user_email = ?", photoName, email );
      return updated > 0;
   }

   public Boolean updateLikesAndChecks(){
      int updated = template.update("update users set likes = 1, checks = 3");
      return updated > 0;
   }

   public Boolean removeOneLike(String email){
      int updated = template.update("update users set likes = likes - 1 where user_email = ?", email);
      return updated > 0;
   }

   public Boolean removeOneCheck(String email){
      int updated = template.update("update users set checks = checks - 1 where user_email = ?", email);
      return updated > 0;
   }
   
   
   public Optional<UserProfile> getExistingProfile(String email){
      final SqlRowSet rs = template.queryForRowSet("select * from profiles where email = ?",email);
      if(rs.first()){
          return Optional.of(UserProfile.populate(rs));
      }
      return Optional.empty();
   }
   
   
   public Optional<UserProfile> getExistingProfileUsingId(Integer id){
      final SqlRowSet rs = template.queryForRowSet("select * from profiles where profile_id = ?",id);
      if(rs.first()){
          return Optional.of(UserProfile.populate(rs));
      }
      return Optional.empty();
   }
     
   public Boolean deleteProfile(String email){
      int deleted = template.update("delete from profiles where email = ?", email);
      // todo: delete the user profile from the table which links the users to chosen profiles (liked profiles)
      return deleted > 0;
   }
   
   
   public Boolean deleteAccount(String email){
      int deleted = template.update("delete from users where user_email = ?", email);
      return deleted > 0;
   }

   public Optional<List<UserProfile>> getListOfProfiles(String oppositeGender){
      final SqlRowSet rs = template.queryForRowSet("select * from profiles where gender = ? and profile_is_public = true", oppositeGender);
      return Optional.of(listbuilder(rs));


   }

   private List<UserProfile> listbuilder(SqlRowSet rs){
      final List<UserProfile> result = new ArrayList<UserProfile>();
      while(rs.next()){
         UserProfile user = new UserProfile();
         user.setProfileId(rs.getInt("profile_id"));
         result.add(user);
      }
      return Collections.unmodifiableList(result);
   }
    
   public Optional<List<UserProfile>> getListOfProfilesByDisplayName(String oppositeGender, String displayName){
      final SqlRowSet rs = template.queryForRowSet("select * from profiles where gender = ? and display_name like '%" + displayName + "%'",oppositeGender );
      return Optional.of(listbuilder(rs));
   }

   public Optional<List<UserProfile>> getListOfProfilesBySearchTerm(String oppositeGender, String searchTerm){
      final SqlRowSet rs = template.queryForRowSet("select * from profiles where gender = ? and summary like '%" + searchTerm + "%'",oppositeGender );
      return Optional.of(listbuilder(rs));
   }
}
