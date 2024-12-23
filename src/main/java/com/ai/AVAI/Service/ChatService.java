package com.ai.AVAI.Service;


import com.ai.AVAI.module.Chat;
import com.ai.AVAI.module.Message;
import com.ai.AVAI.repository.ChatRepository;
import com.ai.AVAI.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChatService {

    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;

    @Autowired
    public ChatService(ChatRepository chatRepository, MessageRepository messageRepository) {
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
    }


    public Chat createChat(String userID) {
        Chat chat = new Chat();
        if(userID == null){
            chat.setUserId("Guest");
        }
        chat.setUserId(userID);
        return chatRepository.save(chat);
    }

    public List<Chat>  getAllChats() {
        return chatRepository.findAll();
    }


    public List<Message> getMessages(long chatId) {
        Optional<Chat> chat = chatRepository.findById(chatId);
        return chat.map(Chat::getMessages).orElseThrow(() -> new IllegalArgumentException("Chat is not found"));
    }

    public Message addMessage(long chatId, String content, boolean isUserMessage) {
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new IllegalArgumentException("Chat not found"));
        Message message = new Message();
        message.setChat(chat);
        message.setContent(content);
        message.setUserMessage(isUserMessage);
        return messageRepository.save(message);
    }

}
