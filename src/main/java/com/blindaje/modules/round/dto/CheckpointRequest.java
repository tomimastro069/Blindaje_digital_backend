package com.blindaje.modules.round.dto;

import jakarta.validation.constraints.NotNull;

public class CheckpointRequest {

    @NotNull(message = "El nombre del checkpoint no puede ser nulo")
    private String name;

    @NotNull(message = "El orden del checkpoint no puede ser nulo")
    private Integer checkpointOrder;

    @NotNull(message = "La latitud no puede ser nula")
    private Double latitude;

    @NotNull(message = "La longitud no puede ser nula")
    private Double longitude;

    // Tolerancia en metros — null usa el default de 50m del servicio
    private Double toleranceMeters;

    public String getName() {
        return name;
    }

    public Integer getCheckpointOrder() {
        return checkpointOrder;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getToleranceMeters() {
        return toleranceMeters;
    }
}
