package com.medilabosolutions.evaluationrisqueservice.domain.model;

public record EvaluationRisque(

    Long idPatient,
    String nomPatient,
    NiveauRisque niveauRisque
)
{}
