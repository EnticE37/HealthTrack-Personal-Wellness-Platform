package com.healthtrack.backend.repository;

import com.healthtrack.backend.model.WellnessChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface WellnessChallengeRepository extends JpaRepository<WellnessChallenge, Long> {
    @Query("select c from WellnessChallenge c where size(c.participants) = (select max(size(c2.participants)) from WellnessChallenge c2)")
    WellnessChallenge findMostPopular();
}
