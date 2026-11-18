package com.progettiportfoglio.multichat.DTO;

import com.progettiportfoglio.multichat.entities.Chat;

public record ChatDTO(
    Long id,
    String slug,
    String title,
    String description,
    boolean locked
) {

    public static ChatDTO fromEntity(Chat chat) {
        return new ChatDTO(
            chat.getId(),
            chat.getSlug(),
            chat.getTitle(),
            chat.getDescription(),
            chat.isLocked()
        );
    }
}