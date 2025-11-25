package com.healthtrack.backend.controller;

import com.healthtrack.backend.model.Appointment;
import com.healthtrack.backend.repository.AppointmentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "http://localhost:5173")
public class SearchController {

    private final AppointmentRepository appointmentRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    public SearchController(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @GetMapping("/appointments")
    public ResponseEntity<List<Appointment>> searchAppointments(@RequestParam(required = false) String healthId,
                                                                @RequestParam(required = false) String providerLicense,
                                                                @RequestParam(required = false) String type,
                                                                @RequestParam(required = false) String start,
                                                                @RequestParam(required = false) String end) {
        LocalDateTime startDate = start != null ? LocalDateTime.parse(start, formatter) : null;
        LocalDateTime endDate = end != null ? LocalDateTime.parse(end, formatter) : null;
        return ResponseEntity.ok(appointmentRepository.search(healthId, providerLicense, type, startDate, endDate));
    }
}
