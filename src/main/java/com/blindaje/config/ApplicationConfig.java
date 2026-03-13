package com.blindaje.config;

import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

@Configuration
public class ApplicationConfig {

    public ApplicationConfig() {
        // Establecer UTC como zona horaria por defecto
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }
}
