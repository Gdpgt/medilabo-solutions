package com.medilabosolutions.evaluationrisqueservice.client;

import com.medilabosolutions.evaluationrisqueservice.client.dto.PatientDto;
import com.medilabosolutions.evaluationrisqueservice.domain.exception.PatientNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

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
}
