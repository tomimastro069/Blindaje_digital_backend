package com.blindaje.modules.incident.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "incidents")
public class Incidente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private IncidenteStatus status;

    private LocalDateTime reportedAt;

    private LocalDateTime resolvedAt;

    private String reportedBy;

    private String tenantId;

    // TODO: Add getters, setters, and constructors
}
