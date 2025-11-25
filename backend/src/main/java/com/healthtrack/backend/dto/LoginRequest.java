package com.healthtrack.backend.dto;

import javax.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank
    private String healthId;
    @NotBlank
    private String password;

    public String getHealthId() {
        return healthId;
    }

    public void setHealthId(String healthId) {
        this.healthId = healthId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
