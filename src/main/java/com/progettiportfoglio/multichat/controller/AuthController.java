package com.progettiportfoglio.multichat.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.progettiportfoglio.multichat.DTO.AuthDTO;
import com.progettiportfoglio.multichat.service.AuthService;
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
    public String login(@RequestBody AuthDTO dto) {
        return "";
    }
    
    @GetMapping("me")
    public void myUser() {
        
    }
    
    @PostMapping("logout")
    public void logout() {
      
    }
    

}
