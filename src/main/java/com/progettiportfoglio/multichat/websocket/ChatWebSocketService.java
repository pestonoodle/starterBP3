package com.progettiportfoglio.multichat.websocket;

import com.progettiportfoglio.multichat.message.MessageDto;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class ChatWebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public ChatWebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void publishMessage(MessageDto message) {
        messagingTemplate.convertAndSend("/topic/chats/" + message.chatId() + "/messages", message);
    }
}
