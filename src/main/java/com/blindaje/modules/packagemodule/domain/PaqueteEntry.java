package com.blindaje.modules.packagemodule.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "package_entries")
public class PaqueteEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String recipientName;

    private String description;

    private LocalDateTime receivedAt;

    private LocalDateTime deliveredAt;

    private String tenantId;

    // TODO: Add getters, setters, and constructors
}
