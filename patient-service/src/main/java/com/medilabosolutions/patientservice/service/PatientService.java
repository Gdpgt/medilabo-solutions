package com.medilabosolutions.patientservice.service;

import com.medilabosolutions.patientservice.domain.exception.PatientNotFoundException;
import com.medilabosolutions.patientservice.domain.model.Patient;
import com.medilabosolutions.patientservice.repository.PatientRepository;
import com.medilabosolutions.patientservice.web.dto.PatientDto;
import com.medilabosolutions.patientservice.web.mapper.PatientMapper;
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
        return patientRepository.save(patient);
    }


    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }


    public Patient getPatient(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException(id));
    }


    @Transactional
    public Patient updatePatient(Long id, PatientDto dto) {
        Patient patient = patientRepository.findById(id).orElseThrow(() -> new PatientNotFoundException(id));
        PatientMapper.updateEntity(patient, dto);
        // .save() is implicit in a transaction : dirty checking by Hibernate at the Commit
        return patient;
    }


    public void deletePatient(Long id) {
        getPatient(id);
        patientRepository.deleteById(id);
    }

}
