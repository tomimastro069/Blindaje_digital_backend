package com.blindaje.modules.round.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "checkpoint_verifications")
public class CheckpointVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long executionId;

    @Column(nullable = false)
    private Long templateCheckpointId;

    @Column(nullable = false)
    private Integer checkpointOrder;

    private String checkpointName;

    @Column(nullable = false)
    private Double guardLatitude;

    @Column(nullable = false)
    private Double guardLongitude;

    @Column(nullable = false)
    private LocalDateTime verifiedAt;

    @Column(nullable = false)
    private Boolean isValid;

    private Double distanceMeters;

    public CheckpointVerification() {
    }

    public CheckpointVerification(Long executionId, Long templateCheckpointId,
            Integer checkpointOrder, String checkpointName,
            Double guardLatitude, Double guardLongitude,
            Boolean isValid, Double distanceMeters) {
        this.executionId = executionId;
        this.templateCheckpointId = templateCheckpointId;
        this.checkpointOrder = checkpointOrder;
        this.checkpointName = checkpointName;
        this.guardLatitude = guardLatitude;
        this.guardLongitude = guardLongitude;
        this.isValid = isValid;
        this.distanceMeters = distanceMeters;
        this.verifiedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getExecutionId() {
        return executionId;
    }

    public Long getTemplateCheckpointId() {
        return templateCheckpointId;
    }

    public Integer getCheckpointOrder() {
        return checkpointOrder;
    }

    public String getCheckpointName() {
        return checkpointName;
    }

    public Double getGuardLatitude() {
        return guardLatitude;
    }

    public Double getGuardLongitude() {
        return guardLongitude;
    }

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public Boolean getIsValid() {
        return isValid;
    }

    public Double getDistanceMeters() {
        return distanceMeters;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setExecutionId(Long executionId) {
        this.executionId = executionId;
    }

    public void setTemplateCheckpointId(Long templateCheckpointId) {
        this.templateCheckpointId = templateCheckpointId;
    }

    public void setCheckpointOrder(Integer checkpointOrder) {
        this.checkpointOrder = checkpointOrder;
    }

    public void setCheckpointName(String checkpointName) {
        this.checkpointName = checkpointName;
    }

    public void setGuardLatitude(Double guardLatitude) {
        this.guardLatitude = guardLatitude;
    }

    public void setGuardLongitude(Double guardLongitude) {
        this.guardLongitude = guardLongitude;
    }

    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public void setIsValid(Boolean isValid) {
        this.isValid = isValid;
    }

    public void setDistanceMeters(Double distanceMeters) {
        this.distanceMeters = distanceMeters;
    }
}
