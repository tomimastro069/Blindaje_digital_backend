package com.blindaje.integrations.ocr.repository;

import com.blindaje.integrations.ocr.domain.OcrScan;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OcrScanRepository extends JpaRepository<OcrScan, Long> {
     Optional<OcrScan> findByDni(String dni);
}