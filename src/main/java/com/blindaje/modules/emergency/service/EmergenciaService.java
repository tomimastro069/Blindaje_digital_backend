package com.blindaje.modules.emergency.service;

import com.blindaje.modules.emergency.repository.EmergenciaRepository;
import org.springframework.stereotype.Service;

@Service
public class EmergenciaService {

    private final EmergenciaRepository emergencyRepository;

    public EmergenciaService(EmergenciaRepository emergencyRepository) {
        this.emergencyRepository = emergencyRepository;
    }

    // TODO: Implement emergency management logic
}
