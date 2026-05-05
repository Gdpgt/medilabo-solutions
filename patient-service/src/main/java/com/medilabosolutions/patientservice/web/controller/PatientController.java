package com.medilabosolutions.patientservice.web.controller;

import org.springframework.web.bind.annotation.*;

import com.medilabosolutions.patientservice.web.mapper.PatientMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import com.medilabosolutions.patientservice.service.PatientService;

import com.medilabosolutions.patientservice.domain.model.Patient;
import com.medilabosolutions.patientservice.web.dto.PatientDto;
import jakarta.validation.Valid;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;


    @PostMapping
    public ResponseEntity<PatientDto> registerPatient(@Valid @RequestBody PatientDto dto) {
        Patient returnedPatient = patientService.registerPatient(PatientMapper.toEntity(dto));

        PatientDto returnedDto = PatientMapper.toDto(returnedPatient);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(returnedPatient.getId())
                .toUri();

        return ResponseEntity.created(location).body(returnedDto);
    }
    
    
    @GetMapping
    public ResponseEntity<List<PatientDto>> getAllPatients() {
        List<PatientDto> patients = patientService.getAllPatients().stream()
                .map(PatientMapper::toDto)
                .toList();

        return ResponseEntity.ok(patients);
    }


    @GetMapping("/{id}")
    public ResponseEntity<PatientDto> getPatient(@PathVariable Long id) {
        Patient patient = patientService.getPatient(id);

        return ResponseEntity.ok(PatientMapper.toDto(patient));
    }


    @PutMapping("/{id}")
    public ResponseEntity<PatientDto> updatePatient(@PathVariable Long id, @Valid @RequestBody PatientDto dto) {
        Patient updatedPatient = patientService.updatePatient(id, PatientMapper.toEntity(dto));

        return ResponseEntity.ok(PatientMapper.toDto(updatedPatient));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);

        return ResponseEntity.noContent().build();
    }

}
