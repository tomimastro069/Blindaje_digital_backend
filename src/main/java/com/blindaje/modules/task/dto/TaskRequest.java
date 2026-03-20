package com.blindaje.modules.task.dto;

import com.blindaje.modules.task.domain.TaskPriority;

import java.time.LocalDateTime;

public class TaskRequest {

    private String title;
    private String description;
    private TaskPriority priority;
    private LocalDateTime deadline;
    private Long assignedToGuardId;

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public TaskPriority getPriority() { return priority; }
    public LocalDateTime getDeadline() { return deadline; }
    public Long getAssignedToGuardId() { return assignedToGuardId; }
}