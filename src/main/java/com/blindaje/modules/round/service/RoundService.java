package com.blindaje.modules.round.service;

import com.blindaje.core.notification.service.NotificacionService;
import com.blindaje.modules.round.domain.*;
import com.blindaje.modules.round.dto.*;
import com.blindaje.modules.round.repository.*;
import com.blindaje.shared.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.blindaje.modules.round.dto.CheckpointVerificationDTO;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RoundService {

    private static final Logger log = LoggerFactory.getLogger(RoundService.class);
    private static final double EARTH_RADIUS_METERS = 6_371_000.0;
    private static final double DEFAULT_TOLERANCE_METERS = 50.0;

    private final RoundRepository roundRepository;
    private final RoundExecutionRepository executionRepository;
    private final CheckpointVerificationRepository verificationRepository;
    private final NotificacionService notificacionService;

    public RoundService(RoundRepository roundRepository,
            RoundExecutionRepository executionRepository,
            CheckpointVerificationRepository verificationRepository,
            NotificacionService notificacionService) {
        this.roundRepository = roundRepository;
        this.executionRepository = executionRepository;
        this.verificationRepository = verificationRepository;
        this.notificacionService = notificacionService;
    }

    // ─── Gestión de plantillas ────────────────────────────────────────────────

    @Transactional
    public Round crearPlantilla(RoundTemplateRequest request, Long userId, String tenantId) {
        Round round = new Round(request.getName(), request.getDescription(), tenantId, userId);

        List<CheckpointRequest> items = request.getCheckpoints();
        for (CheckpointRequest item : items) {
            double tolerancia = item.getToleranceMeters() != null
                    ? item.getToleranceMeters()
                    : DEFAULT_TOLERANCE_METERS;
            RoundCheckpoint cp = new RoundCheckpoint(
                    item.getName(),
                    item.getCheckpointOrder(),
                    item.getLatitude(),
                    item.getLongitude(),
                    tolerancia,
                    round);
            round.getCheckpoints().add(cp);
        }

        Round saved = roundRepository.save(round);
        log.info("Plantilla de ronda creada: id={}, nombre='{}', tenant={}, checkpoints={}",
                saved.getId(), saved.getName(), tenantId, saved.getCheckpoints().size());
        return saved;
    }

    public List<Round> obtenerPlantillas(String tenantId) {
        return roundRepository.findByTenantId(tenantId);
    }

    public Round obtenerPlantillaPorId(Long roundId, String tenantId) {
        Round round = roundRepository.findById(roundId)
                .orElseThrow(() -> new BusinessException("Plantilla de ronda no encontrada"));
        if (!round.getTenantId().equals(tenantId)) {
            throw new BusinessException("No tenés acceso a esta plantilla");
        }

        return round;
    }

    // ─── Ejecuciones ─────────────────────────────────────────────────────────

    @Transactional
    public RoundExecutionResponse iniciarRonda(Long roundId, Long userId, String tenantId) {
        // Verificar que no haya una ronda activa para este usuario
        executionRepository.findByUserIdAndStatus(userId, RoundExecutionStatus.IN_PROGRESS)
                .ifPresent(activa -> {
                    throw new BusinessException(
                            "Ya tenés una ronda en curso (id=" + activa.getId() + "). " +
                                    "Completala o abortala antes de iniciar una nueva.");
                });

        Round round = roundRepository.findById(roundId)
                .orElseThrow(() -> new BusinessException("Plantilla de ronda no encontrada"));

        if (!round.getTenantId().equals(tenantId)) {
            throw new BusinessException("No tenés acceso a esta plantilla");
        }
        if (round.getCheckpoints().isEmpty()) {
            throw new BusinessException("La ronda no tiene checkpoints configurados");
        }

        RoundExecution execution = new RoundExecution(roundId, userId, tenantId,
                round.getCheckpoints().size());
        RoundExecution saved = executionRepository.save(execution);

        log.info("Ronda iniciada: executionId={}, userId={}, roundId={}, checkpoints={}",
                saved.getId(), userId, roundId, saved.getTotalCheckpoints());

        return RoundExecutionResponse.from(saved);
    }

    public RoundExecutionResponse obtenerEjecucionActiva(Long userId) {
        RoundExecution activa = executionRepository.findByUserIdAndStatus(userId, RoundExecutionStatus.IN_PROGRESS)
                .orElseThrow(() -> new BusinessException("No tenés ninguna ronda activa en este momento"));
        return RoundExecutionResponse.from(activa);
    }

    public List<RoundExecutionResponse> obtenerEjecucionesPorTenant(String tenantId) {
        return executionRepository.findByTenantId(tenantId)
                .stream()
                .map(RoundExecutionResponse::from)
                .toList();
    }

    // ─── Verificación de checkpoint ───────────────────────────────────────────

    @Transactional
    public CheckpointVerificationResponse verificarCheckpoint(Long executionId,
            CheckpointVerificationRequest request,
            Long userId,
            String tenantId) {
        RoundExecution execution = executionRepository.findById(executionId)
                .orElseThrow(() -> new BusinessException("Ejecución de ronda no encontrada"));

        if (!execution.getUserId().equals(userId)) {
            throw new BusinessException("Esta ronda no te pertenece");
        }
        if (!execution.getTenantId().equals(tenantId)) {
            throw new BusinessException("No tenés acceso a esta ejecución");
        }
        if (execution.getStatus() != RoundExecutionStatus.IN_PROGRESS) {
            throw new BusinessException("Esta ronda ya está " + execution.getStatus().name().toLowerCase());
        }

        // Obtener plantilla y el checkpoint actual
        Round round = roundRepository.findById(execution.getRoundId())
                .orElseThrow(() -> new BusinessException("Plantilla de ronda no encontrada"));

        int ordenActual = execution.getCurrentCheckpointOrder();
        RoundCheckpoint checkpointActual = round.getCheckpoints().stream()
                .filter(cp -> cp.getCheckpointOrder().equals(ordenActual))
                .findFirst()
                .orElseThrow(() -> new BusinessException("Checkpoint no encontrado en la plantilla"));

        // Calcular distancia con Haversine
        double distancia = haversine(
                request.getLatitude(), request.getLongitude(),
                checkpointActual.getLatitude(), checkpointActual.getLongitude());

        double tolerancia = checkpointActual.getToleranceMeters() != null
                ? checkpointActual.getToleranceMeters()
                : DEFAULT_TOLERANCE_METERS;

        boolean valido = distancia <= tolerancia;

        // Registrar verificación (siempre, para auditoría)
        CheckpointVerification verificacion = new CheckpointVerification(
                executionId,
                checkpointActual.getId(),
                ordenActual,
                checkpointActual.getName(),
                request.getLatitude(),
                request.getLongitude(),
                valido,
                Math.round(distancia * 100.0) / 100.0);
        CheckpointVerification saved = verificationRepository.save(verificacion);

        log.info("Verificación checkpoint: executionId={}, orden={}, valido={}, distancia={}m",
                executionId, ordenActual, valido, distancia);

        if (!valido) {
            String mensaje = String.format(
                    "Fuera de rango. Estás a %.1f m del checkpoint '%s'. Tolerancia: %.0f m.",
                    distancia, checkpointActual.getName(), tolerancia);
            notificacionService.notificarUsuario(
                    userId,
                    tenantId,
                    "Checkpoint fallido",
                    "Estás fuera de rango en '" + checkpointActual.getName() + "' (" + Math.round(distancia) + "m)");
            notificacionService.notificarTenant(
                    tenantId,
                    "Checkpoint fallido",
                    "Usuario " + userId + " falló en '" + checkpointActual.getName() + "' (" + Math.round(distancia)
                            + "m)");

            return new CheckpointVerificationResponse(
                    saved.getId(), false, distancia, tolerancia, mensaje,
                    ordenActual, checkpointActual.getName(), ordenActual, checkpointActual.getName(), false);
        }
        // 🔔 NOTIFICACIÓN OK
        notificacionService.notificarUsuario(
                userId,
                tenantId,
                "Checkpoint verificado",
                "Llegaste a '" + checkpointActual.getName() + "' correctamente");
        // Verificación válida → avanzar
        int siguienteOrden = ordenActual + 1;
        boolean rondaCompleta = siguienteOrden > execution.getTotalCheckpoints();

        if (rondaCompleta) {
            execution.setStatus(RoundExecutionStatus.COMPLETED);
            execution.setEndTime(LocalDateTime.now());
            executionRepository.save(execution);

            notificacionService.notificarUsuario(
                    userId,
                    tenantId,
                    "Ronda completada",
                    "Completaste todos los checkpoints");
            notificacionService.notificarTenant(
                    tenantId,
                    "Ronda completada",
                    "Usuario " + userId + " completó la ronda");
            return new CheckpointVerificationResponse(
                    saved.getId(), true,
                    Math.round(distancia * 100.0) / 100.0, tolerancia,
                    "¡Ronda completada! Llegaste al último checkpoint '" + checkpointActual.getName() + "'.",
                    ordenActual, checkpointActual.getName(), null, null, true);
        }

        notificacionService.notificarUsuario(
                userId,
                tenantId,
                "Checkpoint verificado",
                "Llegaste a '" + checkpointActual.getName() + "' correctamente");

        // Avanzar al siguiente checkpoint
        execution.setCurrentCheckpointOrder(siguienteOrden);
        executionRepository.save(execution);

        notificacionService.notificarTenant(
                tenantId,
                "Progreso de ronda",
                "Usuario " + userId + " verificó '" + checkpointActual.getName() + "'");

        RoundCheckpoint siguiente = round.getCheckpoints().stream()
                .filter(cp -> cp.getCheckpointOrder().equals(siguienteOrden))
                .findFirst()
                .orElse(null);

        String nombreSiguiente = siguiente != null ? siguiente.getName() : "Desconocido";
        notificacionService.notificarUsuario(
                userId,
                tenantId,
                "Siguiente checkpoint",
                "Dirigite a '" + nombreSiguiente + "'");

        String mensaje = String.format(
                "Checkpoint '%s' verificado correctamente (%.1f m). Siguiente: '%s'.",
                checkpointActual.getName(), distancia, nombreSiguiente);

        return new CheckpointVerificationResponse(
                saved.getId(), true,
                Math.round(distancia * 100.0) / 100.0, tolerancia, mensaje,
                ordenActual, checkpointActual.getName(), siguienteOrden, nombreSiguiente, false);
    }

    @Transactional
    public RoundExecutionResponse abortarRonda(Long executionId, Long userId, String tenantId) {
        RoundExecution execution = executionRepository.findById(executionId)
                .orElseThrow(() -> new BusinessException("Ejecución de ronda no encontrada"));

        if (!execution.getUserId().equals(userId)) {
            throw new BusinessException("Esta ronda no te pertenece");
        }
        if (!execution.getTenantId().equals(tenantId)) {
            throw new BusinessException("No tenés acceso a esta ejecución");
        }
        if (execution.getStatus() != RoundExecutionStatus.IN_PROGRESS) {
            throw new BusinessException("Solo se pueden abortar rondas en curso");
        }

        execution.setStatus(RoundExecutionStatus.ABORTED);
        execution.setEndTime(LocalDateTime.now());
        RoundExecution saved = executionRepository.save(execution);

        log.info("Ronda ABORTADA: executionId={}, userId={}", executionId, userId);
        return RoundExecutionResponse.from(saved);
    }

    public List<CheckpointVerificationDTO> obtenerVerificacionesPorEjecucion(Long executionId,
            Long userId,
            String tenantId) {
        RoundExecution execution = executionRepository.findById(executionId)
                .orElseThrow(() -> new BusinessException("Ejecución no encontrada"));
        if (!execution.getTenantId().equals(tenantId)) {
            throw new BusinessException("No tenés acceso a esta ejecución");
        }
        return verificationRepository.findByExecutionId(executionId)
                .stream()
                .map(CheckpointVerificationDTO::new)
                .toList();
    }

    // ─── Haversine ────────────────────────────────────────────────────────────

    /**
     * Calcula la distancia en metros entre dos coordenadas GPS usando la fórmula de
     * Haversine.
     */
    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_METERS * c;
    }
}
