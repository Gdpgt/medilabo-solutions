package com.medilabosolutions.frontendservice.client;

import com.medilabosolutions.frontendservice.client.dto.EvaluationRisqueDto;
import com.medilabosolutions.frontendservice.domain.exception.PatientNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;


@Component
public class EvaluationRisqueClient {

    private final RestClient client;

    public EvaluationRisqueClient(
            @Value("${evaluationrisqueservice.url}") String baseUrl,
            @Value("${medilabo.username}") String username,
            @Value("${medilabo.password}") String password
    ) {
        this.client = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeaders(headers -> headers.setBasicAuth(username, password))
                .build();
    }


    public EvaluationRisqueDto evaluateRisqueByPatient(Long idPatient) {
        try {
            return client.get()
                    .uri("/api/evaluation-risque/{idPatient}", idPatient)
                    .retrieve()
                    .body(EvaluationRisqueDto.class);

        } catch (HttpClientErrorException.NotFound _) {
            throw new PatientNotFoundException(idPatient);
        }
    }

}
