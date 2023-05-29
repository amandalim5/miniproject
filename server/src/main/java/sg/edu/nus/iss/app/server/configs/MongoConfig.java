package sg.edu.nus.iss.app.server.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Configuration
public class MongoConfig {
    @Value("${MONGO_URL}")
    private String connectionString;
  
    private MongoClient client = null;
  
    @Bean
    public MongoClient mongoClient(){
      if(client == null){
        // create an instance of MongoClient, which communicates with MongoDB singleton
        client = MongoClients.create(connectionString);
      }
      return client;
    }
  
    @Bean
    public MongoTemplate mongoTemplate(){
      // create the template with the client and the database name
      return new MongoTemplate(mongoClient(), "tokenreset");
    }
}
