package com.medilabosolutions.frontendservice.client.dto;

import com.medilabosolutions.frontendservice.domain.model.Genre;

import java.time.LocalDate;


public record PatientDto(

        Long id,

        String nom,

        String prenom,

        LocalDate dateNaissance,

        Genre genre,

        String adresse,

        String telephone
) {}
