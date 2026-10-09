package com.progettiportfoglio.multichat.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.progettiportfoglio.multichat.entities.Message;

public interface MessageRepo extends JpaRepository<Message, Long>{

}
