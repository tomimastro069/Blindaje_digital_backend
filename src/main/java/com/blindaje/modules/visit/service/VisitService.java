package com.blindaje.modules.visit.service;

import com.blindaje.modules.visit.repository.VisitRepository;
import org.springframework.stereotype.Service;

@Service
public class VisitService {

    private final VisitRepository visitRepository;

    public VisitService(VisitRepository visitRepository) {
        this.visitRepository = visitRepository;
    }

    // TODO: Implement visit management logic
}
