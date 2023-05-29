package sg.edu.nus.iss.app.server.models;

import org.bson.Document;

import jakarta.json.Json;
import jakarta.json.JsonObject;

public class ChatMessage {
    private Integer chatId;
    private String author;
    private Long timestamp;
    private String message;

    public Integer getChatId() {
        return chatId;
    }
    public void setChatId(Integer chatId) {
        this.chatId = chatId;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public Long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public static ChatMessage documentToChatMessage(Document document){
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setChatId(document.getInteger("chatId"));
        chatMessage.setAuthor(document.getString("author"));
        chatMessage.setMessage(document.getString("message"));
        chatMessage.setTimestamp(document.getLong("timestamp"));
        return chatMessage;
    }
    
    public static JsonObject toJson(ChatMessage chatMessage){
        return Json.createObjectBuilder()
                    .add("message", chatMessage.getMessage())
                    .add("author", chatMessage.getAuthor())
                    .build();
    }
}
