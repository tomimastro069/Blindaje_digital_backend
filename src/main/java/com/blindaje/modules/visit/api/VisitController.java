package com.blindaje.modules.visit.api;

import com.blindaje.config.security.JwtTokenProvider;
import com.blindaje.modules.visit.domain.Visit;
import com.blindaje.modules.visit.Dto.VisitRequest;
import com.blindaje.modules.visit.service.VisitService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/visits")
public class VisitController {

    private final VisitService visitService;
    private final JwtTokenProvider jwtTokenProvider;

    public VisitController(VisitService visitService, JwtTokenProvider jwtTokenProvider) {
        this.visitService = visitService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // Residente crea una visita pre-autorizada
    @PostMapping
    public ResponseEntity<Visit> crearVisita(@RequestBody VisitRequest request,
                                              HttpServletRequest httpRequest) {
        String token = extraerToken(httpRequest);
        Long residentId = jwtTokenProvider.getUserIdFromToken(token);
        String tenantId = jwtTokenProvider.getTenantIdFromToken(token);

        Visit visit = visitService.crearVisita(request, residentId, tenantId);
        return ResponseEntity.ok(visit);
    }

    // Residente ve sus propias visitas
    @GetMapping("/mis-visitas")
    public ResponseEntity<List<Visit>> misVisitas(HttpServletRequest httpRequest) {
        String token = extraerToken(httpRequest);
        Long residentId = jwtTokenProvider.getUserIdFromToken(token);
        return ResponseEntity.ok(visitService.obtenerVisitasPorResidente(residentId));
    }

    // Guardia ve todas las visitas del tenant
    @GetMapping
    public ResponseEntity<List<Visit>> todasLasVisitas(HttpServletRequest httpRequest) {
        String token = extraerToken(httpRequest);
        String tenantId = jwtTokenProvider.getTenantIdFromToken(token);
        return ResponseEntity.ok(visitService.obtenerVisitasPorTenant(tenantId));
    }

    private String extraerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        throw new RuntimeException("Token no encontrado");
    }
}
