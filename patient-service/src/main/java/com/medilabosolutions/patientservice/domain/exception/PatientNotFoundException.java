package com.medilabosolutions.patientservice.domain.exception;

import lombok.Getter;

@Getter
public class PatientNotFoundException extends RuntimeException {

    private final Long id;

    public PatientNotFoundException(Long id) {
        super("Patient introuvable : id = " + id);
        this.id = id;
    }

}
