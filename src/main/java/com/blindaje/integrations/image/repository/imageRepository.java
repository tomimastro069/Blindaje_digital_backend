package com.blindaje.integrations.image.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.blindaje.integrations.image.domain.Image;

@Repository

public interface imageRepository extends JpaRepository<Image, Long> { 
    
}

