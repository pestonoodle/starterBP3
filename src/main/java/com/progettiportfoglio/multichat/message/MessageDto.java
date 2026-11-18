package com.progettiportfoglio.multichat.message;

import java.time.Instant;
import com.progettiportfoglio.multichat.entities.Message;

// DTO del messaggio con conteggi likes/dislikes e reazione dell'utente corrente (se presente)
public record MessageDto(
    Long id,
    Long chatId,
    Long authorId,
    String username,
    String content,
    Instant createdAt,
    long likesCount,
    long dislikesCount,
    String userReaction // "LIKE", "DISLIKE", oppure null
) {

    public static MessageDto fromEntity(Message message) {
        return fromEntity(message, 0, 0, null);
    }

    public static MessageDto fromEntity(Message message, long likesCount, long dislikesCount, String userReaction) {
        return new MessageDto(
            message.getId(),
            message.getChat() != null ? message.getChat().getId() : null,
            message.getAuthor() != null ? message.getAuthor().getId() : null,
            message.getAuthor() != null ? message.getAuthor().getUsername() : null,
            message.getContent(),
            message.getCreatedAt(),
            likesCount,
            dislikesCount,
            userReaction
        );
    }
}
