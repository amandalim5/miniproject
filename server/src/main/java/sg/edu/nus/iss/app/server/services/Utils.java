package sg.edu.nus.iss.app.server.services;

import org.bson.Document;

import sg.edu.nus.iss.app.server.models.ChatMessage;
import sg.edu.nus.iss.app.server.models.PasswordToken;

public class Utils {
    public static Document passwordTokenToDocument(PasswordToken passwordToken){
        Document document = new Document();
        document.put("email", passwordToken.getEmail());
        document.put("token", passwordToken.getToken());
        document.put("dateTime", passwordToken.getDateTime());
        return document;
    }

    public static Document chatMessageToDocument(ChatMessage chatMessage){
        Document document = new Document();
        document.put("chatId", chatMessage.getChatId());
        document.put("author", chatMessage.getAuthor());
        document.put("message", chatMessage.getMessage());
        document.put("timestamp", chatMessage.getTimestamp());
        return document;
    }

}
