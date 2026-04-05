package com.blindaje.modules.emergency.Dto;

import com.blindaje.modules.emergency.domain.Emergencia;
import com.blindaje.modules.emergency.domain.EmergenciaTipo;
import com.blindaje.modules.emergency.domain.EmergenciaStatus;

import java.time.LocalDateTime;

public class EmergenciaResponse {

    private Long id;
    private String title;
    private String description;
    private String message;
    private EmergenciaTipo type;
    private EmergenciaStatus status;
    private LocalDateTime triggeredAt;
    private LocalDateTime resolvedAt;
    private String triggeredBy;
    private Long reportedByUserId;
    private String tenantId;

    public EmergenciaResponse() {}

    public static EmergenciaResponse from(Emergencia e) {
        EmergenciaResponse dto = new EmergenciaResponse();
        dto.id = e.getId();
        dto.title = e.getTitle();
        dto.description = e.getDescription();
        dto.message = e.getMessage();
        dto.type = e.getType();
        dto.status = e.getStatus();
        dto.triggeredAt = e.getTriggeredAt();
        dto.resolvedAt = e.getResolvedAt();
        dto.triggeredBy = e.getTriggeredBy();
        dto.reportedByUserId = e.getReportedByUserId();
        dto.tenantId = e.getTenantId();
        return dto;
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
}
