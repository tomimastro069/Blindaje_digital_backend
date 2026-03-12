package com.blindaje.modules.visit.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "visits")
public class Visit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String visitorName;

    private String visitorDocument;

    @Enumerated(EnumType.STRING)
    private VisitStatus status;

    private LocalDateTime entryTime;

    private LocalDateTime exitTime;

    private String tenantId;

    // TODO: Add getters, setters, and constructors
}
