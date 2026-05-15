package com.medilabosolutions.frontendservice.service;

import com.medilabosolutions.frontendservice.client.EvaluationRisqueClient;
import com.medilabosolutions.frontendservice.client.NotePraticienClient;
import com.medilabosolutions.frontendservice.client.dto.EvaluationRisqueDto;
import com.medilabosolutions.frontendservice.client.dto.NotePraticienDto;
import com.medilabosolutions.frontendservice.client.dto.PatientDto;
import com.medilabosolutions.frontendservice.web.viewmodel.PatientDetailView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class PatientDetailService {

    private final PatientService patientService;

    private final NotePraticienClient notePraticienClient;

    private final EvaluationRisqueClient evaluationRisqueClient;


    public PatientDetailView getPatientDetail(Long idPatient) {
        PatientDto patient = patientService.getPatient(idPatient);

        List<NotePraticienDto> notes;
        boolean notesDisponibles;

        try {
            notes = notePraticienClient.getNotesByPatient(idPatient);
            notesDisponibles = true;

        } catch (RestClientException e) {
            log.warn("Notes indisponibles pour le patient id = {} : {}", idPatient, e.getMessage());
            notes = List.of();
            notesDisponibles = false;
        }

        EvaluationRisqueDto evaluation;
        boolean evaluationDisponible;

        try {
            evaluation = evaluationRisqueClient.evaluateRisqueByPatient(idPatient);
            evaluationDisponible = true;

        } catch (RestClientException e) {
            log.warn("Évaluation de risque indisponible pour le patient id = {} : {}", idPatient, e.getMessage());
            evaluation = null;
            evaluationDisponible = false;
        }

        return new PatientDetailView(patient, notes, notesDisponibles, evaluation, evaluationDisponible);
    }

}
