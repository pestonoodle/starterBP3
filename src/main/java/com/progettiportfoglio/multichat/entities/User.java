package com.progettiportfoglio.multichat.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @NotEmpty
    @Column(unique = true)
    String username;

    @NotEmpty
    @Email
    @Column(unique = true)
    String email;

    String passwordHash;

    @PrePersist
    @PreUpdate
    public void checkFields() {
        username = username.toLowerCase().trim();
        email = email.toLowerCase().trim();
    }

}
