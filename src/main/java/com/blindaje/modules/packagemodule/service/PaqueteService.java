package com.blindaje.modules.packagemodule.service;

import com.blindaje.modules.packagemodule.repository.PaqueteRepository;
import org.springframework.stereotype.Service;

@Service
public class PaqueteService {

    private final PaqueteRepository packageRepository;

    public PaqueteService(PaqueteRepository packageRepository) {
        this.packageRepository = packageRepository;
    }

    // TODO: Implement package management logic
}
