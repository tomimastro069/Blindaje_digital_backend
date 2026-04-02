package com.blindaje.modules.round.api;

import com.blindaje.config.security.JwtTokenProvider;
import com.blindaje.modules.round.domain.CheckpointVerification;
import com.blindaje.modules.round.dto.CheckpointVerificationDTO;
import com.blindaje.modules.round.domain.Round;
import com.blindaje.modules.round.dto.*;
import com.blindaje.modules.round.service.RoundService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rounds")
public class RoundController {

    private final RoundService roundService;
    private final JwtTokenProvider jwtTokenProvider;

    public RoundController(RoundService roundService, JwtTokenProvider jwtTokenProvider) {
        this.roundService = roundService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // ─── Plantillas ───────────────────────────────────────────────────────────

    /**
     * Crea una plantilla de ronda con sus checkpoints ordenados.
     * Roles: ADMIN, GUARD
     */
    @PostMapping("/plantillas")
    @PreAuthorize("hasRole('ADMIN') or hasRole('GUARD')")
    public ResponseEntity<Round> crearPlantilla(@Valid @RequestBody RoundTemplateRequest request,
            HttpServletRequest httpRequest) {
        String token = extraerToken(httpRequest);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        String tenantId = jwtTokenProvider.getTenantIdFromToken(token);
        return ResponseEntity.ok(roundService.crearPlantilla(request, userId, tenantId));
    }

    /**
     * Lista todas las plantillas de ronda del tenant.
     * Roles: ADMIN, GUARD
     */
    @GetMapping("/plantillas")
    @PreAuthorize("hasRole('ADMIN') or hasRole('GUARD')")
    public ResponseEntity<List<Round>> listarPlantillas(HttpServletRequest httpRequest) {
        String token = extraerToken(httpRequest);
        String tenantId = jwtTokenProvider.getTenantIdFromToken(token);
        return ResponseEntity.ok(roundService.obtenerPlantillas(tenantId));
    }

    /**
     * Obtiene una plantilla específica por ID.
     * Roles: ADMIN, GUARD
     */
    @GetMapping("/plantillas/{roundId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('GUARD')")
    public ResponseEntity<Round> obtenerPlantilla(@PathVariable Long roundId,
            HttpServletRequest httpRequest) {
        String token = extraerToken(httpRequest);
        String tenantId = jwtTokenProvider.getTenantIdFromToken(token);
        return ResponseEntity.ok(roundService.obtenerPlantillaPorId(roundId, tenantId));
    }

    // ─── Ejecuciones ─────────────────────────────────────────────────────────

    /**
     * Inicia una nueva ejecución de ronda a partir de una plantilla.
     * El usuario no puede tener otra ronda activa simultáneamente.
     * Roles: ADMIN, GUARD
     */
    @PostMapping("/ejecuciones")
    @PreAuthorize("hasRole('ADMIN') or hasRole('GUARD')")
    public ResponseEntity<RoundExecutionResponse> iniciarRonda(@RequestParam Long roundId,
            HttpServletRequest httpRequest) {
        String token = extraerToken(httpRequest);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        String tenantId = jwtTokenProvider.getTenantIdFromToken(token);
        return ResponseEntity.ok(roundService.iniciarRonda(roundId, userId, tenantId));
    }

    /**
     * Obtiene la ronda activa del usuario autenticado.
     * Roles: ADMIN, GUARD
     */
    @GetMapping("/ejecuciones/activa")
    @PreAuthorize("hasRole('ADMIN') or hasRole('GUARD')")
    public ResponseEntity<RoundExecutionResponse> obtenerRondaActiva(HttpServletRequest httpRequest) {
        String token = extraerToken(httpRequest);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        return ResponseEntity.ok(roundService.obtenerEjecucionActiva(userId));
    }

    /**
     * Lista todas las ejecuciones del tenant (historial completo).
     * Roles: ADMIN, GUARD
     */
    @GetMapping("/ejecuciones")
    @PreAuthorize("hasRole('ADMIN') or hasRole('GUARD')")
    public ResponseEntity<List<RoundExecutionResponse>> listarEjecuciones(HttpServletRequest httpRequest) {
        String token = extraerToken(httpRequest);
        String tenantId = jwtTokenProvider.getTenantIdFromToken(token);
        return ResponseEntity.ok(roundService.obtenerEjecucionesPorTenant(tenantId));
    }

    /**
     * El usuario registra su ubicación GPS actual para verificar el checkpoint en
     * curso.
     * Si la distancia está dentro de la tolerancia, avanza al siguiente checkpoint.
     * Al completar el último checkpoint, la ronda se cierra como COMPLETED.
     * Roles: ADMIN, GUARD
     */
    @PostMapping("/ejecuciones/{executionId}/verificar")
    @PreAuthorize("hasRole('ADMIN') or hasRole('GUARD')")
    public ResponseEntity<CheckpointVerificationResponse> verificarCheckpoint(
            @PathVariable Long executionId,
            @Valid @RequestBody CheckpointVerificationRequest request,
            HttpServletRequest httpRequest) {
        String token = extraerToken(httpRequest);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        String tenantId = jwtTokenProvider.getTenantIdFromToken(token);
        return ResponseEntity.ok(roundService.verificarCheckpoint(executionId, request, userId, tenantId));
    }

    /**
     * Aborta la ronda activa del usuario.
     * Roles: ADMIN, GUARD
     */
    @PatchMapping("/ejecuciones/{executionId}/abortar")
    @PreAuthorize("hasRole('ADMIN') or hasRole('GUARD')")
    public ResponseEntity<RoundExecutionResponse> abortarRonda(@PathVariable Long executionId,
            HttpServletRequest httpRequest) {
        String token = extraerToken(httpRequest);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        String tenantId = jwtTokenProvider.getTenantIdFromToken(token);
        return ResponseEntity.ok(roundService.abortarRonda(executionId, userId, tenantId));
    }

    /**
     * Obtiene el historial de verificaciones de una ejecución.
     * Roles: ADMIN, GUARD
     */
    @GetMapping("/ejecuciones/{executionId}/verificaciones")
    @PreAuthorize("hasRole('ADMIN') or hasRole('GUARD')")
    public ResponseEntity<List<CheckpointVerificationDTO>> obtenerVerificaciones(
            @PathVariable Long executionId,
            HttpServletRequest httpRequest) {
        String token = extraerToken(httpRequest);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        String tenantId = jwtTokenProvider.getTenantIdFromToken(token);
        return ResponseEntity.ok(roundService.obtenerVerificacionesPorEjecucion(executionId, userId, tenantId));
    }

    // ─── Utils ────────────────────────────────────────────────────────────────

    private String extraerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        throw new RuntimeException("Token no encontrado");
    }
}
