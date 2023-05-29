package sg.edu.nus.iss.app.server.repositories;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import sg.edu.nus.iss.app.server.models.PasswordToken;
import sg.edu.nus.iss.app.server.services.Utils;

@Repository
public class TokenRepository {
    @Autowired
    MongoTemplate template;

    public String insertPasswordToken(PasswordToken passwordToken){
        Document toInsert = Utils.passwordTokenToDocument(passwordToken);
        Document newDoc = template.insert(toInsert, "tokens");
        String stringId = newDoc.get("_id").toString();
        System.out.println("the doc of id, " + stringId + " was created!");
        return stringId;
    }

    public Document retrieveToken(String email){
        Criteria criteria = Criteria.where("email").is(email);
        Query query = Query.query(criteria).with(Sort.by(Sort.Direction.DESC,"dateTime"));
        List<Document> result = template.find(query, Document.class, "tokens");
        if(result.size() == 0){
            return null;
        }
        return result.get(0);
    }
}
