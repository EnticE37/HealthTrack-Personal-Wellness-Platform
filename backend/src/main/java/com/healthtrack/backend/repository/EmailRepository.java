package com.healthtrack.backend.repository;

import com.healthtrack.backend.model.Email;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailRepository extends JpaRepository<Email, Long> {
}
