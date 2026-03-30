package com.medilabosolutions.patientservice.web.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;


@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<String> handleDataAccessException(DataAccessException e) {
        log.error("L'accès à la base de donnée a échoué", e);
        return ResponseEntity.internalServerError().body("L'accès à la base de donnée a échoué. Veuillez réessayer plus tard.");
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + " : " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.warn("Le formulaire contient des données non valides : {}", message);
        return ResponseEntity.badRequest().body("Le formulaire contient des données non valides : " + message);
    }

}
