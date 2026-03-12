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

    @Enumerated(EnumType.STRING)
    private EmergenciaStatus status;

    private LocalDateTime triggeredAt;

    private LocalDateTime resolvedAt;

    private String triggeredBy;

    private String tenantId;
    
}