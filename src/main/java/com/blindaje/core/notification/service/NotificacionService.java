package com.blindaje.core.notification.service;

import com.blindaje.core.notification.repository.NotificacionRepository;
import org.springframework.stereotype.Service;

@Service
public class NotificacionService {

    private final NotificacionRepository notificationRepository;

    public NotificacionService(NotificacionRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    // TODO: Implement notification creation, marking as read, querying
}
