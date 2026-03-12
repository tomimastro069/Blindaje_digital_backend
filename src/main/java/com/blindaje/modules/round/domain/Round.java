package com.blindaje.modules.round.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "rounds")
public class Round {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String guardId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String notes;

    private String tenantId;

    // TODO: Add getters, setters, and constructors
}
