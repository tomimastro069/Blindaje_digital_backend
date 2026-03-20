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

    public Notificacion() {}

    public Notificacion(String title, String message, String recipientId, String tenantId) {
        this.title = title;
        this.message = message;
        this.recipientId = recipientId;
        this.tenantId = tenantId;
        this.read = false;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getRecipientId() { return recipientId; }
    public boolean isRead() { return read; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getTenantId() { return tenantId; }

    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setMessage(String message) { this.message = message; }
    public void setRecipientId(String recipientId) { this.recipientId = recipientId; }
    public void setRead(boolean read) { this.read = read; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
}