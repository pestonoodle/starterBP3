package com.progettiportfoglio.multichat.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity 
@Data 
@Table(name="auth_tokens")
public class AuthToken {
    @Id
    @Column(length = 36)
    String token;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;



}
