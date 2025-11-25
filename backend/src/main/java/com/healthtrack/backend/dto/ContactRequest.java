package com.healthtrack.backend.dto;

import javax.validation.constraints.NotBlank;

public class ContactRequest {
    @NotBlank
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
