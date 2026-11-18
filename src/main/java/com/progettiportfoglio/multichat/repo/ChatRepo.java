package com.progettiportfoglio.multichat.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.progettiportfoglio.multichat.entities.Chat;

public interface ChatRepo extends JpaRepository<Chat, Long> {
    Optional<Chat> findBySlug(String slug);
    boolean existsBySlug(String slug);
}
