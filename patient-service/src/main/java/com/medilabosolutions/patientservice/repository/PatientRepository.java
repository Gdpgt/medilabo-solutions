package com.medilabosolutions.patientservice.repository;

import com.medilabosolutions.patientservice.domain.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PatientRepository extends JpaRepository<Patient, Long> {
}
