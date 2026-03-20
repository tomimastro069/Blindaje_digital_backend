package com.blindaje.core.notification.service;

import com.blindaje.core.notification.domain.Notificacion;
import com.blindaje.core.notification.repository.NotificacionRepository;
import org.springframework.stereotype.Service;

@Service
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final WebSocketPublisher webSocketPublisher;

    public NotificacionService(NotificacionRepository notificacionRepository,
                                WebSocketPublisher webSocketPublisher) {
        this.notificacionRepository = notificacionRepository;
        this.webSocketPublisher = webSocketPublisher;
    }

    /**
     * Notificación broadcast: llega a todos los guardias del tenant
     * Usada cuando un residente crea una visita
     */
    public void notificarTenant(String tenantId, String title, String message) {
        // Guardar en DB
        Notificacion notificacion = new Notificacion(title, message, "tenant:" + tenantId, tenantId);
        notificacionRepository.save(notificacion);

        // Publicar por WebSocket a todos los guardias del tenant
        webSocketPublisher.publish("/topic/visitas/" + tenantId, new NotificacionPayload(title, message));
    }

    /**
     * Notificación directa: llega solo a un usuario específico
     * Usada cuando el guardia responde al residente
     */
    public void notificarUsuario(Long userId, String tenantId, String title, String message) {
        // Guardar en DB
        Notificacion notificacion = new Notificacion(title, message, userId.toString(), tenantId);
        notificacionRepository.save(notificacion);

        // Publicar por WebSocket solo a ese usuario
        webSocketPublisher.publish("/queue/notificaciones/" + userId, new NotificacionPayload(title, message));
    }

    // DTO interno para el payload WebSocket
    public static class NotificacionPayload {
        private String title;
        private String message;

        public NotificacionPayload(String title, String message) {
            this.title = title;
            this.message = message;
        }

        public String getTitle() { return title; }
        public String getMessage() { return message; }
    }
}