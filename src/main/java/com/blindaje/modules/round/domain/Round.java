package com.blindaje.modules.round.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rounds")
public class Round {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private String tenantId;

    private Long createdByUserId;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "round", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("checkpointOrder ASC")
    @JsonManagedReference
    private List<RoundCheckpoint> checkpoints = new ArrayList<>();

    public Round() {
    }

    public Round(String name, String description, String tenantId, Long createdByUserId) {
        this.name = name;
        this.description = description;
        this.tenantId = tenantId;
        this.createdByUserId = createdByUserId;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getTenantId() {
        return tenantId;
    }

    public Long getCreatedByUserId() {
        return createdByUserId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<RoundCheckpoint> getCheckpoints() {
        return checkpoints;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public void setCreatedByUserId(Long createdByUserId) {
        this.createdByUserId = createdByUserId;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setCheckpoints(List<RoundCheckpoint> checkpoints) {
        this.checkpoints = checkpoints;
    }
}
