package com.progettiportfoglio.multichat.DTO;

public class AuthDTO {
    public record RegisterRequest(String username, String email, String password) {}
    public record LoginRequest(String email, String password) {}
    public record LoginResponse(String token) {}
    public record UserResponse(Long id, String username, String email, String role) {}

    // Alias per compatibilità con eventuale codice preesistente
    public record registerDTO(String username, String password, String email) {}
    public record loginDTO(String email, String password) {}
}
