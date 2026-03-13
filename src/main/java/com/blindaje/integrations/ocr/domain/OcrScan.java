package com.blindaje.integrations.ocr.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ocr_scans")
public class OcrScan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String extractedText;

    private String imagePath;

    private LocalDateTime scannedAt;

    private String propertyId;
    
    // Campos específicos extraídos
    private String dni;
    private String nombre;
    private String apellido;

    public OcrScan() {}

    public OcrScan(String extractedText, String imagePath, String propertyId) {
        this.extractedText = extractedText;
        this.imagePath = imagePath;
        this.propertyId = propertyId;
        this.scannedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getExtractedText() { return extractedText; }
    public String getImagePath() { return imagePath; }
    public LocalDateTime getScannedAt() { return scannedAt; }
    public String getPropertyId() { return propertyId; }
    public String getDni() { return dni; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }

    public void setId(Long id) { this.id = id; }
    public void setExtractedText(String extractedText) { this.extractedText = extractedText; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public void setScannedAt(LocalDateTime scannedAt) { this.scannedAt = scannedAt; }
    public void setPropertyId(String propertyId) { this.propertyId = propertyId; }
    public void setDni(String dni) { this.dni = dni; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setApellido(String apellido) { this.apellido = apellido; }
}