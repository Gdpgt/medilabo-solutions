package com.medilabosolutions.evaluationrisqueservice.domain.service;

import com.medilabosolutions.evaluationrisqueservice.domain.model.NiveauRisque;

public class RegleNiveauRisque {

    public static NiveauRisque evaluate(int agePatient, String genrePatient, int nbDeclencheurs) {
        boolean homme = "M".equals(genrePatient);
        boolean jeune = agePatient <= 30;

        if (!jeune && nbDeclencheurs >= 8) {
            return NiveauRisque.PRECOCE;
        }
        if (jeune && homme && nbDeclencheurs >= 5) {
            return NiveauRisque.PRECOCE;
        }
        if (jeune && !homme && nbDeclencheurs >= 7) {
            return NiveauRisque.PRECOCE;
        }

        if (!jeune && nbDeclencheurs >= 6) {
            return NiveauRisque.DANGER;
        }
        if (jeune && homme && nbDeclencheurs >= 3) {
            return NiveauRisque.DANGER;
        }
        if (jeune && !homme && nbDeclencheurs >= 4) {
            return NiveauRisque.DANGER;
        }

        if (!jeune && nbDeclencheurs >= 2) {
            return NiveauRisque.LIMITE;
        }

        // AUCUN — covers 0-1 for all + young patients under the DANGER threshold (see README)
        return NiveauRisque.AUCUN;
    }

}
