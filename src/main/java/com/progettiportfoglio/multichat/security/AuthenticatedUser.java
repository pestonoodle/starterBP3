package com.progettiportfoglio.multichat.security;

import java.security.Principal;

// Dati minimi dell'utente autenticato. Non contiene password o token.
public record AuthenticatedUser(Long id, String username, String email, String role) implements Principal {
    @Override
    public String getName() {
        return id.toString();
    }
}
