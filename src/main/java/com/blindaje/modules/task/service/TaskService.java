package com.blindaje.modules.task.service;

import com.blindaje.core.notification.service.NotificacionService;
import com.blindaje.modules.task.domain.Task;
import com.blindaje.modules.task.domain.TaskStatus;
import com.blindaje.modules.task.dto.TaskRequest;
import com.blindaje.modules.task.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final NotificacionService notificacionService;

    public TaskService(TaskRepository taskRepository,
                       NotificacionService notificacionService) {
        this.taskRepository = taskRepository;
        this.notificacionService = notificacionService;
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
        Task saved = taskRepository.save(task);

        if (!request.getAssignedToGuardId().equals(guardId)) {
            notificacionService.notificarUsuario(
                    request.getAssignedToGuardId(),
                    tenantId,
                    "Nueva tarea asignada",
                    "Te asignaron la tarea: " + request.getTitle() +
                    " con prioridad " + request.getPriority()
            );
        }

        return saved;
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