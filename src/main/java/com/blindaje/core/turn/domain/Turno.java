package com.blindaje.core.turn.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "turns")
public class Turno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String guardId;

    private String tenantId;

    // TODO: Add getters, setters, and constructors
}
