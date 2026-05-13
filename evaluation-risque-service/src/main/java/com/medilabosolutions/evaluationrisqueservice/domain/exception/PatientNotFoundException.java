package com.medilabosolutions.evaluationrisqueservice.domain.exception;

import lombok.Getter;

@Getter
public class PatientNotFoundException extends RuntimeException {

    private final Long id;

    public PatientNotFoundException(Long id) {
        super("Ce patient est introuvable : id = " + id);
        this.id = id;
    }
}
