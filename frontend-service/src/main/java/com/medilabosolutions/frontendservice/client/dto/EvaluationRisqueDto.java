package com.medilabosolutions.frontendservice.client.dto;

import com.medilabosolutions.frontendservice.domain.model.NiveauRisque;

public record EvaluationRisqueDto(

    Long idPatient,
    NiveauRisque niveauRisque
)
{}
