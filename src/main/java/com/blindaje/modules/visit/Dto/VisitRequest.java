package com.blindaje.modules.visit.Dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;

public class VisitRequest {

    @NotBlank(message = "El nombre del visitante no puede estar vacío")
    private String visitorName;

    @NotBlank(message = "El documento del visitante no puede estar vacío")
    @Pattern(regexp = "\\d{7,8}", message = "El documento debe tener 7 u 8 dígitos")
    private String visitorDocument;

    private String vehiclePlate; // opcional

    @NotNull(message = "La fecha de visita no puede estar vacía")
    @Future(message = "La fecha de visita debe ser futura")
    private LocalDateTime scheduledAt;

    public String getVisitorName() { return visitorName; }
    public String getVisitorDocument() { return visitorDocument; }
    public String getVehiclePlate() { return vehiclePlate; }
    public LocalDateTime getScheduledAt() { return scheduledAt; }
}