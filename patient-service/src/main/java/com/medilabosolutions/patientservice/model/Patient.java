package com.medilabosolutions.patientservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;


@Entity
@Table(name = "patient")
@Getter @Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @NotBlank(message = "Le nom est obligatoire.")
    @Size(max=50, message = "Le nom ne doit pas dépasser 50 caractères.")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\-' ]+$", message = "Le nom contient des caractères non autorisés.")
    @ToString.Include
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire.")
    @Size(max=50, message = "Le prénom ne doit pas dépasser 50 caractères.")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\-' ]+$", message = "Le prénom contient des caractères non autorisés.")
    @ToString.Include
    private String prenom;

    @Past(message = "La date de naissance ne peut pas être dans le futur.")
    @NotNull(message = "La date de naissance est obligatoire.")
    private LocalDate dateNaissance;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Genre genre;

    @Size(max=100, message = "La taille de l'adresse ne doit pas dépasser 100 caractères.")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿŒœ0-9'’ ,./():+°-]*$", message = "L'adresse contient des caractères spéciaux non autorisés.")
    private String adresse;

    @Pattern(regexp = "^\\d{3}-\\d{3}-\\d{4}$", message = "Le téléphone doit être au format 000-000-0000.")
    private String telephone;

}