package com.healthtrack.backend.model;

import javax.persistence.*;

@Entity
@Table(name = "challenge_participants")
public class ChallengeParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id")
    private WellnessChallenge challenge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String progress = "Not started";

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public WellnessChallenge getChallenge() {
        return challenge;
    }

    public void setChallenge(WellnessChallenge challenge) {
        this.challenge = challenge;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }
}
