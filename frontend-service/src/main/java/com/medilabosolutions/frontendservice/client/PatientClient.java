package com.medilabosolutions.frontendservice.client;

import com.medilabosolutions.frontendservice.client.dto.PatientDto;
import com.medilabosolutions.frontendservice.domain.exception.PatientNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class PatientClient {

    private final RestClient client;

    public PatientClient(
            @Value("${patientservice.url}") String baseUrl,
            @Value("${medilabo.username}") String username,
            @Value("${medilabo.password}") String password
    ) {
        this.client = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeaders(headers -> headers.setBasicAuth(username, password))
                .build();
    }


    public PatientDto registerPatient(PatientDto patientDto) {
        return client.post()
                .uri("/api/patients")
                .body(patientDto)
                .retrieve()
                .body(PatientDto.class);
    }


    public List<PatientDto> getAllPatients() {
        return client.get()
                .uri("/api/patients")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }


    public PatientDto getPatient(Long idPatient) {
        try {
            return client.get()
                    .uri("/api/patients/{id}", idPatient)
                    .retrieve()
                    .body(PatientDto.class);

        } catch (HttpClientErrorException.NotFound _) {
            throw new PatientNotFoundException(idPatient);
        }
    }


    public PatientDto updatePatient(Long idPatient, PatientDto patientDto) {
        try {
            return client.put()
                    .uri("/api/patients/{id}", idPatient)
                    .body(patientDto)
                    .retrieve()
                    .body(PatientDto.class);

        } catch (HttpClientErrorException.NotFound _) {
            throw new PatientNotFoundException(idPatient);
        }
    }


    public void deletePatient(Long idPatient) {
        try {
            client.delete()
                .uri("/api/patients/{id}", idPatient)
                .retrieve()
                .toBodilessEntity();

        } catch (HttpClientErrorException.NotFound _) {
            throw new PatientNotFoundException(idPatient);
        }
    }

}
