package com.medilabosolutions.notepraticienservice.repository;

import com.medilabosolutions.notepraticienservice.domain.model.NotePraticien;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotePraticienRepository extends MongoRepository<NotePraticien, String> {

    List<NotePraticien> findByIdPatient(Long idPatient);

}
