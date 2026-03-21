package com.blindaje.core.notification.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class WebSocketPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketPublisher(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // Broadcast a todos los suscriptores del topic
    public void publish(String destination, Object payload) {
        messagingTemplate.convertAndSend(destination, payload);
    }

    // Mensaje directo a un usuario específico por su username
    public void publishToUser(String username, String destination, Object payload) {
        messagingTemplate.convertAndSendToUser(username, destination, payload);
    }
}