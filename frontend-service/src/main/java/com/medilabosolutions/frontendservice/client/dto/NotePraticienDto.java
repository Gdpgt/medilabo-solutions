package com.medilabosolutions.frontendservice.client.dto;


public record NotePraticienDto(

        String id,

        Long idPatient,

        String nomPatient,

        String note
) {}
