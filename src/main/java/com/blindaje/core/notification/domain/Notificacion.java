package com.blindaje.core.notification.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String message;

    private String recipientId;

    private boolean read;

    private LocalDateTime createdAt;

    private String tenantId;

    // TODO: Add getters, setters, and constructors
}
