package com.medilabosolutions.notepraticienservice.web.advice;

import com.medilabosolutions.notepraticienservice.domain.exception.NoteNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;


@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(NoteNotFoundException.class)
    public ResponseEntity<Void> handleNoteNotFoundException(NoteNotFoundException e) {
        log.warn("La note à l'id {} n'existe pas en base.", e.getId());
        return ResponseEntity.notFound().build();
    }


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


    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<String> handleOptimisticLockingFailureException(OptimisticLockingFailureException e) {
        log.warn("Cette note a été modifiée par un autre utilisateur entre temps : {}", e.getMessage());
        return ResponseEntity.status(409).body("Cette note a été modifiée par un autre utilisateur entre temps. Rechargez et recommencez.");
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn("Le corps de la requête est illisible ou mal formé : {}", e.getMessage());
        return ResponseEntity.badRequest().body("Le corps de la requête est invalide ou mal formé.");
    }


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.warn("Le paramètre '{}' a un type invalide : {}", e.getName(), e.getMessage());
        return ResponseEntity.badRequest().body("Le paramètre '" + e.getName() + "' n'a pas le bon format.");
    }


    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<String> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.warn("Méthode HTTP non supportée : {}", e.getMessage());
        return ResponseEntity.status(405).body("La méthode HTTP utilisée n'est pas autorisée pour cette ressource.");
    }


    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<String> handleNoResourceFoundException(NoResourceFoundException e) {
        log.warn("Ressource introuvable : {}", e.getMessage());
        return ResponseEntity.status(404).body("La ressource demandée n'existe pas.");
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception e) {
        log.error("Erreur inattendue", e);
        return ResponseEntity.internalServerError().body("Une erreur inattendue est survenue. Veuillez réessayer plus tard.");
    }

}
