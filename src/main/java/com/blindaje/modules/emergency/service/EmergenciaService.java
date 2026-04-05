package com.blindaje.modules.emergency.service;

import com.blindaje.core.notification.service.WebSocketPublisher;
import com.blindaje.modules.emergency.Dto.EmergenciaRequest;
import com.blindaje.modules.emergency.Dto.EmergenciaResponse;
import com.blindaje.modules.emergency.domain.Emergencia;
import com.blindaje.modules.emergency.domain.EmergenciaStatus;
import com.blindaje.modules.emergency.repository.EmergenciaRepository;
import com.blindaje.shared.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmergenciaService {

    private static final Logger log = LoggerFactory.getLogger(EmergenciaService.class);

    private final EmergenciaRepository emergencyRepository;
    private final WebSocketPublisher webSocketPublisher;

    public EmergenciaService(EmergenciaRepository emergencyRepository,
                              WebSocketPublisher webSocketPublisher) {
        this.emergencyRepository = emergencyRepository;
        this.webSocketPublisher = webSocketPublisher;
    }

    /**
     * Reporta una emergencia. Guarda en DB primero y luego publica por WebSocket.
     * Regla: guardar en DB → luego WebSocket (ambas obligatorias).
     */
    public EmergenciaResponse reportarEmergencia(EmergenciaRequest request, Long userId, String tenantId) {
        Emergencia emergencia = new Emergencia(
                request.getTitle(),
                request.getDescription(),
                request.getMessage(),
                request.getType(),
                EmergenciaStatus.TRIGGERED,
                LocalDateTime.now(),
                userId.toString(),
                userId,
                tenantId
        );

        // 1. Guardar en DB primero
        emergencia = emergencyRepository.save(emergencia);
        log.info("Emergencia reportada: id={}, type={}, tenant={}", emergencia.getId(), emergencia.getType(), tenantId);

        // 2. Publicar broadcast por WebSocket al topic del tenant
        EmergenciaPayload payload = new EmergenciaPayload(emergencia);
        webSocketPublisher.publish("/topic/emergencias/" + tenantId, payload);
        log.info("Emergencia publicada por WebSocket: /topic/emergencias/{}", tenantId);

        return EmergenciaResponse.from(emergencia);
    }

    /**
     * Lista todas las emergencias del tenant, ordenadas por fecha descendente.
     */
    public List<EmergenciaResponse> obtenerEmergenciasPorTenant(String tenantId) {
        return emergencyRepository.findByTenantIdOrderByTriggeredAtDesc(tenantId)
                .stream()
                .map(EmergenciaResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * Lista solo las emergencias activas (TRIGGERED o IN_PROGRESS) del tenant.
     */
    public List<EmergenciaResponse> obtenerActivas(String tenantId) {
        List<EmergenciaStatus> activeStatuses = List.of(
                EmergenciaStatus.TRIGGERED,
                EmergenciaStatus.IN_PROGRESS
        );
        return emergencyRepository.findByTenantIdAndStatusInOrderByTriggeredAtDesc(tenantId, activeStatuses)
                .stream()
                .map(EmergenciaResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * Resuelve una emergencia. Solo si pertenece al tenant del usuario.
     */
    public EmergenciaResponse resolverEmergencia(Long id, String tenantId) {
        Emergencia emergencia = emergencyRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Emergencia no encontrada: " + id));

        if (!emergencia.getTenantId().equals(tenantId)) {
            throw new BusinessException("No tenés permiso sobre esta emergencia");
        }

        emergencia.setStatus(EmergenciaStatus.RESOLVED);
        emergencia.setResolvedAt(LocalDateTime.now());
        emergencia = emergencyRepository.save(emergencia);
        log.info("Emergencia resuelta: id={}, tenant={}", id, tenantId);

        // Notificar resolución por WebSocket
        webSocketPublisher.publish("/topic/emergencias/" + tenantId, new EmergenciaPayload(emergencia));

        return EmergenciaResponse.from(emergencia);
    }

    /**
     * Marca una emergencia como falsa alarma. Solo si pertenece al tenant.
     */
    public EmergenciaResponse marcarFalsaAlarma(Long id, String tenantId) {
        Emergencia emergencia = emergencyRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Emergencia no encontrada: " + id));

        if (!emergencia.getTenantId().equals(tenantId)) {
            throw new BusinessException("No tenés permiso sobre esta emergencia");
        }

        emergencia.setStatus(EmergenciaStatus.FALSE_ALARM);
        emergencia.setResolvedAt(LocalDateTime.now());
        emergencia = emergencyRepository.save(emergencia);
        log.info("Emergencia marcada como falsa alarma: id={}, tenant={}", id, tenantId);

        // Notificar actualización por WebSocket
        webSocketPublisher.publish("/topic/emergencias/" + tenantId, new EmergenciaPayload(emergencia));

        return EmergenciaResponse.from(emergencia);
    }

    // --- Payload WebSocket interno ---

    public static class EmergenciaPayload {
        private Long id;
        private String title;
        private String description;
        private String message;
        private String type;
        private String status;
        private String triggeredAt;
        private String tenantId;

        public EmergenciaPayload(Emergencia e) {
            this.id = e.getId();
            this.title = e.getTitle();
            this.description = e.getDescription();
            this.message = e.getMessage();
            this.type = e.getType() != null ? e.getType().name() : null;
            this.status = e.getStatus() != null ? e.getStatus().name() : null;
            this.triggeredAt = e.getTriggeredAt() != null ? e.getTriggeredAt().toString() : null;
            this.tenantId = e.getTenantId();
        }

        public Long getId() { return id; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getMessage() { return message; }
        public String getType() { return type; }
        public String getStatus() { return status; }
        public String getTriggeredAt() { return triggeredAt; }
        public String getTenantId() { return tenantId; }
    }
}
