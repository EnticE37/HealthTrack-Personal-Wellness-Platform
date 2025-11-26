package com.healthtrack.backend.controller;

import com.healthtrack.backend.dto.AppointmentRequest;
import com.healthtrack.backend.dto.CancelAppointmentRequest;
import com.healthtrack.backend.model.Appointment;
import com.healthtrack.backend.model.Provider;
import com.healthtrack.backend.model.User;
import com.healthtrack.backend.repository.AppointmentRepository;
import com.healthtrack.backend.repository.ProviderRepository;
import com.healthtrack.backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "http://localhost:5173")
public class AppointmentController {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final ProviderRepository providerRepository;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    public AppointmentController(AppointmentRepository appointmentRepository, UserRepository userRepository, ProviderRepository providerRepository) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.providerRepository = providerRepository;
    }

    @PostMapping
    public ResponseEntity<Appointment> createAppointment(@Valid @RequestBody AppointmentRequest request) {
        Optional<User> userOpt = userRepository.findById(request.getUserId());
        if (!userOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Provider provider = providerRepository.findByLicenseNumber(request.getProviderLicense())
                .orElseGet(() -> {
                    Provider p = new Provider();
                    p.setLicenseNumber(request.getProviderLicense());
                    p.setName("Provider");
                    return providerRepository.save(p);
                });

        Appointment appointment = new Appointment();
        appointment.setUser(userOpt.get());
        appointment.setProvider(provider);
        appointment.setConsultationType(request.getConsultationType());
        appointment.setAppointmentTime(LocalDateTime.parse(request.getAppointmentTime(), formatter));
        appointment.setNotes(request.getNotes());
        return ResponseEntity.ok(appointmentRepository.save(appointment));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Appointment>> getAppointmentsForUser(@PathVariable Long userId) {
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(appointmentRepository.findByUserId(userId));
    }

    @PutMapping("/{appointmentId}/cancel")
    public ResponseEntity<Appointment> cancelAppointment(@PathVariable Long appointmentId, @Valid @RequestBody CancelAppointmentRequest request) {
        Optional<Appointment> appointmentOptional = appointmentRepository.findById(appointmentId);
        if (!appointmentOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Appointment appointment = appointmentOptional.get();
        if (appointment.getAppointmentTime().isBefore(LocalDateTime.now().minusHours(24))) {
            return ResponseEntity.badRequest().build();
        }
        appointment.setStatus("CANCELLED");
        appointment.setCancellationReason(request.getReason());
        return ResponseEntity.ok(appointmentRepository.save(appointment));
    }
}
