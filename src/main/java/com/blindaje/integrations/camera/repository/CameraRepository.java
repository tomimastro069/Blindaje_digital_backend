package com.blindaje.integrations.camera.repository;

import org.springframework.stereotype.Repository;
import com.blindaje.integrations.camera.domain.Camera;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface CameraRepository extends JpaRepository<Camera, Long> { 
    
}
