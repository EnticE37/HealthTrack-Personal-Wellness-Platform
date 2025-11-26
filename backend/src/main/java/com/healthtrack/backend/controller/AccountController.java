package com.healthtrack.backend.controller;

import com.healthtrack.backend.dto.ContactRequest;
import com.healthtrack.backend.dto.ProviderRequest;
import com.healthtrack.backend.dto.UpdateUserRequest;
import com.healthtrack.backend.model.*;
import com.healthtrack.backend.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/account")
@CrossOrigin(origins = "http://localhost:5173")
public class AccountController {

    private final UserRepository userRepository;
    private final EmailRepository emailRepository;
    private final PhoneRepository phoneRepository;
    private final ProviderRepository providerRepository;
    private final UserProviderRepository userProviderRepository;

    public AccountController(UserRepository userRepository,
                             EmailRepository emailRepository,
                             PhoneRepository phoneRepository,
                             ProviderRepository providerRepository,
                             UserProviderRepository userProviderRepository) {
        this.userRepository = userRepository;
        this.emailRepository = emailRepository;
        this.phoneRepository = phoneRepository;
        this.providerRepository = providerRepository;
        this.userProviderRepository = userProviderRepository;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUser(@PathVariable Long userId) {
        return userRepository.findById(userId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable Long userId, @RequestBody UpdateUserRequest request) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        User user = userOptional.get();
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getPrimaryProviderLicense() != null) {
            Provider provider = providerRepository.findByLicenseNumber(request.getPrimaryProviderLicense())
                    .orElseGet(() -> {
                        Provider newProvider = new Provider();
                        newProvider.setLicenseNumber(request.getPrimaryProviderLicense());
                        newProvider.setName("Primary Provider");
                        return providerRepository.save(newProvider);
                    });
            user.setPrimaryProvider(provider);
        }
        return ResponseEntity.ok(userRepository.save(user));
    }

    @PostMapping("/{userId}/emails")
    public ResponseEntity<Email> addEmail(@PathVariable Long userId, @Valid @RequestBody ContactRequest request) {
        return userRepository.findById(userId).map(user -> {
            Email email = new Email();
            email.setUser(user);
            email.setEmail(request.getValue());
            return ResponseEntity.ok(emailRepository.save(email));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/emails/{emailId}")
    public ResponseEntity<Void> removeEmail(@PathVariable Long emailId) {
        if (!emailRepository.existsById(emailId)) {
            return ResponseEntity.notFound().build();
        }
        emailRepository.deleteById(emailId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/phones")
    public ResponseEntity<Phone> addPhone(@PathVariable Long userId, @Valid @RequestBody ContactRequest request) {
        return userRepository.findById(userId).map(user -> {
            Phone phone = new Phone();
            phone.setUser(user);
            phone.setPhone(request.getValue());
            return ResponseEntity.ok(phoneRepository.save(phone));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/phones/{phoneId}")
    public ResponseEntity<Void> removePhone(@PathVariable Long phoneId) {
        if (!phoneRepository.existsById(phoneId)) {
            return ResponseEntity.notFound().build();
        }
        phoneRepository.deleteById(phoneId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/providers")
    public ResponseEntity<UserProvider> linkProvider(@PathVariable Long userId, @Valid @RequestBody ProviderRequest request) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Provider provider = providerRepository.findByLicenseNumber(request.getLicenseNumber())
                .orElseGet(() -> {
                    Provider p = new Provider();
                    p.setLicenseNumber(request.getLicenseNumber());
                    p.setName(request.getName());
                    return providerRepository.save(p);
                });
        UserProvider link = new UserProvider();
        link.setUser(userOptional.get());
        link.setProvider(provider);
        return ResponseEntity.ok(userProviderRepository.save(link));
    }

    @DeleteMapping("/providers/{linkId}")
    public ResponseEntity<Void> unlinkProvider(@PathVariable Long linkId) {
        if (!userProviderRepository.existsById(linkId)) {
            return ResponseEntity.notFound().build();
        }
        userProviderRepository.deleteById(linkId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}/providers")
    public ResponseEntity<List<UserProvider>> getProviders(@PathVariable Long userId) {
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userProviderRepository.findByUserId(userId));
    }
}
