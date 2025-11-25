package com.healthtrack.backend.repository;

import com.healthtrack.backend.model.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProviderRepository extends JpaRepository<Provider, Long> {
    Optional<Provider> findByLicenseNumber(String licenseNumber);
}
