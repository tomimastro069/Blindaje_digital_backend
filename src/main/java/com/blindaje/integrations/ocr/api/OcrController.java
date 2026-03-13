package com.blindaje.integrations.ocr.api;
import com.blindaje.integrations.ocr.DTO.OcrRequest;
import com.blindaje.integrations.ocr.domain.OcrScan;
import com.blindaje.integrations.ocr.service.OcrService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
@RestController
@RequestMapping("/api/ocr")
public class OcrController {

    private final OcrService ocrService;

    public OcrController(OcrService ocrService) {
        this.ocrService = ocrService;
    }

    // OCR ejecutado en el backend
    @PostMapping("/scan-image")
    public OcrScan scanImage(
            @RequestParam Long cameraId,
            @RequestParam("image") MultipartFile image,
            @RequestParam String propertyId) throws Exception {

        return ocrService.processScan(cameraId, image, propertyId);
    }

    // OCR ejecutado en el Edge Node
    @PostMapping("/scan-result")
    public OcrScan receiveResult(@RequestBody OcrRequest request) {

        return ocrService.saveResult(
                request.getExtractedText(),
                request.getImagePath(),
                request.getPropertyId()
        );
    }
}