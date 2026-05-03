package com.medilabosolutions.notepraticienservice.service;

import com.medilabosolutions.notepraticienservice.domain.exception.NoteNotFoundException;
import com.medilabosolutions.notepraticienservice.domain.model.NotePraticien;
import com.medilabosolutions.notepraticienservice.repository.NotePraticienRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class NotePraticienService {

    private final NotePraticienRepository notePraticienRepository;


    public NotePraticien createNote(NotePraticien note) {
        NotePraticien saved = notePraticienRepository.save(note);
        log.info("Note praticien enregistrée id={}", saved.getId());
        return saved;
    }


    public List<NotePraticien> getAllNotes() {
        return notePraticienRepository.findAll();
    }


    public NotePraticien getNote(String id) {
        return notePraticienRepository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException(id));
    }


    public NotePraticien updateNote(String id, NotePraticien updates) {
        NotePraticien note = getNote(id);
        NotePraticien updated = notePraticienRepository.save(note.merge(updates));
        log.info("Note praticien mise à jour id={}", id);
        return updated;
    }


    public void deleteNote(String id) {
        if (!notePraticienRepository.existsById(id)) {
            throw new NoteNotFoundException(id);
        }

        notePraticienRepository.deleteById(id);
        log.info("Note praticien supprimée id={}", id);
    }

}
