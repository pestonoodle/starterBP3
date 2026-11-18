package com.progettiportfoglio.multichat.DTO;

public record CreateChatRequest(
    String title,
    String slug,
    String description
) {}

