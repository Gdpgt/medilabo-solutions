package com.medilabosolutions.notepraticienservice.service;

import com.medilabosolutions.notepraticienservice.domain.exception.NoteNotFoundException;
import com.medilabosolutions.notepraticienservice.domain.model.NotePraticien;
import com.medilabosolutions.notepraticienservice.repository.NotePraticienRepository;
import com.medilabosolutions.notepraticienservice.web.dto.NotePraticienDto;
import com.medilabosolutions.notepraticienservice.web.mapper.NotePraticienMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class NotePraticienService {

    private final NotePraticienRepository notePraticienRepository;


    public NotePraticien createNote(NotePraticien note) {
        return notePraticienRepository.save(note);
    }


    public List<NotePraticien> getAllNotes() {
        return notePraticienRepository.findAll();
    }


    public NotePraticien getNote(String id) {
        return notePraticienRepository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException(id));
    }


    public NotePraticien updateNote(String id, NotePraticienDto dto) {
        NotePraticien note = getNote(id);
        NotePraticienMapper.updateEntity(note, dto);
        return notePraticienRepository.save(note);
    }


    public void deleteNote(String id) {
        if (!notePraticienRepository.existsById(id)) {
            throw new NoteNotFoundException(id);
        }

        notePraticienRepository.deleteById(id);
    }

}
