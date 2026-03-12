package com.blindaje.modules.round.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "round_checkpoints")
public class RoundCheckpoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Double latitude;

    private Double longitude;

    @ManyToOne
    @JoinColumn(name = "round_id")
    private Round round;

    // TODO: Add getters, setters, and constructors
}
