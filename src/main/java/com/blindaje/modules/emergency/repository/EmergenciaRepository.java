package com.blindaje.modules.emergency.repository;

import com.blindaje.modules.emergency.domain.Emergencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmergenciaRepository extends JpaRepository<Emergencia, Long> {
}
