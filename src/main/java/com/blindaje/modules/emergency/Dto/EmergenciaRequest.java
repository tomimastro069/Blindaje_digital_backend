package com.blindaje.modules.emergency.Dto;

import com.blindaje.modules.emergency.domain.EmergenciaTipo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class EmergenciaRequest {

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 200, message = "El título no puede superar los 200 caracteres")
    private String title;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
    private String description;

    // Mensaje adicional para detallar el problema con mayor precisión
    @Size(max = 1000, message = "El mensaje no puede superar los 1000 caracteres")
    private String message;

    @NotNull(message = "El tipo de emergencia es obligatorio")
    private EmergenciaTipo type;

    // --- Getters ---

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getMessage() { return message; }
    public EmergenciaTipo getType() { return type; }

    // --- Setters ---

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setMessage(String message) { this.message = message; }
    public void setType(EmergenciaTipo type) { this.type = type; }
}
