package com.progettiportfoglio.multichat.auth;

import com.progettiportfoglio.multichat.entities.AuthToken;
import com.progettiportfoglio.multichat.entities.User;
import com.progettiportfoglio.multichat.repo.AuthRepo;
import com.progettiportfoglio.multichat.repo.UserRepo;
import com.progettiportfoglio.multichat.security.AuthenticatedUser;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class TokenService {

    UserRepo ur;
    AuthRepo ar;

    public TokenService(UserRepo ur, AuthRepo ar) {
        this.ur = ur;
        this.ar = ar;
    }

    public String createToken(Long userId) {
        String token = UUID.randomUUID().toString();
        User u = ur.findById(userId).get();
        AuthToken at = new AuthToken();

        at.setUser(u);
        at.setToken(token);

        ar.save(at);

        return token;
    }

    public Optional<AuthenticatedUser> findUserByToken(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        return ar.findByToken(token)
            .map(a -> new AuthenticatedUser(
            a.getUser().getId(),
            a.getUser().getUsername(),
            a.getUser().getEmail(),
            a.getUser().getRole() != null ? a.getUser().getRole() : "ROLE_USER"
        ));
    }

    public void deleteToken(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException ("Errore cancellazione token");
        }
        ar.deleteById(token);
    }
}
