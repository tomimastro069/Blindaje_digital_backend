package com.blindaje.core.event.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private EventoType type;

    @Enumerated(EnumType.STRING)
    private Severidad severity;

    private String description;

    private String source;

    private LocalDateTime createdAt;

    private String tenantId;

    // TODO: Add getters, setters, and constructors
    
}
