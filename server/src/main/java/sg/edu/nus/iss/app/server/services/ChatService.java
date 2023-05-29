package sg.edu.nus.iss.app.server.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sg.edu.nus.iss.app.server.models.ChatMessage;
import sg.edu.nus.iss.app.server.repositories.ChatRepository;

@Service
public class ChatService {
    @Autowired
    ChatRepository chatRepository;

    public List<ChatMessage> getChatMessages(Integer chatId){
        return chatRepository.retrieveMessages(chatId)
            .stream().map(v-> ChatMessage.documentToChatMessage(v)).toList();

    }

    public Integer createChatId(Integer femaleProfileId, Integer maleProfileId){
        return chatRepository.createChatId(femaleProfileId, maleProfileId);
    }

    public Integer getChatId(Integer femaleProfileId, Integer maleProfileId){
        return chatRepository.getChatId(femaleProfileId, maleProfileId);
    }

    public void createMessage(String author, Integer chatId, String message){
        ChatMessage c = new ChatMessage();
        c.setAuthor(author);
        c.setChatId(chatId);
        c.setMessage(message);
        c.setTimestamp(System.currentTimeMillis());
        chatRepository.insertMessage(c);

    }
}
