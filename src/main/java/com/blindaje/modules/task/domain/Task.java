package com.blindaje.modules.task.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String observations;

    @Enumerated(EnumType.STRING)
    private TaskPriority priority;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    private LocalDateTime deadline;
    private Long createdByGuardId;   
    private Long assignedToGuardId;  
    private String tenantId;
    private LocalDateTime createdAt;

    public Task() {}

    public Task(String title, String description,
                TaskPriority priority, LocalDateTime deadline,
                Long createdByGuardId, Long assignedToGuardId, String tenantId) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.deadline = deadline;
        this.createdByGuardId = createdByGuardId;
        this.assignedToGuardId = assignedToGuardId;
        this.tenantId = tenantId;
        this.status = TaskStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getObservations() { return observations; }
    public TaskPriority getPriority() { return priority; }
    public TaskStatus getStatus() { return status; }
    public LocalDateTime getDeadline() { return deadline; }
    public Long getCreatedByGuardId() { return createdByGuardId; }
    public Long getAssignedToGuardId() { return assignedToGuardId; }
    public String getTenantId() { return tenantId; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setObservations(String observations) { this.observations = observations; }
    public void setPriority(TaskPriority priority) { this.priority = priority; }
    public void setStatus(TaskStatus status) { this.status = status; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }
    public void setCreatedByGuardId(Long createdByGuardId) { this.createdByGuardId = createdByGuardId; }
    public void setAssignedToGuardId(Long assignedToGuardId) { this.assignedToGuardId = assignedToGuardId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}