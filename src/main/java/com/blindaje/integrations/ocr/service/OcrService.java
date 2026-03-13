package com.blindaje.integrations.ocr.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(OcrService.class);
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

        try {
            logger.info("Starting OCR processing for cameraId={}, propertyId={}", cameraId, propertyId);
            
            /*
             * VALIDAR CÁMARA
             */
            Camera camera = cameraRepository.findById(cameraId)
                    .orElseThrow(() -> new RuntimeException("Camera not found"));
            logger.info("Camera found: {}", camera.getId());

            /*
             * GUARDAR IMAGEN
             */
            String uploadDir = "/app/uploads/";
            Files.createDirectories(new File(uploadDir).toPath());

            String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
            File savedImage = new File(uploadDir + fileName);

            imageFile.transferTo(savedImage);
            logger.info("Image saved to: {}", savedImage.getAbsolutePath());

            /*
             * CREAR REGISTRO IMAGE
             */
            Image image = new Image();
            image.setCamera(camera);
            image.setImagePath(savedImage.getAbsolutePath());
            image.setCapturedAt(LocalDateTime.now());

            imageRepository.save(image);
            logger.info("Image record saved");

            /*
             * EJECUTAR TESSERACT
             */
            StringBuilder extractedText = new StringBuilder();
            
            try {
                logger.info("Starting Tesseract execution");
                ProcessBuilder processBuilder = new ProcessBuilder(
                        "tesseract",
                        savedImage.getAbsolutePath(),
                        "stdout",
                        "--oem", "1",  // Use legacy + LSTM OCR mode
                        "--psm", "11"  // 11 = Sparse text. Find as much text as possible in no particular order. Better for ID cards.
                );

                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream())
                );

                String line;
                while ((line = reader.readLine()) != null) {
                    extractedText.append(line).append("\n");
                }

                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    extractedText = new StringBuilder("[OCR: Tesseract error - exit code " + exitCode + "]");
                }
                logger.info("Tesseract completed with exit code: {}", exitCode);
            } catch (Exception e) {
                logger.error("Tesseract execution failed", e);
                // Tesseract no instalado o no disponible - usar texto de prueba
                extractedText.append("[OCR: Tesseract not available - ").append(e.getMessage()).append("]");
            }

            // Extraer datos específicos del texto OCR
            String fullText = extractedText.toString();
            String nombre = extractField(fullText, "Nombre|Name", 2);
            String apellido = extractField(fullText, "Apelido|Surname", 2);
            String dni = extractDNI(fullText);

            /*
             * CREAR OCR SCAN
             */
            OcrScan scan = new OcrScan();
            scan.setExtractedText(extractedText.toString());
            scan.setImagePath(savedImage.getAbsolutePath());
            scan.setPropertyId(propertyId);
            scan.setScannedAt(LocalDateTime.now());
            scan.setDni(dni);
            scan.setNombre(nombre);
            scan.setApellido(apellido);

            OcrScan saved = ocrScanRepository.save(scan);
            logger.info("OCR Scan saved with id: {}, DNI: {}, Nombre: {}, Apellido: {}", 
                saved.getId(), dni, nombre, apellido);
            
            return saved;
        } catch (Exception e) {
            logger.error("Error in processScan", e);
            throw e;
        }
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

    /**
     * Extrae un campo específico del texto OCR
     * @param text Texto completo del OCR
     * @param fieldPattern Patrón de búsqueda (ej: "Nombre|Name")
     * @param linesToSkip Cuántas líneas saltar después del patrón
     */
    private String extractField(String text, String fieldPattern, int linesToSkip) {
        try {
            String[] lines = text.split("\\n");
            // FIX: Use regex to match any of the patterns (e.g., "Nombre|Name") case-insensitively.
            java.util.regex.Pattern labelPattern = java.util.regex.Pattern.compile("(?i)(" + fieldPattern + ")");

            for (int i = 0; i < lines.length; i++) {
                if (labelPattern.matcher(lines[i]).find()) {
                    logger.debug("Found field pattern '{}' at line {}: {}", fieldPattern, i, lines[i]);
                    // Buscar la siguiente línea no vacía
                    for (int j = i + 1; j < lines.length && j <= i + linesToSkip + 2; j++) {
                        String nextLine = lines[j].trim();
                        if (!nextLine.isEmpty() && 
                            !nextLine.matches("(?i).*[/|].*") && 
                            !nextLine.contains("Sexo") &&
                            !nextLine.contains("Sex")) {
                            logger.info("Extracted field value: {}", nextLine);
                            return nextLine.toUpperCase();
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Error extracting field {}: {}", fieldPattern, e.getMessage());
        }
        return null;
    }

    /**
     * Extrae el DNI del texto OCR
     * DNI argentino tiene 7-8 dígitos
     */
    private String extractDNI(String text) {
        try {
            // 1. BUSQUEDA EXACTA CON PUNTOS (XX.XXX.XXX)
            // Es el indicador más fuerte de que es un DNI real y no ruido o fechas
            java.util.regex.Pattern patternDots = java.util.regex.Pattern.compile("\\b(\\d{1,2})\\.(\\d{3})\\.(\\d{3})\\b");
            java.util.regex.Matcher matcher = patternDots.matcher(text);
            if (matcher.find()) {
                String dni = matcher.group(1) + matcher.group(2) + matcher.group(3);
                logger.info("DNI found by dots format: {}", dni);
                return dni;
            }

            // 2. Primero buscar después de patrones conocidos (Etiquetas)
            // Agregamos soporte para puntos opcionales dentro del número capturado
            java.util.regex.Pattern patternWithLabel = java.util.regex.Pattern.compile(
                "(?i)(DNI|DOC|DOCUMENTO|N°|Nro|Numero|Exemplar)[^0-9a-zA-Z]{0,10}(\\d{1,2}\\.?\\d{3}\\.?\\d{3})"
            );
            matcher = patternWithLabel.matcher(text.replaceAll("\\s+", " "));
            if (matcher.find()) {
                String dni = matcher.group(2).replace(".", "").trim();
                logger.info("DNI found with label: {}", dni);
                return dni;
            }
            
            // 3. Si no encuentra con etiqueta, buscar secuencias de 8 dígitos (Fallback)
            java.util.regex.Pattern pattern8 = java.util.regex.Pattern.compile("\\b(\\d{8})\\b");
            matcher = pattern8.matcher(text);
            
            while (matcher.find()) {
                String number = matcher.group(1);
                // FIX: El filtro anterior "^(0|19|20)" eliminaba DNIs validos de 19 y 20 millones.
                // Solo filtramos si empieza con 0.
                if (!number.startsWith("0")) {
                    logger.info("DNI found (8 digits): {}", number);
                    return number;
                }
            }
            
            // 4. Fallback a 7 dígitos
            java.util.regex.Pattern pattern7 = java.util.regex.Pattern.compile("\\b(\\d{7})\\b");
            matcher = pattern7.matcher(text);
            
            while (matcher.find()) {
                String number = matcher.group(1);
                logger.info("DNI found (7 digits): {}", number);
                return number;
            }
        } catch (Exception e) {
            logger.warn("Error extracting DNI: {}", e.getMessage());
        }
        return null;
    }
}