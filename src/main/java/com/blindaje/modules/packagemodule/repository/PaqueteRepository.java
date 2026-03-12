package com.blindaje.modules.packagemodule.repository;

import com.blindaje.modules.packagemodule.domain.PaqueteEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaqueteRepository extends JpaRepository<PaqueteEntry, Long> {
}
