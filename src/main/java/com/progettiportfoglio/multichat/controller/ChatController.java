package com.progettiportfoglio.multichat.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.progettiportfoglio.multichat.DTO.ChatDTO;
import com.progettiportfoglio.multichat.DTO.CreateChatRequest;
import com.progettiportfoglio.multichat.DTO.SendMessageRequest;
import com.progettiportfoglio.multichat.message.MessageDto;
import com.progettiportfoglio.multichat.security.AuthenticatedUser;
import com.progettiportfoglio.multichat.service.ChatService;
import com.progettiportfoglio.multichat.websocket.ChatWebSocketService;

@RestController
@RequestMapping("/api/chats")
public class ChatController {

    private final ChatService chatService;
    private final ChatWebSocketService webSocketService;

    public ChatController(ChatService chatService, ChatWebSocketService webSocketService) {
        this.chatService = chatService;
        this.webSocketService = webSocketService;
    }

    // GET /api/chats
    @GetMapping
    public List<ChatDTO> getChats() {
        return chatService.getChats();
    }

    // POST /api/chats (Tutti gli utenti possono creare una nuova chat)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ChatDTO createChat(@RequestBody CreateChatRequest request) {
        return chatService.createChat(request);
    }

    // GET /api/chats/{chatId}
    @GetMapping("/{chatId}")
    public ChatDTO getChat(@PathVariable Long chatId) {
        return chatService.getChat(chatId);
    }

    // PATCH /api/chats/{chatId}/lock (Solo ADMIN: blocca/sblocca commenti)
    @PatchMapping("/{chatId}/lock")
    public ChatDTO toggleLockChat(
            @PathVariable Long chatId,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        return chatService.toggleLockChat(chatId, currentUser.role());
    }

    // DELETE /api/chats/{chatId} (Solo ADMIN: elimina chat e tutti i commenti)
    @DeleteMapping("/{chatId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteChat(
            @PathVariable Long chatId,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        chatService.deleteChat(chatId, currentUser.role());
    }

    // GET /api/chats/{chatId}/messages
    @GetMapping("/{chatId}/messages")
    public List<MessageDto> getMessages(
            @PathVariable Long chatId,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        Long userId = currentUser != null ? currentUser.id() : null;
        return chatService.getMessages(chatId, userId);
    }

    // POST /api/chats/{chatId}/messages
    @PostMapping("/{chatId}/messages")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageDto sendMessage(
            @PathVariable Long chatId,
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            @RequestBody SendMessageRequest request) {

        MessageDto message = chatService.sendMessage(chatId, currentUser.id(), request);
        webSocketService.publishMessage(message);
        return message;
    }

    // DELETE /api/chats/messages/{messageId} (Accessibile solo all'ADMIN)
    @DeleteMapping("/messages/{messageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMessage(
            @PathVariable Long messageId,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        chatService.deleteMessage(messageId, currentUser.role());
    }

    // POST /api/chats/messages/{messageId}/reaction (Tutti gli utenti: like o dislike)
    @PostMapping("/messages/{messageId}/reaction")
    public MessageDto reactToMessage(
            @PathVariable Long messageId,
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            @RequestBody com.progettiportfoglio.multichat.DTO.ReactionRequest request) {
        return chatService.reactToMessage(messageId, currentUser.id(), request.isLike());
    }
}
