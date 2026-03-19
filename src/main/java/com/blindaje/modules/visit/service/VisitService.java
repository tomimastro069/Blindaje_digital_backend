package com.blindaje.modules.visit.service;

import com.blindaje.modules.visit.domain.Visit;
import com.blindaje.modules.visit.Dto.VisitRequest;
import com.blindaje.modules.visit.repository.VisitRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VisitService {

    private final VisitRepository visitRepository;

    public VisitService(VisitRepository visitRepository) {
        this.visitRepository = visitRepository;
    }

    public Visit crearVisita(VisitRequest request, Long residentId, String tenantId) {
        Visit visit = new Visit(
                request.getVisitorName(),
                request.getVisitorDocument(),
                request.getVehiclePlate(),
                request.getCompanions(),
                request.getScheduledAt(),
                tenantId,
                residentId
        );
        return visitRepository.save(visit);
    }

    public List<Visit> obtenerVisitasPorResidente(Long residentId) {
        return visitRepository.findByResidentId(residentId);
    }

    public List<Visit> obtenerVisitasPorTenant(String tenantId) {
        return visitRepository.findByTenantId(tenantId);
    }
}