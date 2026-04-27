package com.medilabosolutions.notepraticienservice.domain.model;

import jakarta.validation.constraints.*;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;


@Document(collection = "notes_praticien")
@Getter
@Setter
public class NotePraticien {

    @Id
    @Setter(AccessLevel.NONE)
    private String id;

    @NotNull(message = "L'id du patient est obligatoire.")
    private Long idPatient;

    @NotBlank(message = "Le nom du patient est obligatoire.")
    @Size(max = 50, message = "Le nom du patient ne peut pas dépasser 50 caractères.")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\-' ]+$", message = "Le nom contient des caractères non autorisés.")
    private String nomPatient;

    @NotBlank(message = "La note est obligatoire.")
    // Rejects only ASCII control characters
    // Ref: OWASP Input Validation Cheat Sheet — do not use an allowlist on free-text fields
    @Pattern(regexp = "(?s)^[^\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F]+$", message = "La note contient des caractères non autorisés.")
    private String note;

    @CreatedDate
    @Setter(AccessLevel.NONE)
    private Instant dateCreation;

}
