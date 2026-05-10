package com.medilabosolutions.evaluationrisqueservice.domain.model;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum Declencheur {

    HEMOGLOBINE_A1C("hémoglobine a1c"),
    MICROALBUMINE("microalbumine"),
    TAILLE("taille"),
    POIDS("poids"),
    FUMEUR("fumeur"),
    FUMEUSE("fumeuse"),
    // "contains("anorma")" matches "anormal", "anormale", "anormales", "anormaux" without needing to enumerate them
    ANORMAL("anorma"),
    CHOLESTEROL("cholesterol"),
    VERTIGES("vertige"),
    RECHUTE("rechute"),
    REACTION("reaction"),
    ANTICORPS("anticorps");

    private final Set<String> motsCles;

    Declencheur(String... motsCles) {
        this.motsCles = Arrays.stream(motsCles)
                .map(Declencheur::normalize)
                .collect(Collectors.toUnmodifiableSet());
    }


    public boolean matches(String noteNormalisee) {
        return motsCles.stream().anyMatch(noteNormalisee::contains);
    }


    public static String normalize(String texte) {
        return Normalizer.normalize(texte, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase();
    }
}
