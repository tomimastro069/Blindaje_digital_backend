package com.blindaje.modules.round.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public class RoundTemplateRequest {

    @NotBlank(message = "El nombre de la ronda no puede estar vacío")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String name;

    @Size(max = 255, message = "La descripción no puede superar 255 caracteres")
    private String description;

    @NotEmpty(message = "La ronda debe tener al menos un checkpoint")
    @Valid
    private List<CheckpointRequest> checkpoints;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<CheckpointRequest> getCheckpoints() {
        return checkpoints;
    }
}
