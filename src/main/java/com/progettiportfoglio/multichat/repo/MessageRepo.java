package com.progettiportfoglio.multichat.repo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.progettiportfoglio.multichat.entities.Message;

public interface MessageRepo extends JpaRepository<Message, Long> {
    List<Message> findByChatIdOrderByCreatedAtAscIdAsc(Long chatId);
    void deleteByChatId(Long chatId);
}
