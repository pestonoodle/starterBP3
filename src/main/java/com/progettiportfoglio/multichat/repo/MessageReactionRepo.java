package com.progettiportfoglio.multichat.repo;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.progettiportfoglio.multichat.entities.MessageReaction;

public interface MessageReactionRepo extends JpaRepository<MessageReaction, Long> {
    Optional<MessageReaction> findByMessageIdAndUserId(Long messageId, Long userId);
    List<MessageReaction> findByMessageId(Long messageId);
    List<MessageReaction> findByMessageChatId(Long chatId);
    void deleteByMessageId(Long messageId);
    void deleteByMessageChatId(Long chatId);
}

