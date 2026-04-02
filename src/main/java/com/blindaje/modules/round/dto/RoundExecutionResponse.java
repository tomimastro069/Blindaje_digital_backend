package com.blindaje.modules.round.dto;

import com.blindaje.modules.round.domain.RoundExecution;

import java.time.LocalDateTime;

public class RoundExecutionResponse {

    private Long id;
    private Long roundId;
    private Long userId;
    private String tenantId;
    private RoundExecutionStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer currentCheckpointOrder;
    private Integer totalCheckpoints;

    public RoundExecutionResponse() {
    }

    public static RoundExecutionResponse from(RoundExecution execution) {
        RoundExecutionResponse r = new RoundExecutionResponse();
        r.id = execution.getId();
        r.roundId = execution.getRoundId();
        r.userId = execution.getUserId();
        r.tenantId = execution.getTenantId();
        r.status = execution.getStatus();
        r.startTime = execution.getStartTime();
        r.endTime = execution.getEndTime();
        r.currentCheckpointOrder = execution.getCurrentCheckpointOrder();
        r.totalCheckpoints = execution.getTotalCheckpoints();
        return r;
    }

    public Long getId() {
        return id;
    }

    public Long getRoundId() {
        return roundId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public RoundExecutionStatus getStatus() {
        return status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public Integer getCurrentCheckpointOrder() {
        return currentCheckpointOrder;
    }

    public Integer getTotalCheckpoints() {
        return totalCheckpoints;
    }
}
