package com.progettiportfoglio.multichat.service;

import org.springframework.beans.factory.annotation.Autowired;
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

    final UserRepo ur;
    final TokenService ts;
    final PasswordEncoder pe;

    @Autowired
    PasswordEncoder p;

    AuthService(UserRepo ur, TokenService ts, PasswordEncoder pe) {
        this.ur = ur;
        this.ts = ts;
        this.pe = pe;
    }

    public void registrazione(AuthDTO.registerDTO dto) {
        User u = new User();

        if (dto.username() == null || dto.username().length() < 2 || dto.username().length() > 30) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username non valido");
        }

        if (!ur.findByUsername(dto.username()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username gia esistente");
        }

        if (dto.email() == null || !dto.email().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))

        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email non valida");
        }
        if (!ur.findByEmail(dto.email()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email gia esistente");
        }

        if (dto.password() == null || dto.password().length() < 8
                || dto.password().getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La password deve avere almeno 8 caratteri e massimo 72 byte UTF-8");
        }

        u.setUsername(dto.username());
        u.setPasswordHash(p.encode(dto.password()));
        u.setEmail(dto.email());
        ur.save(u);

    }

    public String login(AuthDTO.loginDTO dto) {

        User u = ur.findByEmail(dto.email()).orElseThrow(() -> new IllegalArgumentException("Account non trovato"));

        if (!p.matches(dto.password(), u.getPasswordHash())) {
            throw new IllegalArgumentException("Credenziali Errate");
        }

        return ts.createToken(u.getId());
    }

    public void logout(String token){
        ts.deleteToken(token);
    }

}
