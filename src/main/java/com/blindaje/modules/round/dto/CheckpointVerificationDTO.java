package com.blindaje.modules.round.dto;

import java.time.LocalDateTime;

import com.blindaje.modules.round.domain.CheckpointVerification;

public class CheckpointVerificationDTO {
    private Long id;
    private Integer order;
    private String name;
    private Double lat;
    private Double lon;
    private Boolean valid;
    private Double distance;
    private LocalDateTime timestamp;

    public CheckpointVerificationDTO(CheckpointVerification v) {
        this.id = v.getId();
        this.order = v.getCheckpointOrder();
        this.name = v.getCheckpointName();
        this.lat = v.getGuardLatitude();
        this.lon = v.getGuardLongitude();
        this.valid = v.getIsValid();
        this.distance = v.getDistanceMeters();
        this.timestamp = v.getVerifiedAt();
    }
    public CheckpointVerificationDTO() {}

    public Long getId() { return id; }
    public Integer getOrder() { return order; }
    public String getName() { return name; }
    public Double getLat() { return lat; }
    public Double getLon() { return lon; }
    public Boolean getValid() { return valid; }
    public Double getDistance() { return distance; }
    public LocalDateTime getTimestamp() { return timestamp; }
}