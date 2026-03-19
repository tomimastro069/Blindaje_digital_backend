package com.blindaje.modules.visit.repository;

import com.blindaje.modules.visit.domain.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {
    List<Visit> findByResidentId(Long residentId);
    List<Visit> findByTenantId(String tenantId);
}