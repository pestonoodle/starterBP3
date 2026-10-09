package com.progettiportfoglio.multichat.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.progettiportfoglio.multichat.entities.Chat;

public interface ChatRepo extends JpaRepository<Chat, Long> {

}
