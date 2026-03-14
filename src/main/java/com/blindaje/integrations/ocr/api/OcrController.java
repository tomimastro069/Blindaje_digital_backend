package com.blindaje.integrations.ocr.api;

import com.blindaje.integrations.ocr.domain.OcrScan;
import com.blindaje.integrations.ocr.service.OcrService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/ocr")
public class OcrController {

    private final OcrService ocrService;

    public OcrController(OcrService ocrService) {
        this.ocrService = ocrService;
    }

    /**
     * POST /api/ocr/scan
     * Recibe la imagen del DNI y el propertyId como parámetros del form
     */
    @PostMapping("/scan")
    public ResponseEntity<?> scan(
            @RequestParam("imagen") MultipartFile imagen,
            @RequestParam("propertyId") String propertyId) {

        if (imagen.isEmpty()) {
            return ResponseEntity.badRequest().body("La imagen no puede estar vacía");
        }

        try {
            OcrScan resultado = ocrService.procesarImagen(imagen, propertyId);
            return ResponseEntity.ok(resultado);
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError()
                    .body("Error de Tesseract: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * GET /api/ocr/scan/{propertyId}
     * Consulta el último scan de una propiedad
     */
    @GetMapping("/scan/{propertyId}")
    public ResponseEntity<?> getScan(@PathVariable String propertyId) {
        return ocrService.buscarPorPropertyId(propertyId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}