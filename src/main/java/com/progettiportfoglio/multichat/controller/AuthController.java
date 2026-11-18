package com.progettiportfoglio.multichat.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.progettiportfoglio.multichat.DTO.AuthDTO;
import com.progettiportfoglio.multichat.security.AuthenticatedUser;
import com.progettiportfoglio.multichat.security.TokenAuthenticationFilter;
import com.progettiportfoglio.multichat.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService as;

    public AuthController(AuthService as) {
        this.as = as;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthDTO.UserResponse register(@RequestBody AuthDTO.RegisterRequest dto) {
        return as.registrazione(dto);
    }

    @PostMapping("/login")
    public AuthDTO.LoginResponse login(@RequestBody AuthDTO.LoginRequest dto) {
        String token = as.login(dto);
        return new AuthDTO.LoginResponse(token);
    }

    @GetMapping("/me")
    public AuthDTO.UserResponse myUser(@AuthenticationPrincipal AuthenticatedUser u) {
        return new AuthDTO.UserResponse(u.id(), u.username(), u.email(), u.role());
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletRequest request) {
        String token = TokenAuthenticationFilter.extractBearerToken(
                request.getHeader("Authorization"));

        if (token == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Token mancante o non valido");
        }

        as.logout(token);
    }
}
