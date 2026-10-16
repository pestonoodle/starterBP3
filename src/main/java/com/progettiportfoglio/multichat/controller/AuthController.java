package com.progettiportfoglio.multichat.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.progettiportfoglio.multichat.DTO.AuthDTO;
import com.progettiportfoglio.multichat.entities.User;
import com.progettiportfoglio.multichat.security.AuthenticatedUser;
import com.progettiportfoglio.multichat.security.TokenAuthenticationFilter;
import com.progettiportfoglio.multichat.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    final AuthService as;


    public AuthController(AuthService as) {
        this.as = as;
    }

    @PostMapping("/register")
    public void register(@RequestBody AuthDTO.registerDTO dto){
        as.registrazione(dto);
    }

    @PostMapping("login")
    public String login(@RequestBody AuthDTO.loginDTO dto) {
        return as.login(dto);
    }
    
    @GetMapping("me")
    public AuthenticatedUser myUser(@AuthenticationPrincipal AuthenticatedUser u) {
        return u;
    }
    
    @PostMapping("logout")
    public void logout(HttpServletRequest request) {
    String token = TokenAuthenticationFilter.extractBearerToken(
        request.getHeader("Authorization")
    );

    if (token == null) {
        throw new ResponseStatusException(
            HttpStatus.UNAUTHORIZED, "Token mancante o non valido"
        );
    }

    as.logout(token);
    }

}
