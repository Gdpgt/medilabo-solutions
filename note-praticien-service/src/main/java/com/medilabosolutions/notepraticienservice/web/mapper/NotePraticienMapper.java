package com.medilabosolutions.notepraticienservice.web.mapper;

import com.medilabosolutions.notepraticienservice.domain.model.NotePraticien;
import com.medilabosolutions.notepraticienservice.web.dto.NotePraticienDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NotePraticienMapper {


    public static NotePraticien toEntity(NotePraticienDto dto) {
        return new NotePraticien().setIdPatient(dto.idPatient())
                .setNomPatient(dto.nomPatient())
                .setNote(dto.note());
    }


    public static NotePraticienDto toDto(NotePraticien note) {
        return new NotePraticienDto(
                note.getId(),
                note.getIdPatient(),
                note.getNomPatient(),
                note.getNote()
        );
    }

}
