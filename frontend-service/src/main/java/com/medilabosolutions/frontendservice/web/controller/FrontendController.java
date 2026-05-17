package com.medilabosolutions.frontendservice.web.controller;

import com.medilabosolutions.frontendservice.client.NotePraticienClient;
import com.medilabosolutions.frontendservice.client.dto.NotePraticienDto;
import com.medilabosolutions.frontendservice.client.dto.PatientDto;
import com.medilabosolutions.frontendservice.service.PatientDetailService;
import com.medilabosolutions.frontendservice.service.PatientService;
import com.medilabosolutions.frontendservice.web.viewmodel.PatientDetailView;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Comparator;
import java.util.List;


@Controller
@RequiredArgsConstructor
@RequestMapping
public class FrontendController {

    private final PatientService patientService;

    private final PatientDetailService patientDetailService;

    private final NotePraticienClient notePraticienClient;


    @GetMapping("/")
    public String home() {
        return "redirect:/patients";
    }


    @GetMapping("/patients")
    public String listePatients(Model model) {
        List<PatientDto> patients = patientService.getAllPatients().stream()
                .sorted(Comparator.comparing(PatientDto::nom, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(PatientDto::prenom, String.CASE_INSENSITIVE_ORDER))
                .toList();

        model.addAttribute("patients", patients);
        return "patients/liste";
    }


    @GetMapping("/patients/nouveau")
    public String formulaireCreation(Model model) {
        model.addAttribute("patientDto", PatientDto.vide());
        model.addAttribute("mode", "creation");
        return "patients/formulaire";
    }


    @PostMapping("/patients")
    public String registerPatient(@Valid @ModelAttribute("patientDto") PatientDto dto,
                                  BindingResult bindingResult,
                                  Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("mode", "creation");
            return "patients/formulaire";
        }

        PatientDto saved = patientService.registerPatient(dto);
        return "redirect:/patients/" + saved.id();
    }


    @GetMapping("/patients/{id}")
    public String detailPatient(@PathVariable Long id, Model model) {
        PatientDetailView detail = patientDetailService.getPatientDetail(id);

        model.addAttribute("detail", detail);
        model.addAttribute("nouvelleNote", new NotePraticienDto(null, id, detail.patient().nom(), "", null));
        return "patients/detail";
    }


    @GetMapping("/patients/{id}/modifier")
    public String formulaireEdition(@PathVariable Long id, Model model) {
        PatientDto patient = patientService.getPatient(id);

        model.addAttribute("patientDto", patient);
        model.addAttribute("mode", "edition");
        return "patients/formulaire";
    }


    @PutMapping("/patients/{id}")
    public String updatePatient(@PathVariable Long id,
                                @Valid @ModelAttribute("patientDto") PatientDto dto,
                                BindingResult bindingResult,
                                Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("mode", "edition");
            return "patients/formulaire";
        }

        patientService.updatePatient(id, dto);
        return "redirect:/patients/" + id;
    }


    @DeleteMapping("/patients/{id}")
    public String deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return "redirect:/patients";
    }


    @PostMapping("/patients/{id}/notes")
    public String createNote(@PathVariable Long id,
                             @Valid @ModelAttribute("nouvelleNote") NotePraticienDto dto,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            PatientDetailView detail = patientDetailService.getPatientDetail(id);
            model.addAttribute("detail", detail);
            return "patients/detail";
        }

        NotePraticienDto patientNamed = new NotePraticienDto(null, id, dto.nomPatient(), dto.note(), null);
        notePraticienClient.createNote(patientNamed);
        return "redirect:/patients/" + id;
    }


    @DeleteMapping("/patients/{id}/notes/{noteId}")
    public String deleteNote(@PathVariable Long id, @PathVariable String noteId) {
        notePraticienClient.deleteNote(noteId);
        return "redirect:/patients/" + id;
    }

}
