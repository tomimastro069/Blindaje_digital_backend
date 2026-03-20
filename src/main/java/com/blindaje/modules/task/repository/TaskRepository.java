package com.blindaje.modules.task.repository;

import com.blindaje.modules.task.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByAssignedToGuardId(Long guardId);
    List<Task> findByCreatedByGuardId(Long guardId);
    List<Task> findByTenantId(String tenantId);
}