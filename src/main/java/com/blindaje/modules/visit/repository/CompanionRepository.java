package com.blindaje.modules.visit.repository;

import com.blindaje.modules.visit.domain.Companion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanionRepository extends JpaRepository<Companion, Long> {
    List<Companion> findByVisitId(Long visitId);
}