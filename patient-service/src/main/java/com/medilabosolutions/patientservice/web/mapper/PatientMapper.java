package com.medilabosolutions.patientservice.web.mapper;

import com.medilabosolutions.patientservice.domain.model.Patient;
import com.medilabosolutions.patientservice.web.dto.PatientDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PatientMapper {

    
    public static Patient toEntity(PatientDto dto) {
        return new Patient().setNom(dto.nom())
                .setPrenom(dto.prenom())
                .setDateNaissance(dto.dateNaissance())
                .setGenre(dto.genre())
                .setAdresse(dto.adresse())
                .setTelephone(dto.telephone());
    }
    
    
    public static void updateEntity(Patient patient, PatientDto dto) {
        patient.setNom(dto.nom())
                .setPrenom(dto.prenom())
                .setDateNaissance(dto.dateNaissance())
                .setGenre(dto.genre())
                .setAdresse(dto.adresse())
                .setTelephone(dto.telephone());
    }


    public static PatientDto toDto(Patient patient) {
        return new PatientDto(patient.getId(),
                patient.getNom(),
                patient.getPrenom(),
                patient.getDateNaissance(),
                patient.getGenre(),
                patient.getAdresse(),
                patient.getTelephone());
    }

}
