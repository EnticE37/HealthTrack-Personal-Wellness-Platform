package com.healthtrack.backend.dto;

import javax.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank
    private String healthId;

    public String getHealthId() {
        return healthId;
    }

    public void setHealthId(String healthId) {
        this.healthId = healthId;
    }
}
