package com.medilabosolutions.patientservice.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class PatientNotFoundException extends RuntimeException {

    private final Long id;

}
