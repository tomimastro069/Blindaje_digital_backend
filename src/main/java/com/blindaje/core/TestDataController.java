package com.blindaje.core;

import com.blindaje.integrations.camera.domain.Camera;
import com.blindaje.integrations.camera.repository.CameraRepository;
import com.blindaje.modules.property.domain.Propiedad;
import com.blindaje.modules.property.repository.PropiedadRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test-data")
public class TestDataController {

    private final PropiedadRepository propiedadRepository;
    private final CameraRepository cameraRepository;

    public TestDataController(PropiedadRepository propiedadRepository, CameraRepository cameraRepository) {
        this.propiedadRepository = propiedadRepository;
        this.cameraRepository = cameraRepository;
    }

    @GetMapping("/init")
    public String initTestData() {
        try {
            // Crear propiedad
            Propiedad propiedad = new Propiedad();
            propiedad.setName("Propiedad 1");
            propiedad.setAddress("Calle 1 #123");
            propiedad.setTenantId("tenant1");
            Propiedad savedPropiedad = propiedadRepository.save(propiedad);

            // Crear cámara
            Camera camera = new Camera();
            camera.setName("Cámara 1");
            camera.setIpAddress("192.168.1.100");
            camera.setLocation("Entrada");
            cameraRepository.save(camera);

            return "Test data initialized successfully! Property ID: " + savedPropiedad.getId();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
