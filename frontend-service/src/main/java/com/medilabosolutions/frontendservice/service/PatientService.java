package com.medilabosolutions.frontendservice.service;

import com.medilabosolutions.frontendservice.client.PatientClient;
import com.medilabosolutions.frontendservice.client.dto.PatientDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class PatientService {

    private final PatientClient patientClient;


    @Cacheable(value = "patients", key = "#id")
    public PatientDto getPatient(Long id) {
        return patientClient.getPatient(id);
    }


    @Cacheable(value = "patientsList")
    public List<PatientDto> getAllPatients() {
        return patientClient.getAllPatients();
    }


    @CacheEvict(value = {"patients", "patientsList"}, allEntries = true)
    public PatientDto registerPatient(PatientDto dto) {
        PatientDto saved = patientClient.registerPatient(dto);
        log.info("Patient enregistré id = {}", saved.id());
        return saved;
    }


    @Caching(evict = {
            @CacheEvict(value = "patients", key = "#id"),
            @CacheEvict(value = "patientsList", allEntries = true)
    })
    public PatientDto updatePatient(Long id, PatientDto dto) {
        PatientDto updated = patientClient.updatePatient(id, dto);
        log.info("Patient mis à jour id = {}", id);
        return updated;
    }


    @Caching(evict = {
            @CacheEvict(value = "patients", key = "#id"),
            @CacheEvict(value = "patientsList", allEntries = true)
    })
    public void deletePatient(Long id) {
        patientClient.deletePatient(id);
        log.info("Patient supprimé id = {}", id);
    }

}
