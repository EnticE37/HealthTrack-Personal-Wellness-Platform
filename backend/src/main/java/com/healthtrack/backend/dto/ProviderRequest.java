package com.healthtrack.backend.dto;

import javax.validation.constraints.NotBlank;

public class ProviderRequest {
    @NotBlank
    private String licenseNumber;
    @NotBlank
    private String name;

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
