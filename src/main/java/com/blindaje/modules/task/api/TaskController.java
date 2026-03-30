package com.blindaje.modules.task.api;

import com.blindaje.config.security.JwtTokenProvider;
import com.blindaje.modules.task.domain.Task;
import com.blindaje.modules.task.domain.TaskStatus;
import com.blindaje.modules.task.dto.TaskRequest;
import com.blindaje.modules.task.service.TaskService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;
    private final JwtTokenProvider jwtTokenProvider;

    public TaskController(TaskService taskService, JwtTokenProvider jwtTokenProvider) {
        this.taskService = taskService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // Guardia crea una tarea
    @PostMapping
    @PreAuthorize("hasRole('GUARD')")
    public ResponseEntity<Task> crearTarea(@Valid @RequestBody TaskRequest request,
                                            HttpServletRequest httpRequest) {
        String token = extraerToken(httpRequest);
        Long guardId = jwtTokenProvider.getUserIdFromToken(token);
        String tenantId = jwtTokenProvider.getTenantIdFromToken(token);

        return ResponseEntity.ok(taskService.crearTarea(request, guardId, tenantId));
    }

    // Guardia ve sus tareas asignadas
    @GetMapping("/mis-tareas")
    @PreAuthorize("hasRole('GUARD')")
    public ResponseEntity<List<Task>> misTareas(HttpServletRequest httpRequest) {
        String token = extraerToken(httpRequest);
        Long guardId = jwtTokenProvider.getUserIdFromToken(token);
        return ResponseEntity.ok(taskService.obtenerMisTareas(guardId));
    }

    // Guardia actualiza el estado de una tarea
    @PatchMapping("/{taskId}/estado")
    @PreAuthorize("hasRole('GUARD')")
    public ResponseEntity<Task> actualizarEstado(@PathVariable Long taskId,
                                                  @RequestParam TaskStatus estado,
                                                  HttpServletRequest httpRequest) {
        String token = extraerToken(httpRequest);
        Long guardId = jwtTokenProvider.getUserIdFromToken(token);
        return ResponseEntity.ok(taskService.actualizarEstado(taskId, estado, guardId));
    }

    // Admin ve todas las tareas del tenant
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Task>> todasLasTareas(HttpServletRequest httpRequest) {
        String token = extraerToken(httpRequest);
        String tenantId = jwtTokenProvider.getTenantIdFromToken(token);
        return ResponseEntity.ok(taskService.obtenerTareasPorTenant(tenantId));
    }

    @PatchMapping("/{taskId}/observaciones")
    @PreAuthorize("hasRole('GUARD')")
    public ResponseEntity<Task> agregarObservaciones(@PathVariable Long taskId,
                                                  @RequestParam String observaciones,
                                                  HttpServletRequest httpRequest) {
    String token = extraerToken(httpRequest);
    Long guardId = jwtTokenProvider.getUserIdFromToken(token);
    return ResponseEntity.ok(taskService.agregarObservaciones(taskId, observaciones, guardId));
    }

    private String extraerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        throw new RuntimeException("Token no encontrado");
    }
}