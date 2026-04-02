package com.blindaje.modules.round.repository;

import com.blindaje.modules.round.domain.Round;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoundRepository extends JpaRepository<Round, Long> {

    List<Round> findByTenantId(String tenantId);
}
