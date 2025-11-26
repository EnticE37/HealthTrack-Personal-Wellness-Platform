package com.healthtrack.backend.repository;

import com.healthtrack.backend.model.UserProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserProviderRepository extends JpaRepository<UserProvider, Long> {
    List<UserProvider> findByUserId(Long userId);
}
