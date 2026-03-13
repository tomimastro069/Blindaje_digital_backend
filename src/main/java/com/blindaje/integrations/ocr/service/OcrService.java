package com.blindaje.integrations.ocr.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.blindaje.integrations.camera.domain.Camera;
import com.blindaje.integrations.camera.repository.CameraRepository;
import com.blindaje.integrations.image.domain.Image;
import com.blindaje.integrations.image.repository.imageRepository;
import com.blindaje.integrations.ocr.domain.OcrScan;
import com.blindaje.integrations.ocr.repository.OcrScanRepository;

@Service
public class OcrService {

    private final OcrScanRepository ocrScanRepository;
    private final imageRepository imageRepository;
    private final CameraRepository cameraRepository;

    public OcrService(
            OcrScanRepository ocrScanRepository,
            imageRepository imageRepository,
            CameraRepository cameraRepository) {

        this.ocrScanRepository = ocrScanRepository;
        this.imageRepository = imageRepository;
        this.cameraRepository = cameraRepository;
    }

    @Transactional
    public OcrScan processScan(
            Long cameraId,
            MultipartFile imageFile,
            String propertyId) throws Exception {

        /*
         * VALIDAR CÁMARA
         */
        Camera camera = cameraRepository.findById(cameraId)
                .orElseThrow(() -> new RuntimeException("Camera not found"));

        /*
         * GUARDAR IMAGEN
         */
        String uploadDir = "uploads/";
        Files.createDirectories(new File(uploadDir).toPath());

        String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
        File savedImage = new File(uploadDir + fileName);

        imageFile.transferTo(savedImage);

        /*
         * CREAR REGISTRO IMAGE
         */
        Image image = new Image();
        image.setCamera(camera);
        image.setImagePath(savedImage.getAbsolutePath());
        image.setCapturedAt(LocalDateTime.now());

        imageRepository.save(image);

        /*
         * EJECUTAR TESSERACT
         */
        ProcessBuilder processBuilder = new ProcessBuilder(
                "tesseract",
                savedImage.getAbsolutePath(),
                "stdout"
        );

        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
        );

        StringBuilder extractedText = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            extractedText.append(line).append("\n");
        }

        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("Tesseract OCR failed");
        }

        /*
         * CREAR OCR SCAN
         */
        OcrScan scan = new OcrScan();
        scan.setExtractedText(extractedText.toString());
        scan.setImagePath(savedImage.getAbsolutePath());
        scan.setPropertyId(propertyId);
        scan.setScannedAt(LocalDateTime.now());

        return ocrScanRepository.save(scan);
    }

    /*
     * CUANDO EL OCR SE EJECUTA EN EDGE
     */
    @Transactional
    public OcrScan saveResult(
            String extractedText,
            String imagePath,
            String propertyId) {

        OcrScan scan = new OcrScan();

        scan.setExtractedText(extractedText);
        scan.setImagePath(imagePath);
        scan.setPropertyId(propertyId);
        scan.setScannedAt(LocalDateTime.now());

        return ocrScanRepository.save(scan);
    }
}