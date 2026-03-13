package com.blindaje.integrations.camera.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import java.time.LocalDateTime;


@Entity
@Table(name = "cameras")
public class Camera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String location;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "property_id")
    private Integer propertyId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
