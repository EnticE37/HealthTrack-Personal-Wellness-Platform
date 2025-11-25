package com.healthtrack.backend.repository;

import com.healthtrack.backend.model.Phone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhoneRepository extends JpaRepository<Phone, Long> {
}
