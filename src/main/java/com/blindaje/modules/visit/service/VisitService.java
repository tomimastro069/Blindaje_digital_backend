package com.blindaje.modules.visit.service;

import com.blindaje.core.notification.service.NotificacionService;
import com.blindaje.modules.visit.domain.Companion;
import com.blindaje.modules.visit.domain.Visit;
import com.blindaje.modules.visit.Dto.CompanionRequest;
import com.blindaje.modules.visit.Dto.VisitRequest;
import com.blindaje.modules.visit.repository.VisitRepository;
import com.blindaje.modules.visit.repository.CompanionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VisitService {

    private final VisitRepository visitRepository;
    private final NotificacionService notificacionService;
    private final CompanionRepository companionRepository;

    public VisitService(VisitRepository visitRepository,
                        NotificacionService notificacionService,
                        CompanionRepository companionRepository) {
        this.visitRepository = visitRepository;
        this.notificacionService = notificacionService;
        this.companionRepository = companionRepository;

    }

    public Visit crearVisita(VisitRequest request, Long residentId, String tenantId) {
        Visit visit = new Visit(
                request.getVisitorName(),
                request.getVisitorDocument(),
                request.getVehiclePlate(),
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
    public Visit agregarAcompanantes(Long visitId, CompanionRequest request) {
    Visit visit = visitRepository.findById(visitId)
            .orElseThrow(() -> new RuntimeException("Visita no encontrada: " + visitId));

    List<Companion> companions = request.getCompanions().stream()
            .map(c -> new Companion(c.getName(), c.getDocument(), visit))
            .toList();

    companionRepository.saveAll(companions);
    visit.getCompanions().addAll(companions);
    return visit;
}

    public List<Visit> obtenerVisitasPorResidente(Long residentId) {
        return visitRepository.findByResidentId(residentId);
    }

    public List<Visit> obtenerVisitasPorTenant(String tenantId) {
        return visitRepository.findByTenantId(tenantId);
    }

    public Visit registrarEntrada(Long visitId) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new RuntimeException("Visita no encontrada: " + visitId));

        visit.setEntryTime(java.time.LocalDateTime.now());
        visit.setStatus(com.blindaje.modules.visit.domain.VisitStatus.IN_PROGRESS);
        
        return visitRepository.save(visit);
    }

    public Visit registrarSalida(Long visitId) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new RuntimeException("Visita no encontrada: " + visitId));

        if (visit.getEntryTime() == null) {
            throw new RuntimeException("No se puede registrar la salida de una visita sin entrada");
        }

        visit.setExitTime(java.time.LocalDateTime.now());
        visit.setStatus(com.blindaje.modules.visit.domain.VisitStatus.COMPLETED);

        return visitRepository.save(visit);
    }
}