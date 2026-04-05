package com.blindaje.modules.emergency.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "emergencies")
public class Emergencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    // Mensaje adicional que detalla el problema con más precisión
    @Column(length = 1000)
    private String message;

    @Enumerated(EnumType.STRING)
    private EmergenciaTipo type;

    @Enumerated(EnumType.STRING)
    private EmergenciaStatus status;

    private LocalDateTime triggeredAt;

    private LocalDateTime resolvedAt;

    private String triggeredBy;

    private Long reportedByUserId;

    private String tenantId;

    public Emergencia() {
    }

    public Emergencia(String title, String description, String message,
                      EmergenciaTipo type, EmergenciaStatus status,
                      LocalDateTime triggeredAt, String triggeredBy,
                      Long reportedByUserId, String tenantId) {
        this.title = title;
        this.description = description;
        this.message = message;
        this.type = type;
        this.status = status;
        this.triggeredAt = triggeredAt;
        this.triggeredBy = triggeredBy;
        this.reportedByUserId = reportedByUserId;
        this.tenantId = tenantId;
    }

    // --- Getters ---

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getMessage() { return message; }
    public EmergenciaTipo getType() { return type; }
    public EmergenciaStatus getStatus() { return status; }
    public LocalDateTime getTriggeredAt() { return triggeredAt; }
    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public String getTriggeredBy() { return triggeredBy; }
    public Long getReportedByUserId() { return reportedByUserId; }
    public String getTenantId() { return tenantId; }

    // --- Setters ---

    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setMessage(String message) { this.message = message; }
    public void setType(EmergenciaTipo type) { this.type = type; }
    public void setStatus(EmergenciaStatus status) { this.status = status; }
    public void setTriggeredAt(LocalDateTime triggeredAt) { this.triggeredAt = triggeredAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
    public void setTriggeredBy(String triggeredBy) { this.triggeredBy = triggeredBy; }
    public void setReportedByUserId(Long reportedByUserId) { this.reportedByUserId = reportedByUserId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
}