package com.medilabosolutions.notepraticienservice.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class NoteNotFoundException extends RuntimeException {

    private final String id;
}
