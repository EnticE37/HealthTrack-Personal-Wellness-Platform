package com.healthtrack.backend.repository;

import com.healthtrack.backend.model.ChallengeParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChallengeParticipantRepository extends JpaRepository<ChallengeParticipant, Long> {
    List<ChallengeParticipant> findByChallengeId(Long challengeId);
}
