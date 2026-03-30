package com.blindaje.modules.visit.Dto;

import java.time.LocalDateTime;

public class VisitRequest {

    private String visitorName;
    private String visitorDocument;
    private String vehiclePlate;
    private LocalDateTime scheduledAt;

    public String getVisitorName() { return visitorName; }
    public String getVisitorDocument() { return visitorDocument; }
    public String getVehiclePlate() { return vehiclePlate; }
    public LocalDateTime getScheduledAt() { return scheduledAt; }
}