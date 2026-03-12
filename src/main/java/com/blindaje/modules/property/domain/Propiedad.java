package com.blindaje.modules.property.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "properties")
public class Propiedad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String address;

    private String tenantId;

    // TODO: Add getters, setters, and constructors
}
