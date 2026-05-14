package com.medilabosolutions.evaluationrisqueservice.web.advice;

import com.medilabosolutions.evaluationrisqueservice.domain.exception.PatientNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(PatientNotFoundException.class)
    public ResponseEntity<Void> handlePatientNotFoundException(PatientNotFoundException e) {
        log.warn(e.getMessage());
        // 404
        return ResponseEntity.notFound().build();
    }


    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<String> handleHttpServerErrorException(HttpServerErrorException e) {
        log.error("Une erreur du serveur est survenue", e);
        // Bad Gateway
        return ResponseEntity.status(502).body("Une erreur du serveur est survenue. Veuillez réessayer plus tard.");
    }


    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<String> handleResourceAccessException(ResourceAccessException e) {
        log.error("Le service est indisponible", e);
        // Service Unavailable
        return ResponseEntity.status(503).body("Une erreur du serveur est survenue. Veuillez réessayer plus tard.");
    }


    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<String> handleHttpClientErrorException(HttpClientErrorException e) {
        log.error("Une erreur est survenue", e);
        // Bad Gateway
        return ResponseEntity.status(502).body("Une erreur du serveur est survenue. Veuillez réessayer plus tard.");
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn("Le corps de la requête est illisible ou mal formé : {}", e.getMessage());
        // 400
        return ResponseEntity.badRequest().body("Le corps de la requête est invalide ou mal formé.");
    }


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.warn("Le paramètre '{}' a un type invalide : {}", e.getName(), e.getMessage());
        // 400
        return ResponseEntity.badRequest().body("Le paramètre '" + e.getName() + "' n'a pas le bon format.");
    }


    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<String> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.warn("Méthode HTTP non supportée : {}", e.getMessage());
        // Method Not Allowed
        return ResponseEntity.status(405).body("La méthode HTTP utilisée n'est pas autorisée pour cette ressource.");
    }


    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<String> handleNoResourceFoundException(NoResourceFoundException e) {
        log.warn("Ressource introuvable : {}", e.getMessage());
        // Not Found
        return ResponseEntity.status(404).body("La ressource demandée n'existe pas.");
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception e) {
        log.error("Erreur inattendue", e);
        // 500
        return ResponseEntity.internalServerError().body("Une erreur inattendue est survenue. Veuillez réessayer plus tard.");
    }

}
