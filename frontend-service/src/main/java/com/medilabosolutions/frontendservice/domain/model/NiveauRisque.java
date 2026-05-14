package com.medilabosolutions.frontendservice.domain.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum NiveauRisque {

    AUCUN("Aucun"),
    LIMITE("Limite"),
    DANGER("Danger"),
    PRECOCE("Précoce");

    private final String libelle;

    NiveauRisque(String libelle) {
        this.libelle = libelle;
    }

    @JsonValue
    public String getLibelle() {
        return libelle;
    }
}
