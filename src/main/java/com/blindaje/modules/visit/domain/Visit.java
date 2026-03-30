package com.blindaje.modules.visit.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "visits")
public class Visit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String visitorName;
    private String visitorDocument;
    private String vehiclePlate;
    private LocalDateTime scheduledAt;  // fecha futura que pone el residente

    @Enumerated(EnumType.STRING)
    private VisitStatus status;

    private LocalDateTime entryTime;
    private LocalDateTime exitTime;

    private String tenantId;
    private Long residentId;  // quién creó la visita

    @OneToMany(mappedBy = "visit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Companion> companions = new ArrayList<>();

    public Visit() {}

    public Visit(String visitorName, String visitorDocument, String vehiclePlate,
                 LocalDateTime scheduledAt, String tenantId, Long residentId) {
        this.visitorName = visitorName;
        this.visitorDocument = visitorDocument;
        this.vehiclePlate = vehiclePlate;
        this.scheduledAt = scheduledAt;
        this.tenantId = tenantId;
        this.residentId = residentId;
        this.status = VisitStatus.APPROVED; // pre-autorizada directamente
    }

    public Long getId() { return id; }
    public String getVisitorName() { return visitorName; }
    public String getVisitorDocument() { return visitorDocument; }
    public String getVehiclePlate() { return vehiclePlate; }
    public LocalDateTime getScheduledAt() { return scheduledAt; }
    public VisitStatus getStatus() { return status; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public LocalDateTime getExitTime() { return exitTime; }
    public String getTenantId() { return tenantId; }
    public Long getResidentId() { return residentId; }
    public List<Companion> getCompanions() { return companions; }
    public void setCompanions(List<Companion> companions) { this.companions = companions; }

    public void setId(Long id) { this.id = id; }
    public void setVisitorName(String visitorName) { this.visitorName = visitorName; }
    public void setVisitorDocument(String visitorDocument) { this.visitorDocument = visitorDocument; }
    public void setVehiclePlate(String vehiclePlate) { this.vehiclePlate = vehiclePlate; }
    public void setScheduledAt(LocalDateTime scheduledAt) { this.scheduledAt = scheduledAt; }
    public void setStatus(VisitStatus status) { this.status = status; }
    public void setEntryTime(LocalDateTime entryTime) { this.entryTime = entryTime; }
    public void setExitTime(LocalDateTime exitTime) { this.exitTime = exitTime; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public void setResidentId(Long residentId) { this.residentId = residentId; }
}