package com.progettiportfoglio.multichat.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.progettiportfoglio.multichat.entities.User;

public interface UserRepo extends JpaRepository<User, Long>{

}
