package com.blindaje.core.notification.service;

import com.blindaje.core.notification.domain.Notificacion;
import com.blindaje.core.notification.repository.NotificacionRepository;
import com.blindaje.modules.user.domain.User;
import com.blindaje.modules.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final WebSocketPublisher webSocketPublisher;
    private final UserRepository userRepository;

    public NotificacionService(NotificacionRepository notificacionRepository,
                                WebSocketPublisher webSocketPublisher,
                                UserRepository userRepository) {
        this.notificacionRepository = notificacionRepository;
        this.webSocketPublisher = webSocketPublisher;
        this.userRepository = userRepository;
    }

    public void notificarTenant(String tenantId, String title, String message) {
        Notificacion notificacion = new Notificacion(title, message, "tenant:" + tenantId, tenantId);
        notificacionRepository.save(notificacion);
        webSocketPublisher.publish("/topic/visitas/" + tenantId, new NotificacionPayload(title, message));
    }

    public void notificarUsuario(Long userId, String tenantId, String title, String message) {
        // Buscar el username del usuario por su id
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + userId));

        Notificacion notificacion = new Notificacion(title, message, userId.toString(), tenantId);
        notificacionRepository.save(notificacion);

        // Enviar al usuario por su username
        webSocketPublisher.publishToUser(user.getUsername(), "/queue/notificaciones", new NotificacionPayload(title, message));
    }

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