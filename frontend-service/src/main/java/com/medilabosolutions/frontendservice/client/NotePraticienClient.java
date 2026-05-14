package com.medilabosolutions.frontendservice.client;

import com.medilabosolutions.frontendservice.client.dto.NotePraticienDto;
import com.medilabosolutions.frontendservice.domain.exception.NotePraticienNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
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


    public NotePraticienDto createNote(NotePraticienDto noteDto) {
        return client.post()
                .uri("/api/notes")
                .body(noteDto)
                .retrieve()
                .body(NotePraticienDto.class);
    }


    public List<NotePraticienDto> getNotesByPatient(Long idPatient) {
        return client.get()
                .uri("/api/notes?idPatient={idPatient}", idPatient)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }


    public void deleteNote(String id) {
        try {
            client.delete()
                .uri("/api/notes/{id}", id)
                .retrieve()
                .toBodilessEntity();

        } catch (HttpClientErrorException.NotFound _) {
            throw new NotePraticienNotFoundException(id);
        }
    }

}
