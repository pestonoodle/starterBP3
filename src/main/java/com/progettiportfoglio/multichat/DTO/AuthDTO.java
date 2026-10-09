package com.progettiportfoglio.multichat.DTO;

public class AuthDTO {
    public record registerDTO(String username, String password, String email) {};
    public record loginDTO(String email , String password) {};

}
