package com.blindaje.modules.round.dto;

public class CheckpointVerificationResponse {

    private Long verificationId;
    private Boolean isValid;
    private Double distanceMeters;
    private Double toleranceMeters;
    private String message;
    private Integer verifiedCheckpointOrder;
    private String verifiedCheckpointName;
    private Integer nextCheckpointOrder;
    private String nextCheckpointName;
    private Boolean roundCompleted;

    public CheckpointVerificationResponse() {
    }

    public CheckpointVerificationResponse(Long verificationId, Boolean isValid, Double distanceMeters,
            Double toleranceMeters, String message,
            Integer verifiedCheckpointOrder, String verifiedCheckpointName,
            Integer nextCheckpointOrder, String nextCheckpointName,
            Boolean roundCompleted) {
        this.verificationId = verificationId;
        this.isValid = isValid;
        this.distanceMeters = distanceMeters;
        this.toleranceMeters = toleranceMeters;
        this.message = message;
        this.verifiedCheckpointOrder = verifiedCheckpointOrder;
        this.verifiedCheckpointName = verifiedCheckpointName;
        this.nextCheckpointOrder = nextCheckpointOrder;
        this.nextCheckpointName = nextCheckpointName;
        this.roundCompleted = roundCompleted;
    }

    public Long getVerificationId() {
        return verificationId;
    }

    public Boolean getIsValid() {
        return isValid;
    }

    public Double getDistanceMeters() {
        return distanceMeters;
    }

    public Double getToleranceMeters() {
        return toleranceMeters;
    }

    public String getMessage() {
        return message;
    }

    public Integer getVerifiedCheckpointOrder() {
        return verifiedCheckpointOrder;
    }

    public String getVerifiedCheckpointName() {
        return verifiedCheckpointName;
    }

    public Integer getNextCheckpointOrder() {
        return nextCheckpointOrder;
    }

    public String getNextCheckpointName() {
        return nextCheckpointName;
    }

    public Boolean getRoundCompleted() {
        return roundCompleted;
    }
}
