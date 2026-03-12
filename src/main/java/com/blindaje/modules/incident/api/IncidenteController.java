package com.blindaje.modules.incident.api;

import com.blindaje.modules.incident.service.IncidenteService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/incidents")
public class IncidenteController {

    private final IncidenteService incidentService;

    public IncidenteController(IncidenteService incidentService) {
        this.incidentService = incidentService;
    }

    // TODO: Implement incident endpoints
}
