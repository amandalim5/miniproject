package sg.edu.nus.iss.app.server.repositories;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import sg.edu.nus.iss.app.server.models.ChatMessage;
import sg.edu.nus.iss.app.server.services.Utils;

@Repository
public class ChatRepository {
    @Autowired
    MongoTemplate template;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void insertMessage(ChatMessage chatMessage){
        Document toInsert = Utils.chatMessageToDocument(chatMessage);
        Document newDoc = template.insert(toInsert, "messages");
        String stringId = newDoc.get("_id").toString();
        System.out.println("the doc id: " + stringId  + " <=========== look here for chats");
    }
    
    public List<Document> retrieveMessages(Integer chatId){
        Criteria criteria = Criteria.where("chatId").is(chatId);
        Query query = Query.query(criteria).with(Sort.by(Sort.Direction.ASC,"timestamp"));
        List<Document> result = template.find(query, Document.class, "messages");
        return result;
    }

    public Integer createChatId(Integer femaleProfileId, Integer maleProfileId){
        int added = jdbcTemplate.update("insert into chats (male_profile_id, female_profile_id) values (?,?)", maleProfileId, femaleProfileId);
        if(added <= 0 ){
            return -1;
        } else{
            final SqlRowSet rs = jdbcTemplate.queryForRowSet("select * from chats where male_profile_id = ? and female_profile_id = ?", maleProfileId, femaleProfileId);
            rs.first();
            return rs.getInt("chat_id");
        }
        

    }

    public Integer getChatId(Integer femaleProfileId, Integer maleProfileId){
        final SqlRowSet rs = jdbcTemplate.queryForRowSet("select * from chats where male_profile_id = ? and female_profile_id = ?", maleProfileId, femaleProfileId);
            if(rs.first()){
                return rs.getInt("chat_id");
    
            } else{
                return -1;
            }
    }

    public void deleteChatId(Integer profileId){
        jdbcTemplate.update("delete from chats where male_profile_id = ? or female_profile_id = ?", profileId, profileId);

    }
}
