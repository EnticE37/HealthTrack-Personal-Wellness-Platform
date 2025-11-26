package com.healthtrack.backend.repository;

import com.healthtrack.backend.model.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {
}
