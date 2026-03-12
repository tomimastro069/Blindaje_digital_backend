package com.blindaje.core.turn.service;

import com.blindaje.core.turn.repository.TurnoRepository;
import org.springframework.stereotype.Service;

@Service
public class TurnoService {

    private final TurnoRepository turnRepository;

    public TurnoService(TurnoRepository turnRepository) {
        this.turnRepository = turnRepository;
    }

    // TODO: Implement turn management logic
}
