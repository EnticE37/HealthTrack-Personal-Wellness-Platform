package com.healthtrack.backend.controller;

import com.healthtrack.backend.dto.LoginRequest;
import com.healthtrack.backend.dto.RegisterRequest;
import com.healthtrack.backend.model.Email;
import com.healthtrack.backend.model.Phone;
import com.healthtrack.backend.model.User;
import com.healthtrack.backend.repository.EmailRepository;
import com.healthtrack.backend.repository.PhoneRepository;
import com.healthtrack.backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final UserRepository userRepository;
    private final EmailRepository emailRepository;
    private final PhoneRepository phoneRepository;

    public AuthController(UserRepository userRepository, EmailRepository emailRepository, PhoneRepository phoneRepository) {
        this.userRepository = userRepository;
        this.emailRepository = emailRepository;
        this.phoneRepository = phoneRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.findByHealthId(request.getHealthId()).isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        User user = new User();
        user.setName(request.getName());
        user.setHealthId(request.getHealthId());
        User saved = userRepository.save(user);

        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            Email email = new Email();
            email.setEmail(request.getEmail());
            email.setUser(saved);
            emailRepository.save(email);
        }

        if (request.getPhone() != null && !request.getPhone().isEmpty()) {
            Phone phone = new Phone();
            phone.setPhone(request.getPhone());
            phone.setUser(saved);
            phoneRepository.save(phone);
        }

        return ResponseEntity.ok(saved);
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@Valid @RequestBody LoginRequest request) {
        Optional<User> user = userRepository.findByHealthId(request.getHealthId());
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
