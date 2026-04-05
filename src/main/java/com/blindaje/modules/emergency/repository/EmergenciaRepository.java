package com.blindaje.modules.emergency.repository;

import com.blindaje.modules.emergency.domain.Emergencia;
import com.blindaje.modules.emergency.domain.EmergenciaStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmergenciaRepository extends JpaRepository<Emergencia, Long> {

    // Todas las emergencias del tenant (filtro por tenantId obligatorio según specs)
    List<Emergencia> findByTenantIdOrderByTriggeredAtDesc(String tenantId);

    // Solo las activas (TRIGGERED o IN_PROGRESS) del tenant
    List<Emergencia> findByTenantIdAndStatusInOrderByTriggeredAtDesc(String tenantId, List<EmergenciaStatus> statuses);
}
