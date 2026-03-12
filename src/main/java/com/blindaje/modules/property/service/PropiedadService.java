package com.blindaje.modules.property.service;

import com.blindaje.modules.property.repository.PropiedadRepository;
import org.springframework.stereotype.Service;

@Service
public class PropiedadService {

    private final PropiedadRepository propertyRepository;

    public PropiedadService(PropiedadRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    // TODO: Implement property CRUD operations
}
