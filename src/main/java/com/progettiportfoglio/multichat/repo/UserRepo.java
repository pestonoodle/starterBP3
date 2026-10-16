package com.progettiportfoglio.multichat.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.progettiportfoglio.multichat.entities.User;


public interface UserRepo extends JpaRepository<User, Long>{
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
}
