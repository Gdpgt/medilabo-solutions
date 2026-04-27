package com.medilabosolutions.notepraticienservice.web.mapper;

import com.medilabosolutions.notepraticienservice.domain.model.NotePraticien;
import com.medilabosolutions.notepraticienservice.web.dto.NotePraticienDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NotePraticienMapper {


    public static void updateEntity(NotePraticien note, NotePraticienDto dto) {
        note.setIdPatient(dto.idPatient())
            .setNomPatient(dto.nomPatient())
            .setNote(dto.note());
    }

}
