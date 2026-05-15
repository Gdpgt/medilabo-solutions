package com.medilabosolutions.frontendservice.web.viewmodel;

import com.medilabosolutions.frontendservice.client.dto.EvaluationRisqueDto;
import com.medilabosolutions.frontendservice.client.dto.NotePraticienDto;
import com.medilabosolutions.frontendservice.client.dto.PatientDto;

import java.util.List;


public record PatientDetailView(

        PatientDto patient,

        List<NotePraticienDto> notes,

        boolean notesDisponibles,

        EvaluationRisqueDto evaluation,

        boolean evaluationDisponible
) {}
