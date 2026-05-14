package com.medilabosolutions.evaluationrisqueservice.web.controller;

import com.medilabosolutions.evaluationrisqueservice.application.service.EvaluationRisqueService;
import com.medilabosolutions.evaluationrisqueservice.domain.model.EvaluationRisque;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/evaluation-risque")
public class EvaluationRisqueController {

    private final EvaluationRisqueService evaluationRisqueService;


    @GetMapping("/{idPatient}")
    public ResponseEntity<EvaluationRisque> evaluateRisqueByPatient(@PathVariable Long idPatient) {
        EvaluationRisque risque = evaluationRisqueService.evaluateRisqueByPatient(idPatient);
        return ResponseEntity.ok(risque);
    }

}
