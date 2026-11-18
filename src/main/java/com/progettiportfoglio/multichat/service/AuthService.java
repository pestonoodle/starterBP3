package com.progettiportfoglio.multichat.service;

import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.progettiportfoglio.multichat.DTO.AuthDTO;
import com.progettiportfoglio.multichat.auth.TokenService;
import com.progettiportfoglio.multichat.entities.User;
import com.progettiportfoglio.multichat.repo.UserRepo;

@Service
public class AuthService {

    private final UserRepo ur;
    private final TokenService ts;
    private final PasswordEncoder pe;

    public AuthService(UserRepo ur, TokenService ts, PasswordEncoder pe) {
        this.ur = ur;
        this.ts = ts;
        this.pe = pe;
    }

    public AuthDTO.UserResponse registrazione(AuthDTO.RegisterRequest dto) {
        if (dto.username() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username mancante");
        }
        String username = dto.username().trim().toLowerCase();
        if (username.length() < 3 || username.length() > 30) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lo username deve avere da 3 a 30 caratteri");
        }

        if (ur.findByUsername(username).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username già esistente");
        }

        if (dto.email() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email mancante");
        }
        String email = dto.email().trim().toLowerCase();
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email non valida");
        }

        if (ur.findByEmail(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email già esistente");
        }

        if (dto.password() == null || dto.password().length() < 8
                || dto.password().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La password deve avere almeno 8 caratteri e massimo 72 byte UTF-8");
        }

        User u = new User();
        u.setUsername(username);
        u.setEmail(email);
        u.setPasswordHash(pe.encode(dto.password()));
        User saved = ur.save(u);

        return new AuthDTO.UserResponse(saved.getId(), saved.getUsername(), saved.getEmail(), saved.getRole());
    }

    public String login(AuthDTO.LoginRequest dto) {
        if (dto.email() == null || dto.password() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenziali non valide");
        }

        String email = dto.email().trim().toLowerCase();
        User u = ur.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenziali non valide"));

        if (!pe.matches(dto.password(), u.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenziali non valide");
        }

        return ts.createToken(u.getId());
    }

    public void logout(String token) {
        ts.deleteToken(token);
    }
}
