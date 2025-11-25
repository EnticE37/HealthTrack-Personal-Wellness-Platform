package com.healthtrack.backend.repository;

import com.healthtrack.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByHealthId(String healthId);

    Optional<User> findByHealthIdAndPassword(String healthId, String password);
}
