package com.progettiportfoglio.multichat.DTO;

public record ReactionRequest(
    boolean isLike // true = LIKE (v), false = DISLIKE (x)
) {}

