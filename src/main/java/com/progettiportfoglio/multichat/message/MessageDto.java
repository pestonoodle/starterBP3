package com.progettiportfoglio.multichat.message;

import java.time.Instant;

// Usare questo DTO sia nelle risposte REST sia negli eventi WebSocket.
public record MessageDto(Long id, Long chatId, Long authorId, String username,
                         String content, Instant createdAt) {
}
