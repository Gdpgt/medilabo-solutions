package com.medilabosolutions.notepraticienservice.domain.exception;

import lombok.Getter;

@Getter
public class NoteNotFoundException extends RuntimeException {

    private final String id;

    public NoteNotFoundException(String id) {
        super("Note introuvable : id = " + id);
        this.id = id;
    }
}
