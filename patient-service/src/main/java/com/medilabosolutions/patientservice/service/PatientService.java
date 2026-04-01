package com.medilabosolutions.patientservice.service;

import com.medilabosolutions.patientservice.domain.exception.PatientNotFoundException;
import com.medilabosolutions.patientservice.domain.model.Patient;
import com.medilabosolutions.patientservice.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class PatientService {

    private final PatientRepository patientRepository;


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


    public Patient updatePatient(Patient patient) {
        return patientRepository.save(patient);
    }


    public void deletePatient(Long id) {
        getPatient(id);
        patientRepository.deleteById(id);
    }

}
