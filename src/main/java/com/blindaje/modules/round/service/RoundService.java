package com.blindaje.modules.round.service;

import com.blindaje.modules.round.repository.RoundRepository;
import org.springframework.stereotype.Service;

@Service
public class RoundService {

    private final RoundRepository roundRepository;

    public RoundService(RoundRepository roundRepository) {
        this.roundRepository = roundRepository;
    }

    // TODO: Implement round management logic
}
