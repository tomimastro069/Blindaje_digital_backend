package com.blindaje.modules.round.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "round_checkpoints")
public class RoundCheckpoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "checkpoint_order")
    private Integer checkpointOrder;

    private Double latitude;

    private Double longitude;

    private Double toleranceMeters;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "round_id", nullable = false)
    @JsonBackReference
    private Round round;

    public RoundCheckpoint() {
    }

    public RoundCheckpoint(String name, Integer checkpointOrder, Double latitude, Double longitude,
            Double toleranceMeters, Round round) {
        this.name = name;
        this.checkpointOrder = checkpointOrder;
        this.latitude = latitude;
        this.longitude = longitude;
        this.toleranceMeters = toleranceMeters != null ? toleranceMeters : 50.0;
        this.round = round;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getCheckpointOrder() {
        return checkpointOrder;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getToleranceMeters() {
        return toleranceMeters;
    }

    public Round getRound() {
        return round;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCheckpointOrder(Integer checkpointOrder) {
        this.checkpointOrder = checkpointOrder;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setToleranceMeters(Double toleranceMeters) {
        this.toleranceMeters = toleranceMeters;
    }

    public void setRound(Round round) {
        this.round = round;
    }
}
