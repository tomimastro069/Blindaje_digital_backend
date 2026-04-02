package com.blindaje.modules.round.dto;

import jakarta.validation.constraints.NotNull;

public class CheckpointVerificationRequest {

    @NotNull(message = "La latitud no puede ser nula")
    private Double latitude;

    @NotNull(message = "La longitud no puede ser nula")
    private Double longitude;

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}
