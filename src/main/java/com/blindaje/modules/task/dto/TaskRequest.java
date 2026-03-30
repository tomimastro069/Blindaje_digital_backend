package com.blindaje.modules.task.dto;

import com.blindaje.modules.task.domain.TaskPriority;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class TaskRequest {

    @NotBlank(message = "El título no puede estar vacío")
    @Size(min = 3, max = 100, message = "El título debe tener entre 3 y 100 caracteres")
    private String title;

    @NotBlank(message = "La descripción no puede estar vacía")
    private String description;

    @NotNull(message = "La prioridad no puede estar vacía")
    private TaskPriority priority;

    @NotNull(message = "La fecha límite no puede estar vacía")
    @Future(message = "La fecha límite debe ser futura")
    private LocalDateTime deadline;

    @NotNull(message = "Debe asignar la tarea a un guardia")
    private Long assignedToGuardId;

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public TaskPriority getPriority() { return priority; }
    public LocalDateTime getDeadline() { return deadline; }
    public Long getAssignedToGuardId() { return assignedToGuardId; }
}