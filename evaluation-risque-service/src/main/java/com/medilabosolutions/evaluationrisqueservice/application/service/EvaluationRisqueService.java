package com.medilabosolutions.evaluationrisqueservice.application.service;

import com.medilabosolutions.evaluationrisqueservice.client.NotePraticienClient;
import com.medilabosolutions.evaluationrisqueservice.client.PatientClient;
import com.medilabosolutions.evaluationrisqueservice.client.dto.NotePraticienDto;
import com.medilabosolutions.evaluationrisqueservice.client.dto.PatientDto;
import com.medilabosolutions.evaluationrisqueservice.domain.model.Declencheur;
import com.medilabosolutions.evaluationrisqueservice.domain.model.EvaluationRisque;
import com.medilabosolutions.evaluationrisqueservice.domain.model.NiveauRisque;
import com.medilabosolutions.evaluationrisqueservice.domain.service.RegleNiveauRisque;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EvaluationRisqueService {

    private final PatientClient patientClient;

    private final NotePraticienClient notePraticienClient;

    private static final List<Declencheur> DECLENCHEURS = List.of(Declencheur.values());


    public EvaluationRisque evaluateRisqueByPatient(Long idPatient) {
        PatientDto patientDto = patientClient.getPatient(idPatient);
        Objects.requireNonNull(
                patientDto.dateNaissance(),
                () -> "dateNaissance absente pour patient " + idPatient
        );
        List<NotePraticienDto> notesPraticien = notePraticienClient.getNotesByPatient(idPatient);
        int agePatient = calculateAgePatient(patientDto.dateNaissance());
        int nbDeclencheurs = countDeclencheurs(notesPraticien);

        NiveauRisque niveauRisque = RegleNiveauRisque.evaluate(agePatient, patientDto.genre(), nbDeclencheurs);

        return new EvaluationRisque(idPatient, niveauRisque);
    }


    private int calculateAgePatient(LocalDate dateNaissance) {
        return Period.between(dateNaissance, LocalDate.now()).getYears();
    }


    private int countDeclencheurs(List<NotePraticienDto> notesPraticien) {
        List<String> notesNormalisees = notesPraticien.stream()
                .map(noteDto -> Declencheur.normalize(noteDto.note()))
                .toList();

        return (int) DECLENCHEURS.stream()
                .filter(d -> notesNormalisees.stream().anyMatch(d::matches))
                .count();
    }

}
