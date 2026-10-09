package com.progettiportfoglio.multichat.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.progettiportfoglio.multichat.entities.AuthToken;

public interface AuthRepo extends JpaRepository<AuthToken, String> {

}
