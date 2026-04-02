package com.blindaje.modules.round.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.blindaje.modules.round.dto.RoundExecutionStatus;

@Entity
@Table(name = "round_executions")
public class RoundExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "round_id", nullable = false)
    private Long roundId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String tenantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoundExecutionStatus status;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    // El orden del próximo checkpoint a verificar (1-indexed)
    @Column(nullable = false)
    private Integer currentCheckpointOrder;

    // Total de checkpoints de la plantilla (para saber cuándo completar)
    @Column(nullable = false)
    private Integer totalCheckpoints;

    public RoundExecution() {
    }

    public RoundExecution(Long roundId, Long userId, String tenantId, Integer totalCheckpoints) {
        this.roundId = roundId;
        this.userId = userId;
        this.tenantId = tenantId;
        this.status = RoundExecutionStatus.IN_PROGRESS;
        this.startTime = LocalDateTime.now();
        this.currentCheckpointOrder = 1;
        this.totalCheckpoints = totalCheckpoints;
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

    public void setId(Long id) {
        this.id = id;
    }

    public void setRoundId(Long roundId) {
        this.roundId = roundId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public void setStatus(RoundExecutionStatus status) {
        this.status = status;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setCurrentCheckpointOrder(Integer currentCheckpointOrder) {
        this.currentCheckpointOrder = currentCheckpointOrder;
    }

    public void setTotalCheckpoints(Integer totalCheckpoints) {
        this.totalCheckpoints = totalCheckpoints;
    }
}
