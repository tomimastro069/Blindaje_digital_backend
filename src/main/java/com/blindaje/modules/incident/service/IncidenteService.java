package com.blindaje.modules.incident.service;

import com.blindaje.modules.incident.repository.IncidenteRepository;
import org.springframework.stereotype.Service;

@Service
public class IncidenteService {

    private final IncidenteRepository incidentRepository;

    public IncidenteService(IncidenteRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    // TODO: Implement incident management logic
}
