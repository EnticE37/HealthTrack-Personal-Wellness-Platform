package com.healthtrack.backend.controller;

import com.healthtrack.backend.dto.ChallengeRequest;
import com.healthtrack.backend.dto.InvitationRequest;
import com.healthtrack.backend.model.*;
import com.healthtrack.backend.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/challenges")
@CrossOrigin(origins = "http://localhost:5173")
public class ChallengeController {

    private final WellnessChallengeRepository challengeRepository;
    private final ChallengeParticipantRepository participantRepository;
    private final InvitationRepository invitationRepository;
    private final UserRepository userRepository;

    public ChallengeController(WellnessChallengeRepository challengeRepository,
                               ChallengeParticipantRepository participantRepository,
                               InvitationRepository invitationRepository,
                               UserRepository userRepository) {
        this.challengeRepository = challengeRepository;
        this.participantRepository = participantRepository;
        this.invitationRepository = invitationRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<WellnessChallenge> createChallenge(@Valid @RequestBody ChallengeRequest request) {
        Optional<User> creatorOpt = userRepository.findById(request.getCreatorId());
        if (!creatorOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        WellnessChallenge challenge = new WellnessChallenge();
        challenge.setCreator(creatorOpt.get());
        challenge.setGoal(request.getGoal());
        challenge.setStartDate(LocalDate.parse(request.getStartDate()));
        challenge.setEndDate(LocalDate.parse(request.getEndDate()));
        WellnessChallenge saved = challengeRepository.save(challenge);

        if (request.getParticipantIds() != null) {
            for (Long participantId : request.getParticipantIds()) {
                userRepository.findById(participantId).ifPresent(user -> {
                    ChallengeParticipant participant = new ChallengeParticipant();
                    participant.setChallenge(saved);
                    participant.setUser(user);
                    participantRepository.save(participant);
                });
            }
        }
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public List<WellnessChallenge> listChallenges() {
        return challengeRepository.findAll();
    }

    @PostMapping("/{challengeId}/participants")
    public ResponseEntity<ChallengeParticipant> addParticipant(@PathVariable Long challengeId, @RequestParam Long userId) {
        Optional<WellnessChallenge> challengeOpt = challengeRepository.findById(challengeId);
        Optional<User> userOpt = userRepository.findById(userId);
        if (!challengeOpt.isPresent() || !userOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        ChallengeParticipant participant = new ChallengeParticipant();
        participant.setChallenge(challengeOpt.get());
        participant.setUser(userOpt.get());
        return ResponseEntity.ok(participantRepository.save(participant));
    }

    @PostMapping("/invite")
    public ResponseEntity<Invitation> inviteParticipant(@Valid @RequestBody InvitationRequest request) {
        Optional<User> inviterOpt = userRepository.findById(request.getInviterId());
        if (!inviterOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Invitation invitation = new Invitation();
        invitation.setInviter(inviterOpt.get());
        invitation.setContact(request.getContact());
        invitation.setContactType(request.getContactType());
        invitation.setInitiatedAt(LocalDateTime.now());
        invitation.setExpiresAt(LocalDateTime.now().plusDays(15));
        return ResponseEntity.ok(invitationRepository.save(invitation));
    }

    @GetMapping("/{challengeId}/participants")
    public ResponseEntity<List<ChallengeParticipant>> getParticipants(@PathVariable Long challengeId) {
        if (!challengeRepository.existsById(challengeId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(participantRepository.findByChallengeId(challengeId));
    }
}
