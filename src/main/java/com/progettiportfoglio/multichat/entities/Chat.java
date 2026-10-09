package com.progettiportfoglio.multichat.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity 
@Data 
@Table(name="chats")
public class Chat {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    
    String title, description;

    @Column(unique = true)
    String slug;
}
