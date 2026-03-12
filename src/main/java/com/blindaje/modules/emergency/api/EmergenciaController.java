package com.blindaje.modules.emergency.api;

import com.blindaje.modules.emergency.service.EmergenciaService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/emergencies")
public class EmergenciaController {

    private final EmergenciaService emergencyService;

    public EmergenciaController(EmergenciaService emergencyService) {
        this.emergencyService = emergencyService;
    }

    // TODO: Implement emergency endpoints
}
