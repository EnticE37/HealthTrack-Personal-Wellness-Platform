package com.healthtrack.backend.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class AppointmentRequest {
    @NotNull
    private Long userId;
    @NotBlank
    private String providerLicense;
    @NotBlank
    private String appointmentTime;
    @NotBlank
    private String consultationType;
    private String notes;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getProviderLicense() {
        return providerLicense;
    }

    public void setProviderLicense(String providerLicense) {
        this.providerLicense = providerLicense;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getConsultationType() {
        return consultationType;
    }

    public void setConsultationType(String consultationType) {
        this.consultationType = consultationType;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
