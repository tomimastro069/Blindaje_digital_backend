package com.blindaje.modules.round.repository;

import com.blindaje.modules.round.domain.RoundExecution;
import com.blindaje.modules.round.dto.RoundExecutionStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoundExecutionRepository extends JpaRepository<RoundExecution, Long> {

    List<RoundExecution> findByTenantId(String tenantId);

    List<RoundExecution> findByUserId(Long userId);

    Optional<RoundExecution> findByUserIdAndStatus(Long userId, RoundExecutionStatus status);
}
