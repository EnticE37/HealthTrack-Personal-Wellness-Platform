package com.healthtrack.backend.repository;

import com.healthtrack.backend.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByUserId(Long userId);

    @Query("select a from Appointment a where (:healthId is null or a.user.healthId = :healthId) " +
            "and (:providerLicense is null or a.provider.licenseNumber = :providerLicense) " +
            "and (:type is null or a.consultationType = :type) " +
            "and (:startDate is null or a.appointmentTime >= :startDate) " +
            "and (:endDate is null or a.appointmentTime <= :endDate)")
    List<Appointment> search(@Param("healthId") String healthId,
                              @Param("providerLicense") String providerLicense,
                              @Param("type") String type,
                              @Param("startDate") LocalDateTime startDate,
                              @Param("endDate") LocalDateTime endDate);

    long countByUserIdAndAppointmentTimeBetween(Long userId, LocalDateTime start, LocalDateTime end);
}
