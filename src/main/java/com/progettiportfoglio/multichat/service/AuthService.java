package com.progettiportfoglio.multichat.service;

//???
import org.springframework.stereotype.Service;

import com.progettiportfoglio.multichat.DTO.AuthDTO;
import com.progettiportfoglio.multichat.entities.User;
import com.progettiportfoglio.multichat.repo.AuthRepo;

@Service
public class AuthService {
    final AuthRepo ar;

    AuthService(AuthRepo ar) {
        this.ar = ar;
    }

    public void registrazione(AuthDTO.registerDTO dto) {
        User u = new User();

        if (dto.username() == null || dto.username().length() < 2 || dto.username().length() > 30) {
            throw new IllegalArgumentException("Username non valido");
        }

        if (dto.email() == null ||
                !dto.email().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("Email non valida");
        }

        if (dto.password() == null ||
                dto.password().length() < 8 ||
                dto.password().getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException(
                    "La password deve avere almeno 8 caratteri e massimo 72 byte UTF-8");
        }

    }

}
