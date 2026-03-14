package com.blindaje.integrations.ocr.service;

import com.blindaje.integrations.ocr.domain.OcrScan;
import com.blindaje.integrations.ocr.repository.OcrScanRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.Optional;
import java.util.regex.*;

@Service
public class OcrService {

    private final OcrScanRepository ocrScanRepository;

    public OcrService(OcrScanRepository ocrScanRepository) {
        this.ocrScanRepository = ocrScanRepository;
    }

    public OcrScan procesarImagen(MultipartFile imagen, String propertyId) throws IOException, InterruptedException {

        // 1. Guardar imagen temporalmente
        Path tempDir = Files.createTempDirectory("ocr_");
        Path imagePath = tempDir.resolve("dni_scan.png");
        imagen.transferTo(imagePath.toFile());

        // 2. Invocar Tesseract
        //    --psm 1 → detecta orientación automáticamente + segmenta
        //    -l spa+eng → el DNI argentino mezcla ambos idiomas
        Path outputBase = tempDir.resolve("output");
        ProcessBuilder pb = new ProcessBuilder(
            "tesseract",
            imagePath.toString(),
            outputBase.toString(),
            "-l", "spa+eng",
            "--psm", "1"
        );
        pb.redirectErrorStream(true);
        Process process = pb.start();

        String tesseractLog;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            tesseractLog = reader.lines().reduce("", (a, b) -> a + "\n" + b);
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Tesseract falló (exit " + exitCode + "): " + tesseractLog);
        }

        // 3. Leer texto generado
        Path outputTxt = Path.of(outputBase + ".txt");
        String textoExtraido = Files.readString(outputTxt).trim();

        // 4. Parsear campos (intenta dorso primero, luego frente)
        String dni      = extraerDni(textoExtraido);
        String apellido = extraerApellido(textoExtraido);
        String nombre   = extraerNombre(textoExtraido);

        // 5. Construir y guardar entidad
        OcrScan scan = new OcrScan(textoExtraido, imagePath.toString(), propertyId);
        scan.setDni(dni);
        scan.setApellido(apellido);
        scan.setNombre(nombre);

        // 6. Limpiar temporales
        Files.deleteIfExists(outputTxt);
        Files.deleteIfExists(imagePath);
        Files.deleteIfExists(tempDir);

        return ocrScanRepository.save(scan);
    }

    public Optional<OcrScan> buscarPorPropertyId(String propertyId) {
        return ocrScanRepository.findByPropertyId(propertyId);
    }

    // ─── Helpers de parseo ───────────────────────────────────────────────────

    /**
     * Intenta extraer DNI de la MRZ (dorso) primero,
     * si no encuentra, busca formato con puntos (frente)
     */
    private String extraerDni(String texto) {
        // Dorso: MRZ línea 1 → IDARG47078983
        Pattern mrzPattern = Pattern.compile("IDARG(\\d{7,8})<");
        Matcher mrzMatcher = mrzPattern.matcher(texto);
        if (mrzMatcher.find()) return mrzMatcher.group(1);

        // Frente: 47.078.983 o 47078983
        Pattern frentePattern = Pattern.compile("\\b(\\d{1,2}\\.\\d{3}\\.\\d{3}|\\d{7,8})\\b");
        Matcher frenteMatcher = frentePattern.matcher(texto);
        return frenteMatcher.find() ? frenteMatcher.group().replaceAll("\\.", "") : null;
    }

    /**
     * Intenta extraer apellido de la MRZ (dorso) primero,
     * si no encuentra, busca etiqueta del frente
     */
        private String extraerApellido(String texto) {
        // Dorso: busca APELLIDO<< en cualquier parte de la línea, tolerando caracteres raros antes
        Pattern mrzPattern = Pattern.compile("([A-Z]{4,})<<[A-Z0-9 <]+");
        Matcher mrzMatcher = mrzPattern.matcher(texto);
        if (mrzMatcher.find()) return mrzMatcher.group(1);

        // Frente
        Pattern frentePattern = Pattern.compile(
            "(?i)Apellido\\s*/\\s*Surname[\\s\\r\\n]+([A-ZÁÉÍÓÚÑ][A-ZÁÉÍÓÚÑ\\s]+)",
            Pattern.UNICODE_CHARACTER_CLASS
        );
        Matcher frenteMatcher = frentePattern.matcher(texto);
        return frenteMatcher.find() ? frenteMatcher.group(1).trim() : null;
    }

    private String extraerNombre(String texto) {
        // Dorso: captura entre << y el siguiente 
        Pattern mrzPattern = Pattern.compile("[A-Z]{4,}<<([A-Z0-9 <]+?)<<");
        Matcher mrzMatcher = mrzPattern.matcher(texto);
        if (mrzMatcher.find()) {
            return mrzMatcher.group(1)
                .replace("0", "O")   
                .replace(" O", "O")     // Tesseract confunde O con 0
                .replaceAll("\\s+", " ")  // elimina espacios dobles
                .replace("< ", " ")       // limpia < con espacio
                .replace("<", " ")        // reemplaza < restantes por espacio
                .trim();
        }

        // Frente
        Pattern frentePattern = Pattern.compile(
            "(?i)Nombre\\s*/\\s*Name[\\s\\r\\n]+([A-ZÁÉÍÓÚÑ][A-ZÁÉÍÓÚÑ\\s]+)",
            Pattern.UNICODE_CHARACTER_CLASS
        );
        Matcher frenteMatcher = frentePattern.matcher(texto);
        return frenteMatcher.find() ? frenteMatcher.group(1).trim() : null;
    }
}