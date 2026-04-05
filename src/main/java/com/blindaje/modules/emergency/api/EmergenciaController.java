package com.blindaje.modules.emergency.api;

import com.blindaje.config.security.JwtTokenProvider;
import com.blindaje.modules.emergency.Dto.EmergenciaRequest;
import com.blindaje.modules.emergency.Dto.EmergenciaResponse;
import com.blindaje.modules.emergency.service.EmergenciaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emergencias")
public class EmergenciaController {

    private static final Logger log = LoggerFactory.getLogger(EmergenciaController.class);

    private final EmergenciaService emergencyService;
    private final JwtTokenProvider jwtTokenProvider;

    public EmergenciaController(EmergenciaService emergencyService,
                                 JwtTokenProvider jwtTokenProvider) {
        this.emergencyService = emergencyService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * POST /api/emergencias
     * Reportar una nueva emergencia. Solo GUARD y ADMIN.
     * Dispara alerta en tiempo real por WebSocket a /topic/emergencias/{tenantId}.
     */
    @PostMapping
    @PreAuthorize("hasRole('GUARD') or hasRole('ADMIN')")
    public ResponseEntity<EmergenciaResponse> reportarEmergencia(
            @Valid @RequestBody EmergenciaRequest request,
            HttpServletRequest httpRequest) {

        String token = extraerToken(httpRequest);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        String tenantId = jwtTokenProvider.getTenantIdFromToken(token);

        log.info("Reporte de emergencia iniciado por userId={} en tenant={}", userId, tenantId);
        EmergenciaResponse response = emergencyService.reportarEmergencia(request, userId, tenantId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/emergencias
     * Listar todas las emergencias del tenant. Solo GUARD y ADMIN.
     */
    @GetMapping
    @PreAuthorize("hasRole('GUARD') or hasRole('ADMIN')")
    public ResponseEntity<List<EmergenciaResponse>> listarEmergencias(HttpServletRequest httpRequest) {
        String token = extraerToken(httpRequest);
        String tenantId = jwtTokenProvider.getTenantIdFromToken(token);
        return ResponseEntity.ok(emergencyService.obtenerEmergenciasPorTenant(tenantId));
    }

    /**
     * GET /api/emergencias/activas
     * Listar solo emergencias activas (TRIGGERED o IN_PROGRESS). Solo GUARD y ADMIN.
     */
    @GetMapping("/activas")
    @PreAuthorize("hasRole('GUARD') or hasRole('ADMIN')")
    public ResponseEntity<List<EmergenciaResponse>> listarActivas(HttpServletRequest httpRequest) {
        String token = extraerToken(httpRequest);
        String tenantId = jwtTokenProvider.getTenantIdFromToken(token);
        return ResponseEntity.ok(emergencyService.obtenerActivas(tenantId));
    }

    /**
     * PATCH /api/emergencias/{id}/resolver
     * Resolver una emergencia. Solo GUARD y ADMIN.
     * Actualiza la DB y notifica por WebSocket.
     */
    @PatchMapping("/{id}/resolver")
    @PreAuthorize("hasRole('GUARD') or hasRole('ADMIN')")
    public ResponseEntity<EmergenciaResponse> resolverEmergencia(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {

        String token = extraerToken(httpRequest);
        String tenantId = jwtTokenProvider.getTenantIdFromToken(token);
        log.info("Resolviendo emergencia id={} por tenant={}", id, tenantId);
        return ResponseEntity.ok(emergencyService.resolverEmergencia(id, tenantId));
    }

    /**
     * PATCH /api/emergencias/{id}/falsa-alarma
     * Marcar una emergencia como falsa alarma. Solo GUARD y ADMIN.
     * Actualiza la DB y notifica por WebSocket.
     */
    @PatchMapping("/{id}/falsa-alarma")
    @PreAuthorize("hasRole('GUARD') or hasRole('ADMIN')")
    public ResponseEntity<EmergenciaResponse> marcarFalsaAlarma(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {

        String token = extraerToken(httpRequest);
        String tenantId = jwtTokenProvider.getTenantIdFromToken(token);
        log.info("Marcando falsa alarma en emergencia id={} por tenant={}", id, tenantId);
        return ResponseEntity.ok(emergencyService.marcarFalsaAlarma(id, tenantId));
    }

    // --- Método utilitario: extrae el token Bearer del header Authorization ---
    private String extraerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        throw new RuntimeException("Token no encontrado");
    }
}
