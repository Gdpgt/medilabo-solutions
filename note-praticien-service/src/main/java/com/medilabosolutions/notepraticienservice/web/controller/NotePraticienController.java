package com.medilabosolutions.notepraticienservice.web.controller;

import com.medilabosolutions.notepraticienservice.domain.model.NotePraticien;
import com.medilabosolutions.notepraticienservice.service.NotePraticienService;
import com.medilabosolutions.notepraticienservice.web.dto.NotePraticienDto;
import com.medilabosolutions.notepraticienservice.web.mapper.NotePraticienMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notes")
public class NotePraticienController {

    private final NotePraticienService notePraticienService;


    @PostMapping
    public ResponseEntity<NotePraticienDto> createNote(@Valid @RequestBody NotePraticienDto dto) {
        NotePraticien savedNote = notePraticienService.createNote(NotePraticienMapper.toEntity(dto));

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedNote.getId())
                .toUri();

        return ResponseEntity.created(location).body(NotePraticienMapper.toDto(savedNote));
    }


    @GetMapping
    public ResponseEntity<List<NotePraticienDto>> getAllNotes() {
        List<NotePraticienDto> notes = notePraticienService.getAllNotes().stream()
                .map(NotePraticienMapper::toDto)
                .toList();

        return ResponseEntity.ok(notes);
    }


    @GetMapping("/{id}")
    public ResponseEntity<NotePraticienDto> getNote(@PathVariable String id) {
        NotePraticien note = notePraticienService.getNote(id);

        return ResponseEntity.ok(NotePraticienMapper.toDto(note));
    }


    @PutMapping("/{id}")
    public ResponseEntity<NotePraticienDto> updateNote(@PathVariable String id, @Valid @RequestBody NotePraticienDto dto) {
        NotePraticien updatedNote = notePraticienService.updateNote(id, NotePraticienMapper.toEntity(dto));

        return ResponseEntity.ok(NotePraticienMapper.toDto(updatedNote));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable String id) {
        notePraticienService.deleteNote(id);

        return ResponseEntity.noContent().build();
    }

}
