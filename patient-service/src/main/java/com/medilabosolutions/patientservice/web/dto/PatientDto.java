package com.medilabosolutions.patientservice.web.dto;

import jakarta.validation.constraints.*;

import com.medilabosolutions.patientservice.domain.model.Genre;

import java.time.LocalDate;


public record PatientDto(

        Long id,

        @NotBlank(message = "Le nom est obligatoire.")
        @Size(max = 50, message = "Le nom ne doit pas dépasser 50 caractères.")
        @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\-' ]+$", message = "Le nom contient des caractères non autorisés.")
        String nom,

        @NotBlank(message = "Le prénom est obligatoire.")
        @Size(max = 50, message = "Le prénom ne doit pas dépasser 50 caractères.")
        @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\-' ]+$", message = "Le prénom contient des caractères non autorisés.")
        String prenom,

        @Past(message = "La date de naissance ne peut pas être dans le futur.")
        @NotNull(message = "La date de naissance est obligatoire.")
        LocalDate dateNaissance,

        @NotNull
        Genre genre,

        @Size(max = 100, message = "La taille de l'adresse ne doit pas dépasser 100 caractères.")
        @Pattern(regexp = "^[a-zA-ZÀ-ÿŒœ0-9'’ ,./():+°-]*$", message = "L'adresse contient des caractères spéciaux non autorisés.")
        String adresse,

        @Pattern(regexp = "^\\d{3}-\\d{3}-\\d{4}$", message = "Le téléphone doit être au format 000-000-0000.")
        String telephone
) {}
