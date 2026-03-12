package com.blindaje.modules.provider.repository;

import com.blindaje.modules.provider.domain.ProviderEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProviderRepository extends JpaRepository<ProviderEntry, Long> {
}
