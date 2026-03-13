package com.blindaje.integrations.ocr.DTO;

public class OcrRequest {

    private Long cameraId;
    private String imagePath;
    private String extractedText;
    private String propertyId;

    public Long getCameraId() { return cameraId; }
    public String getImagePath() { return imagePath; }
    public String getExtractedText() { return extractedText; }
    public String getPropertyId() { return propertyId; }
}