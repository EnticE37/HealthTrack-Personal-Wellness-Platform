package com.healthtrack.backend.controller;

import com.healthtrack.backend.model.Appointment;
import com.healthtrack.backend.model.HealthMetric;
import com.healthtrack.backend.model.User;
import com.healthtrack.backend.model.WellnessChallenge;
import com.healthtrack.backend.repository.AppointmentRepository;
import com.healthtrack.backend.repository.HealthMetricRepository;
import com.healthtrack.backend.repository.UserRepository;
import com.healthtrack.backend.repository.WellnessChallengeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/summary")
@CrossOrigin(origins = "http://localhost:5173")
public class SummaryController {

    private final AppointmentRepository appointmentRepository;
    private final HealthMetricRepository healthMetricRepository;
    private final WellnessChallengeRepository challengeRepository;
    private final UserRepository userRepository;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public SummaryController(AppointmentRepository appointmentRepository,
                             HealthMetricRepository healthMetricRepository,
                             WellnessChallengeRepository challengeRepository,
                             UserRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.healthMetricRepository = healthMetricRepository;
        this.challengeRepository = challengeRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/appointments-count")
    public ResponseEntity<Long> countAppointments(@RequestParam Long userId, @RequestParam String start, @RequestParam String end) {
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.notFound().build();
        }
        LocalDateTime startDate = LocalDate.parse(start, dateFormatter).atStartOfDay();
        LocalDateTime endDate = LocalDate.parse(end, dateFormatter).atTime(23, 59, 59);
        long count = appointmentRepository.countByUserIdAndAppointmentTimeBetween(userId, startDate, endDate);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/metrics/{userId}")
    public ResponseEntity<List<HealthMetric>> getMetrics(@PathVariable Long userId) {
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(healthMetricRepository.findByUserId(userId));
    }

    @PostMapping("/metrics/{userId}")
    public ResponseEntity<HealthMetric> saveMetric(@PathVariable Long userId, @RequestBody HealthMetric metric) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        metric.setUser(userOpt.get());
        return ResponseEntity.ok(healthMetricRepository.save(metric));
    }

    @GetMapping("/popular-challenge")
    public ResponseEntity<WellnessChallenge> mostPopularChallenge() {
        return Optional.ofNullable(challengeRepository.findMostPopular())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @GetMapping("/most-active-user")
    public ResponseEntity<User> mostActiveUser() {
        List<User> users = userRepository.findAll();
        User best = null;
        int bestScore = -1;
        for (User user : users) {
            int score = user.getEmails().size() + user.getPhones().size();
            if (score > bestScore) {
                bestScore = score;
                best = user;
            }
        }
        if (best == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(best);
    }
}
