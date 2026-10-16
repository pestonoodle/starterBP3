package com.progettiportfoglio.multichat.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.progettiportfoglio.multichat.entities.AuthToken;
import java.util.Optional;


public interface AuthRepo extends JpaRepository<AuthToken, String> {
    Optional<AuthToken> findByToken(String token);
    
}
