package com.healthtrack.backend.dto;

import javax.validation.constraints.NotBlank;

public class CancelAppointmentRequest {
    @NotBlank
    private String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
