package com.medilabosolutions.patientservice.web.advice;

import com.medilabosolutions.patientservice.domain.exception.PatientNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.ResponseEntity;
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


    @ExceptionHandler(PatientNotFoundException.class)
    public ResponseEntity<Void> handlePatientNotFoundException(PatientNotFoundException e) {
        log.warn("La patient à l'id {} n'existe pas en base.", e.getId());
        return ResponseEntity.notFound().build();
    }

    
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        String cause = e.getMostSpecificCause().getMessage();

        if (cause != null && cause.contains("uk_patient_identite")) {
            log.warn("Ce patient a déjà été enregistré : {}", e.getMessage());
            // 409 : Conflit logique métier
            return ResponseEntity.status(409).body("Ce patient a déjà été enregistré.");
        }

        log.warn("Les données renseignées pour ce patient ne respectent pas le format autorisé : {}", e.getMessage());
        return ResponseEntity.badRequest().body("Les données renseignées pour ce patient ne respectent pas le format autorisé.");
    }


    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<String> handleOptimisticLockingFailureException(OptimisticLockingFailureException e) {
        log.warn("Ce patient a été modifié par un autre utilisateur entre temps : {}", e.getMessage());
        return ResponseEntity.status(409).body("Ce patient a été modifié par un autre utilisateur entre temps. Rechargez et recommencez.");
    }









}
