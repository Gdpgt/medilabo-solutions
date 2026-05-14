package com.medilabosolutions.frontendservice.domain.exception;

import lombok.Getter;

@Getter
public class NotePraticienNotFoundException extends RuntimeException {

    private final String id;

    public NotePraticienNotFoundException(String id) {
        super("Cette note praticien est introuvable : id = " + id);
        this.id = id;
    }
}
