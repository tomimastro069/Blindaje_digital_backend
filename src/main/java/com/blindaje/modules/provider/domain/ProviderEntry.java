package com.blindaje.modules.provider.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "provider_entries")
public class ProviderEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String companyName;

    private String providerName;

    private String document;

    private String tenantId;

    // TODO: Add getters, setters, and constructors
}
