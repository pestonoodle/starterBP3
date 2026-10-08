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
        // TODO [WS-02]: inviare message con messagingTemplate.convertAndSend(...).
        // Destination: "/topic/chats/" + message.chatId() + "/messages".
        // Il controller chiamera questo metodo DOPO il salvataggio nel database.
        throw new UnsupportedOperationException("TODO: pubblicare il messaggio sul topic");
    }
}
