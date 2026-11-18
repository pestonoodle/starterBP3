package com.progettiportfoglio.multichat.service;

import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.progettiportfoglio.multichat.DTO.ChatDTO;
import com.progettiportfoglio.multichat.DTO.CreateChatRequest;
import com.progettiportfoglio.multichat.DTO.ReactionRequest;
import com.progettiportfoglio.multichat.DTO.SendMessageRequest;
import com.progettiportfoglio.multichat.entities.Chat;
import com.progettiportfoglio.multichat.entities.Message;
import com.progettiportfoglio.multichat.entities.MessageReaction;
import com.progettiportfoglio.multichat.entities.User;
import com.progettiportfoglio.multichat.message.MessageDto;
import com.progettiportfoglio.multichat.repo.ChatRepo;
import com.progettiportfoglio.multichat.repo.MessageReactionRepo;
import com.progettiportfoglio.multichat.repo.MessageRepo;
import com.progettiportfoglio.multichat.repo.UserRepo;

@Service
public class ChatService {

    private final ChatRepo chatRepo;
    private final MessageRepo messageRepo;
    private final UserRepo userRepo;
    private final MessageReactionRepo reactionRepo;

    public ChatService(ChatRepo chatRepo, MessageRepo messageRepo, UserRepo userRepo, MessageReactionRepo reactionRepo) {
        this.chatRepo = chatRepo;
        this.messageRepo = messageRepo;
        this.userRepo = userRepo;
        this.reactionRepo = reactionRepo;
    }

    @Transactional(readOnly = true)
    public List<ChatDTO> getChats() {
        return chatRepo.findAll()
                .stream()
                .map(ChatDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public ChatDTO getChat(Long chatId) {
        return chatRepo.findById(chatId)
                .map(ChatDTO::fromEntity)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat non trovata"));
    }

    @Transactional(readOnly = true)
    public List<MessageDto> getMessages(Long chatId) {
        return getMessages(chatId, null);
    }

    @Transactional(readOnly = true)
    public List<MessageDto> getMessages(Long chatId, Long currentUserId) {
        if (!chatRepo.existsById(chatId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat non trovata");
        }

        List<Message> messages = messageRepo.findByChatIdOrderByCreatedAtAscIdAsc(chatId);
        List<MessageReaction> reactions = reactionRepo.findByMessageChatId(chatId);

        return messages.stream().map(msg -> {
            long likes = reactions.stream()
                    .filter(r -> r.getMessage().getId().equals(msg.getId()) && r.isLike())
                    .count();
            long dislikes = reactions.stream()
                    .filter(r -> r.getMessage().getId().equals(msg.getId()) && !r.isLike())
                    .count();

            String userReaction = null;
            if (currentUserId != null) {
                userReaction = reactions.stream()
                        .filter(r -> r.getMessage().getId().equals(msg.getId()) && r.getUser().getId().equals(currentUserId))
                        .findFirst()
                        .map(r -> r.isLike() ? "LIKE" : "DISLIKE")
                        .orElse(null);
            }

            return MessageDto.fromEntity(msg, likes, dislikes, userReaction);
        }).toList();
    }

    @Transactional
    public ChatDTO createChat(CreateChatRequest request) {
        if (request == null || request.title() == null || request.title().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Il titolo della chat è obbligatorio");
        }

        String title = request.title().trim();
        String slug;

        if (request.slug() != null && !request.slug().trim().isEmpty()) {
            slug = request.slug().trim().toLowerCase().replaceAll("[^a-z0-9_-]", "-");
        } else {
            slug = title.toLowerCase().replaceAll("[^a-z0-9_-]", "-");
        }

        if (slug.isEmpty()) {
            slug = "chat-" + System.currentTimeMillis();
        }

        // Se lo slug esiste già, aggiungiamo un suffisso univoco
        String baseSlug = slug;
        int counter = 1;
        while (chatRepo.existsBySlug(slug)) {
            slug = baseSlug + "-" + counter;
            counter++;
        }

        Chat chat = new Chat();
        chat.setTitle(title);
        chat.setSlug(slug);
        chat.setDescription(request.description() != null ? request.description().trim() : "");
        chat.setLocked(false);

        Chat saved = chatRepo.save(chat);
        return ChatDTO.fromEntity(saved);
    }

    @Transactional
    public MessageDto sendMessage(Long chatId, Long authorId, SendMessageRequest request) {
        if (request == null || request.content() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Il contenuto del messaggio è obbligatorio");
        }

        String trimmedContent = request.content().trim();
        if (trimmedContent.isEmpty() || trimmedContent.length() > 2000) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Il messaggio deve contenere tra 1 e 2000 caratteri");
        }

        Chat chat = chatRepo.findById(chatId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat non trovata"));

        if (chat.isLocked()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Questa stanza è bloccata: non è possibile inviare nuovi commenti");
        }

        User author = userRepo.findById(authorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utente non trovato"));

        Message message = new Message();
        message.setChat(chat);
        message.setAuthor(author);
        message.setContent(trimmedContent);
        message.setCreatedAt(Instant.now());

        Message saved = messageRepo.save(message);
        return MessageDto.fromEntity(saved);
    }

    @Transactional
    public ChatDTO toggleLockChat(Long chatId, String role) {
        if (!"ROLE_ADMIN".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo l'amministratore può bloccare/sbloccare le chat");
        }

        Chat chat = chatRepo.findById(chatId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat non trovata"));

        chat.setLocked(!chat.isLocked());
        Chat updated = chatRepo.save(chat);
        return ChatDTO.fromEntity(updated);
    }

    @Transactional
    public void deleteChat(Long chatId, String role) {
        if (!"ROLE_ADMIN".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo l'amministratore può eliminare le chat");
        }

        if (!chatRepo.existsById(chatId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat non trovata");
        }

        // Elimina le reazioni e i messaggi associati prima di eliminare la chat
        reactionRepo.deleteByMessageChatId(chatId);
        messageRepo.deleteByChatId(chatId);
        chatRepo.deleteById(chatId);
    }

    @Transactional
    public void deleteMessage(Long messageId, String role) {
        if (!"ROLE_ADMIN".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo l'amministratore può cancellare i messaggi");
        }
        if (!messageRepo.existsById(messageId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Messaggio non trovato");
        }
        reactionRepo.deleteByMessageId(messageId);
        messageRepo.deleteById(messageId);
    }

    @Transactional
    public MessageDto reactToMessage(Long messageId, Long userId, boolean isLike) {
        Message message = messageRepo.findById(messageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Messaggio non trovato"));

        if (message.getChat() != null && message.getChat().isLocked()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Questa stanza è bloccata: non è possibile modificare le reazioni");
        }

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utente non trovato"));

        var existingReaction = reactionRepo.findByMessageIdAndUserId(messageId, userId);

        if (existingReaction.isPresent()) {
            MessageReaction r = existingReaction.get();
            if (r.isLike() == isLike) {
                // Se clicca di nuovo sulla stessa reazione, la rimuove (toggle)
                reactionRepo.delete(r);
            } else {
                // Se era dislike e clicca like (o viceversa), aggiorna la scelta
                r.setLike(isLike);
                reactionRepo.save(r);
            }
        } else {
            // Nuova reazione
            MessageReaction newReaction = new MessageReaction();
            newReaction.setMessage(message);
            newReaction.setUser(user);
            newReaction.setLike(isLike);
            reactionRepo.save(newReaction);
        }

        List<MessageReaction> reactions = reactionRepo.findByMessageId(messageId);
        long likes = reactions.stream().filter(MessageReaction::isLike).count();
        long dislikes = reactions.stream().filter(r -> !r.isLike()).count();

        String currentReaction = reactions.stream()
                .filter(r -> r.getUser().getId().equals(userId))
                .findFirst()
                .map(r -> r.isLike() ? "LIKE" : "DISLIKE")
                .orElse(null);

        return MessageDto.fromEntity(message, likes, dislikes, currentReaction);
    }
}

