package com.progettiportfoglio.multichat.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.progettiportfoglio.multichat.entities.User;
import com.progettiportfoglio.multichat.repo.UserRepo;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Inizializza l'utente admin se non esiste già
        String adminEmail = "admin@admin.com";
        userRepo.findByEmail(adminEmail).ifPresentOrElse(
            adminUser -> {
                // Assicuriamoci che abbia ROLE_ADMIN e password corretta
                boolean updated = false;
                if (!"ROLE_ADMIN".equals(adminUser.getRole())) {
                    adminUser.setRole("ROLE_ADMIN");
                    updated = true;
                }
                if (!passwordEncoder.matches("admin", adminUser.getPasswordHash())) {
                    adminUser.setPasswordHash(passwordEncoder.encode("admin"));
                    updated = true;
                }
                if (!"admin".equals(adminUser.getUsername())) {
                    adminUser.setUsername("admin");
                    updated = true;
                }
                if (updated) {
                    userRepo.save(adminUser);
                }
            },
            () -> {
                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail(adminEmail);
                admin.setPasswordHash(passwordEncoder.encode("admin"));
                admin.setRole("ROLE_ADMIN");
                userRepo.save(admin);
            }
        );
    }
}

