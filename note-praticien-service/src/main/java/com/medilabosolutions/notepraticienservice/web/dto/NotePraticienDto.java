package com.medilabosolutions.notepraticienservice.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record NotePraticienDto(

        String id,

        @NotNull(message = "L'id du patient est obligatoire.")
        Long idPatient,

        @NotBlank(message = "Le nom du patient est obligatoire.")
        @Size(max = 50, message = "Le nom du patient ne doit pas dépasser 50 caractères.")
        @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\-' ]+$", message = "Le nom du patient contient des caractères non autorisés.")
        String nomPatient,

        @NotBlank(message = "La note est obligatoire.")
        // Rejects only ASCII control characters
        // Ref: OWASP Input Validation Cheat Sheet — do not use an allowlist on free-text fields
        @Pattern(regexp = "(?s)^[^\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F]+$", message = "La note contient des caractères non autorisés.")
        String note
) {}
