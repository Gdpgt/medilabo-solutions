package com.medilabosolutions.evaluationrisqueservice.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record PatientDto (

        @JsonProperty("id")
        Long idPatient,

        LocalDate dateNaissance,

        String genre
)
{}
