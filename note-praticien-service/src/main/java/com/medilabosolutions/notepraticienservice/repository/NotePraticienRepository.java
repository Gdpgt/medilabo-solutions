package com.medilabosolutions.notepraticienservice.repository;

import com.medilabosolutions.notepraticienservice.domain.model.NotePraticien;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotePraticienRepository extends MongoRepository<NotePraticien, String> {
}
