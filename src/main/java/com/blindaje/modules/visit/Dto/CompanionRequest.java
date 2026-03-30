package com.blindaje.modules.visit.Dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import java.util.List;

public class CompanionRequest {

    @NotEmpty(message = "La lista de acompañantes no puede estar vacía")
    @Valid
    private List<CompanionItem> companions;

    public List<CompanionItem> getCompanions() { return companions; }

    public static class CompanionItem {

        @NotBlank(message = "El nombre del acompañante no puede estar vacío")
        private String name;

        @NotBlank(message = "El documento del acompañante no puede estar vacío")
        @Pattern(regexp = "\\d{7,8}", message = "El documento debe tener 7 u 8 dígitos")
        private String document;

        public String getName() { return name; }
        public String getDocument() { return document; }
    }
}