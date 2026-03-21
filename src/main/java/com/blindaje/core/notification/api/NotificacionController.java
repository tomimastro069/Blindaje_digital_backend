package com.blindaje.core.notification.api;

import com.blindaje.config.security.JwtTokenProvider;
import com.blindaje.core.notification.domain.Notificacion;
import com.blindaje.core.notification.service.NotificacionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    private final NotificacionService notificacionService;
    private final JwtTokenProvider jwtTokenProvider;

    public NotificacionController(NotificacionService notificacionService,
                                   JwtTokenProvider jwtTokenProvider) {
        this.notificacionService = notificacionService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/pendientes")
    public ResponseEntity<List<Notificacion>> pendientes(HttpServletRequest request) {
        String token = extraerToken(request);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        return ResponseEntity.ok(notificacionService.obtenerPendientes(userId));
    }

    @PatchMapping("/{id}/leer")
    public ResponseEntity<Void> marcarComoLeida(@PathVariable Long id,
                                                 HttpServletRequest request) {
        String token = extraerToken(request);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        notificacionService.marcarComoLeida(id, userId);
        return ResponseEntity.ok().build();
    }

    private String extraerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        throw new RuntimeException("Token no encontrado");
    }
}