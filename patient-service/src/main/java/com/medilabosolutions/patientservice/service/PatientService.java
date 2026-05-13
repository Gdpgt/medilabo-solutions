package com.medilabosolutions.patientservice.service;

import com.medilabosolutions.patientservice.domain.exception.PatientNotFoundException;
import com.medilabosolutions.patientservice.domain.model.Patient;
import com.medilabosolutions.patientservice.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class PatientService {

    private final PatientRepository patientRepository;

    // Vérification de l'existence du patient gérée au niveau de la base de donnée via contrainte.
    // Géré dans le global handler via handleDataIntegrationViolationException.
    public Patient registerPatient(Patient patient) {
        Patient saved = patientRepository.save(patient);
        log.info("Patient enregistré id = {}", saved.getId());
        return saved;
    }


    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }


    public Patient getPatient(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException(id));
    }


    @Transactional
    public Patient updatePatient(Long id, Patient updates) {
        // .save() is implicit in a transaction : dirty checking by Hibernate at the Commit
        Patient updated = getPatient(id).merge(updates);
        log.info("Patient mis à jour id = {}", id);
        return updated;
    }


    public void deletePatient(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new PatientNotFoundException(id);
        }

        patientRepository.deleteById(id);
        log.info("Patient supprimé id = {}", id);
    }

}
