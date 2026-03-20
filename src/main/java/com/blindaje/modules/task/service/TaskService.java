package com.blindaje.modules.task.service;

import com.blindaje.modules.task.domain.Task;
import com.blindaje.modules.task.domain.TaskStatus;
import com.blindaje.modules.task.dto.TaskRequest;
import com.blindaje.modules.task.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task crearTarea(TaskRequest request, Long guardId, String tenantId) {
        Task task = new Task(
                request.getTitle(),
                request.getDescription(),
                request.getPriority(),
                request.getDeadline(),
                guardId,
                request.getAssignedToGuardId(),
                tenantId
        );
        return taskRepository.save(task);
    }

    public List<Task> obtenerMisTareas(Long guardId) {
        return taskRepository.findByAssignedToGuardId(guardId);
    }

    public Task actualizarEstado(Long taskId, TaskStatus nuevoEstado, Long guardId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

        if (!task.getAssignedToGuardId().equals(guardId)) {
            throw new RuntimeException("No tenés permiso para modificar esta tarea");
        }

        task.setStatus(nuevoEstado);
        return taskRepository.save(task);
    }
    public Task agregarObservaciones(Long taskId, String observaciones, Long guardId) {
    Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

    if (!task.getAssignedToGuardId().equals(guardId)) {
        throw new RuntimeException("No tenés permiso para modificar esta tarea");
    }

    task.setObservations(observaciones);
    return taskRepository.save(task);
    }

    public List<Task> obtenerTareasPorTenant(String tenantId) {
        return taskRepository.findByTenantId(tenantId);
    }
}