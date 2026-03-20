package com.blindaje.modules.visit.service;

import com.blindaje.core.notification.service.NotificacionService;
import com.blindaje.modules.visit.domain.Visit;
import com.blindaje.modules.visit.Dto.VisitRequest;
import com.blindaje.modules.visit.repository.VisitRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VisitService {

    private final VisitRepository visitRepository;
    private final NotificacionService notificacionService;

    public VisitService(VisitRepository visitRepository,
                        NotificacionService notificacionService) {
        this.visitRepository = visitRepository;
        this.notificacionService = notificacionService;
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
        Visit saved = visitRepository.save(visit);

        // Notificar a todos los guardias del tenant en tiempo real
        notificacionService.notificarTenant(
                tenantId,
                "Nueva visita registrada",
                "El visitante " + request.getVisitorName() +
                " (DNI: " + request.getVisitorDocument() + ")" +
                " tiene una visita programada para " + request.getScheduledAt()
        );

        return saved;
    }

    public List<Visit> obtenerVisitasPorResidente(Long residentId) {
        return visitRepository.findByResidentId(residentId);
    }

    public List<Visit> obtenerVisitasPorTenant(String tenantId) {
        return visitRepository.findByTenantId(tenantId);
    }
}