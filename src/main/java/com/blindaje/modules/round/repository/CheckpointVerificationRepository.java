package com.blindaje.modules.round.repository;

import com.blindaje.modules.round.domain.CheckpointVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CheckpointVerificationRepository extends JpaRepository<CheckpointVerification, Long> {

    List<CheckpointVerification> findByExecutionId(Long executionId);
}
