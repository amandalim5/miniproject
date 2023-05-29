package sg.edu.nus.iss.app.server.repositories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import sg.edu.nus.iss.app.server.models.UserProfile;

@Repository
public class LikeRepository {
    @Autowired
    private JdbcTemplate template;

    public Boolean checkIfCombiExists(final Integer profileIdLikes, final Integer profileIfLiked){
        final SqlRowSet rs = template.queryForRowSet("select * from likes where profile_likes_id = ? and profile_liked_id = ?", profileIdLikes, profileIfLiked);
        Integer number = 0;
        System.out.println("================== checking if profile " + profileIdLikes + " likes profile " + profileIfLiked);
        while(rs.next()){
            System.out.println(rs.getInt("profile_likes_id"));
            number++;
        }
        if(rs.first()){
            System.out.println("LikeRepository: there was an existing combi");
            return true;
        } else{
            System.out.println("LikeRepository: there was NO existing combi");
            return false;
        }
    }

    public Boolean addTheCombi(final Integer profileIdLikes, final Integer profileIdLiked){
        int added = template.update("insert into likes (profile_likes_id, profile_liked_id) values (?, ?)", profileIdLikes, profileIdLiked);

        return added > 0;
    }

    public Optional<List<UserProfile>> getThePeopleWhoLikeUser(final Integer userProfileId){
        final SqlRowSet rs = template.queryForRowSet("select * from likes where profile_liked_id = ?", userProfileId);
        List<Integer> list = new ArrayList<Integer>();
        if(rs.first()){
            list.add(rs.getInt("profile_likes_id"));
            System.out.println(rs.getInt("profile_likes_id") + " +++++++++++++++++++ testing testing 123");
            while(rs.next()){
                list.add(rs.getInt("profile_likes_id"));
                System.out.println("this user liked the user: " + rs.getInt("profile_likes_id"));
            }
            String theSearch = "and profile_id in (";
            for(int i=0 ; i<list.size(); i++){
                theSearch = theSearch + Integer.toString(list.get(i)) + ",";
                System.out.println("the search now: " + theSearch);
            }
            theSearch = theSearch.substring(0, theSearch.length() - 1) + ")";
            System.out.println(theSearch);
            final SqlRowSet theList = template.queryForRowSet("select * from profiles where profile_is_public = true " + theSearch);
            final List<UserProfile> result = new ArrayList<UserProfile>();
            while(theList.next()){
                UserProfile userProfile = new UserProfile();
                userProfile.setProfileId(theList.getInt("profile_id"));
                userProfile.setEmail(theList.getString("email"));
                userProfile.setDisplayName(theList.getString("display_name"));
                userProfile.setSummary(theList.getString("summary"));
                userProfile.setBirthday(theList.getInt("birthday"));
                userProfile.setBirthmonth(theList.getInt("birthmonth"));
                userProfile.setBirthyear(theList.getInt("birthyear"));
                userProfile.setHeight(theList.getInt("height"));
                userProfile.setWeight(theList.getInt("weight"));
                userProfile.setIsSmoking(theList.getString("is_smoking"));
                result.add(userProfile);
            }
            return Optional.of(Collections.unmodifiableList(result));
        } else{
            return Optional.empty();
        }
        

    }

    public void deleteLike(Integer profileId){
        template.update("delete from likes where profile_likes_id = ? or profile_liked_id = ?", profileId, profileId);
    }

    
}
