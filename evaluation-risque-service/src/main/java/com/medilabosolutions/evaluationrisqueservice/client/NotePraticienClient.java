package com.medilabosolutions.evaluationrisqueservice.client;

import com.medilabosolutions.evaluationrisqueservice.client.dto.NotePraticienDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class NotePraticienClient {

    private final RestClient client;

    public NotePraticienClient(
            @Value("${notepraticienservice.url}") String baseUrl,
            @Value("${medilabo.username}") String username,
            @Value("${medilabo.password}") String password
    ) {
        this.client = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeaders(headers -> headers.setBasicAuth(username, password))
                .build();
    }


    public List<NotePraticienDto> getNotesByPatient(Long idPatient) {
        return client.get()
                .uri("/api/notes?idPatient={idPatient}", idPatient)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

}
