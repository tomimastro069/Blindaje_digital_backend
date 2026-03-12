package com.blindaje.modules.provider.service;

import com.blindaje.modules.provider.repository.ProviderRepository;
import org.springframework.stereotype.Service;

@Service
public class ProviderService {

    private final ProviderRepository providerRepository;

    public ProviderService(ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    // TODO: Implement provider management logic
}
