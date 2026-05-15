package com.medilabosolutions.frontendservice.web.advice;

import com.medilabosolutions.frontendservice.domain.exception.NotePraticienNotFoundException;
import com.medilabosolutions.frontendservice.domain.exception.PatientNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;


@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(PatientNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handlePatientNotFoundException(PatientNotFoundException e, Model model) {
        log.warn("Le patient à l'id {} n'existe pas.", e.getId());
        model.addAttribute("message", "Patient introuvable.");
        return "error/404";
    }


    @ExceptionHandler(NotePraticienNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotePraticienNotFoundException(NotePraticienNotFoundException e, Model model) {
        log.warn("La note à l'id {} n'existe pas.", e.getId());
        model.addAttribute("message", "Note introuvable.");
        return "error/404";
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleMethodArgumentNotValidException(MethodArgumentNotValidException e, Model model) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + " : " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.warn("Le formulaire contient des données non valides : {}", message);
        model.addAttribute("message", "Le formulaire contient des données non valides : " + message);
        return "error/400";
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleHttpMessageNotReadableException(HttpMessageNotReadableException e, Model model) {
        log.warn("Le corps de la requête est illisible ou mal formé : {}", e.getMessage());
        model.addAttribute("message", "La requête est invalide ou mal formée.");
        return "error/400";
    }


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, Model model) {
        log.warn("Le paramètre '{}' a un type invalide : {}", e.getName(), e.getMessage());
        model.addAttribute("message", "Le paramètre '" + e.getName() + "' n'a pas le bon format.");
        return "error/400";
    }


    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public String handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e, Model model) {
        log.warn("Méthode HTTP non supportée : {}", e.getMessage());
        model.addAttribute("message", "Méthode HTTP non autorisée.");
        return "error/405";
    }


    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNoResourceFoundException(NoResourceFoundException e, Model model) {
        log.warn("Ressource introuvable : {}", e.getMessage());
        model.addAttribute("message", "La page demandée n'existe pas.");
        return "error/404";
    }


    @ExceptionHandler(HttpClientErrorException.class)
    public String handleHttpClientErrorException(HttpClientErrorException e, Model model, jakarta.servlet.http.HttpServletResponse response) {
        HttpStatus status = HttpStatus.valueOf(e.getStatusCode().value());
        String body = e.getResponseBodyAsString();

        log.warn("Erreur client renvoyée par un backend : {} - {}", status, body);
        response.setStatus(status.value());
        model.addAttribute("message", body != null && !body.isBlank() ? body : "La requête n'a pas pu être traitée par le service appelé.");

        if (status == HttpStatus.CONFLICT) {
            return "error/409";
        }
        return "error/400";
    }


    @ExceptionHandler(HttpServerErrorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleHttpServerErrorException(HttpServerErrorException e, Model model) {
        log.error("Un service backend a renvoyé une erreur serveur : {}", e.getMessage());
        model.addAttribute("message", "Un service interne est temporairement indisponible.");
        return "error/500";
    }


    @ExceptionHandler(ResourceAccessException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public String handleResourceAccessException(ResourceAccessException e, Model model) {
        log.error("Un service backend est injoignable : {}", e.getMessage());
        model.addAttribute("message", "Un service est injoignable. Veuillez réessayer dans quelques instants.");
        return "error/503";
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericException(Exception e, Model model) {
        log.error("Erreur inattendue", e);
        model.addAttribute("message", "Une erreur inattendue est survenue.");
        return "error/500";
    }

}
