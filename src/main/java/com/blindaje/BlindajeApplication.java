package com.blindaje;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.TimeZone;

@SpringBootApplication
public class BlindajeApplication {

    public static void main(String[] args) {
        // Establecer UTC como zona horaria por defecto ANTES de inicializar Spring
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SpringApplication.run(BlindajeApplication.class, args);
    }
}
