package com.healthtrack.backend.dto;

public class UpdateUserRequest {
    private String name;
    private String primaryProviderLicense;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrimaryProviderLicense() {
        return primaryProviderLicense;
    }

    public void setPrimaryProviderLicense(String primaryProviderLicense) {
        this.primaryProviderLicense = primaryProviderLicense;
    }
}
